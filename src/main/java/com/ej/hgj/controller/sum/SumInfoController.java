package com.ej.hgj.controller.sum;

import com.alibaba.fastjson.JSON;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.sum.SumFile;
import com.ej.hgj.entity.sum.SumInfo;
import com.ej.hgj.entity.user.UserLog;
import com.ej.hgj.service.sum.SumFileService;
import com.ej.hgj.service.sum.SumInfoService;
import com.ej.hgj.service.user.UserLogService;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/sum/info")
public class SumInfoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private SumInfoService sumInfoService;

    @Autowired
    private SumFileService sumFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLogService userLogService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           SumInfo sumInfo){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        if(sumInfo != null && sumInfo.getFilesCode() != null){
            sumInfo.setFilesCode(sumInfo.getFilesCode().replace("/",""));
        }
        List<SumInfo> list = sumInfoService.getList(sumInfo);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<SumInfo> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<SumInfo> entityPageInfo = new PageInfo<>();
            entityPageInfo.setList(new ArrayList<>());
            entityPageInfo.setTotal(pageInfo.getTotal());
            entityPageInfo.setPageNum(page);
            entityPageInfo.setPageSize(limit);
            map.put("pageInfo",entityPageInfo);
        }else {
            map.put("pageInfo",pageInfo);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(HttpServletRequest request, @RequestBody SumInfo sumInfo){
        String userId = userService.getById(TokenUtils.getUserId(request)).getId();

        UserLog userLog = new UserLog();
        userLog.setId(System.currentTimeMillis()+"");
        userLog.setOperateUrl("/sum/info/save");
        userLog.setOperateMenu("汇总列表");
        userLog.setUserId(userId);
        userLog.setCreateTime(new Date());
        userLog.setUpdateTime(new Date());
        userLog.setDeleteFlag(0);

        sumInfo.setUpdateTime(new Date());
        sumInfo.setUpdateBy(userId);
        AjaxResult ajaxResult = new AjaxResult();
        if(sumInfo.getId() != null){
            sumInfoService.update(sumInfo);
            userLog.setOperateContent("汇总信息编辑");
            userLog.setOperateId(sumInfo.getId());
            userLogService.save(userLog);
        }else{
            sumInfo.setId(System.currentTimeMillis()+"");
            sumInfo.setFileDate(new Date());
            sumInfo.setCreateBy(userId);
            sumInfo.setCreateTime(new Date());
            sumInfo.setDeleteFlag(0);
            sumInfoService.save(sumInfo);

            userLog.setOperateContent("汇总信息新增");
            userLog.setOperateId(sumInfo.getId());
            userLogService.save(userLog);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(HttpServletRequest request, @RequestParam(required=false, value = "id") String id){
        String userId = userService.getById(TokenUtils.getUserId(request)).getId();
        AjaxResult ajaxResult = new AjaxResult();
        sumInfoService.delete(id);

        UserLog userLog = new UserLog();
        userLog.setId(System.currentTimeMillis()+"");
        userLog.setOperateUrl("/sum/info/delete");
        userLog.setOperateMenu("汇总列表");
        userLog.setUserId(userId);
        userLog.setCreateTime(new Date());
        userLog.setUpdateTime(new Date());
        userLog.setDeleteFlag(0);
        userLog.setOperateContent("汇总信息删除");
        userLog.setOperateId(id);
        userLogService.save(userLog);

        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    // 上传文件
    @PostMapping("/file/upload")
    public AjaxResult upload(HttpServletRequest request, @RequestParam("file") MultipartFile file,String sumId,String dirNum) throws IOException {
        String userId = userService.getById(TokenUtils.getUserId(request)).getId();
        AjaxResult ajaxResult = new AjaxResult();
        sumInfoService.uploadFile(file,sumId,dirNum,userId);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    // 下载文件
    @PostMapping("/file/download")
    public void download(HttpServletResponse response, String fileId) {
        SumFile sumFile = sumFileService.findById(fileId);
//        try {
//            response.setContentType("application/vnd.ms-excel");
//            //1.输入流，通过输入流读取上传的文件
//            FileInputStream fileInputStream = new FileInputStream(new File(sumFile.getFileUrl()));
//            //2.输出流，通过输出流将文件写回了浏览器，在浏览器展示
//            ServletOutputStream outputStream = response.getOutputStream();
//            //4.将文件读取进bytes数组，通过输出流写回浏览器
////            int len = 0;
////            byte[] bytes = new byte[1024];
////            while ((len = fileInputStream.read(bytes)) != -1) {
////                outputStream.write(bytes, 0, len);
////            }
//            IOUtils.copy(fileInputStream,outputStream);
//            outputStream.flush();
//            //5.关闭资源
//            outputStream.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        OutputStream os = null;
        InputStream is = null;
        try {
            InputStream in = new FileInputStream(new File(sumFile.getFileUrl()));
            // 取得输出流
            os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment");
            //复制
            IOUtils.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            System.out.println("下载文件失败，" + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            }
        }
    }

    // excel导出
    @GetMapping("/excel/download")
    public void excelDownload(HttpServletResponse response, HttpServletRequest request, SumInfo sumInfo) throws IOException {
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //建立sheet对象
        HSSFSheet sheet=wb.createSheet("汇总列表");
        //在sheet里创建第一行，参数为行索引
        HSSFRow row1=sheet.createRow(0);
        //创建单元格
        HSSFCell cell=row1.createCell(0);
        //设置单元格内容
        cell.setCellValue("汇总列表");
        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));

        //在sheet里创建第二行
        HSSFRow row2=sheet.createRow(1);
        //创建单元格并设置单元格内容
        row2.createCell(0).setCellValue("公司代码");
        row2.createCell(1).setCellValue("入档日期");
        row2.createCell(2).setCellValue("项目代码");
        row2.createCell(3).setCellValue("所属部门");
        row2.createCell(4).setCellValue("档案类型代码");
        row2.createCell(5).setCellValue("租户楼层");
        row2.createCell(6).setCellValue("租户单元");
        row2.createCell(7).setCellValue("存放柜号");
        row2.createCell(8).setCellValue("存放层数");
        row2.createCell(9).setCellValue("存放盒号");
        row2.createCell(10).setCellValue("文件编号");
        row2.createCell(11).setCellValue("文件数量");
        row2.createCell(12).setCellValue("原件份数");
        row2.createCell(13).setCellValue("原件页数");
        row2.createCell(14).setCellValue("复印件份数");
        row2.createCell(15).setCellValue("复印件页数");
        row2.createCell(16).setCellValue("文件密级");
        row2.createCell(17).setCellValue("合同开始日");
        row2.createCell(18).setCellValue("合同结束日");
        row2.createCell(19).setCellValue("创建时间");
        row2.createCell(20).setCellValue("更新时间");
        List<SumInfo> list = sumInfoService.getList(sumInfo);
        if(!list.isEmpty()) {
            for (int i = 0; i <= list.size() - 1; i++) {
                HSSFRow row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(list.get(i).getCompanyCode()==null?"":list.get(i).getCompanyCode());
                row.createCell(1).setCellValue(list.get(i).getFileDate()==null?null:list.get(i).getFileDate());
                row.createCell(2).setCellValue(list.get(i).getProjectCode()==null?"":list.get(i).getProjectCode());
                row.createCell(3).setCellValue(list.get(i).getAffDept()==null?"":list.get(i).getAffDept());
                row.createCell(4).setCellValue(list.get(i).getArchiveTypeCode()==null?"":list.get(i).getArchiveTypeCode());
                row.createCell(5).setCellValue(list.get(i).getTenantFloor()==null?"":list.get(i).getTenantFloor());
                row.createCell(6).setCellValue(list.get(i).getTenantUnit()==null?"":list.get(i).getTenantUnit());
                row.createCell(7).setCellValue(list.get(i).getDepositCabinetNum()==null?"":list.get(i).getDepositCabinetNum());
                row.createCell(8).setCellValue(list.get(i).getDepositNum()==null?"":list.get(i).getDepositNum());
                row.createCell(9).setCellValue(list.get(i).getDepositBoxNum()==null?"":list.get(i).getDepositBoxNum());
                row.createCell(10).setCellValue(list.get(i).getCompanyCode()==null?"":list.get(i).getCompanyCode()
                        + "/" + list.get(i).getProjectCode()==null?"":list.get(i).getProjectCode()
                        + "/" + list.get(i).getArchiveTypeCode()==null?"":list.get(i).getArchiveTypeCode()
                        + "/" + list.get(i).getTenantFloor()==null?"":list.get(i).getTenantFloor()
                        + "/" + list.get(i).getTenantUnit()==null?"":list.get(i).getTenantUnit());
                row.createCell(11).setCellValue(list.get(i).getFileNum());
                row.createCell(12).setCellValue(list.get(i).getScriptNum());
                row.createCell(13).setCellValue(list.get(i).getScriptPage());
                row.createCell(14).setCellValue(list.get(i).getCopyNum());
                row.createCell(15).setCellValue(list.get(i).getCopyPage());
                row.createCell(16).setCellValue(list.get(i).getFileSecLevel());
                row.createCell(17).setCellValue(list.get(i).getContractStartDate());
                row.createCell(18).setCellValue(list.get(i).getContractEndDate());
                row.createCell(19).setCellValue(list.get(i).getCreateTime());
                row.createCell(20).setCellValue(list.get(i).getUpdateTime());
            }
        }
        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        wb.write(output);
        try {
            // 取得输出流
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment");
            //复制
            output.flush();
        } catch (IOException e) {
            System.out.println("下载文件失败，" + e.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
            }
        }
    }

}

