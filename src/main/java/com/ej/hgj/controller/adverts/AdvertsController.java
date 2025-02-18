package com.ej.hgj.controller.adverts;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.adverts.AdvertsDaoMapper;
import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.file.FileMessage;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.file.FileSendClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/adverts")
public class AdvertsController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.path.remote}")
    private String uploadPathRemote;

    @Autowired
    private AdvertsDaoMapper advertsDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Adverts adverts){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(adverts.getEndTime())){
            adverts.setEndTime(adverts.getEndTime() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Adverts> list = advertsDaoMapper.getList(adverts);
        for(Adverts a : list){
            if(StringUtils.isNotBlank(a.getImgPath())){
//                String base64Image = "";
//                try {
//                    BufferedImage image = ImageIO.read(new File(a.getImgPath()));
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    ImageIO.write(image, "png", baos);
//                    byte[] imageBytes = baos.toByteArray();
//                    base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                a.setImgPath(base64Image);

                // 远程文件读取
                try {
                    String base64Image = "";
                    // 获取文件路径
                    String imgPath = a.getImgPath();
                    // 拼接远程文件地址
                    String fileUrl = Constant.REMOTE_FILE_URL + "/" + imgPath;
                    BufferedImage image = FileSendClient.downloadFileBufferedImage(fileUrl);
                    if(image != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        byte[] imageBytes = baos.toByteArray();
                        base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
                        a.setImgPath(base64Image);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Adverts> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Adverts> entityPageInfo = new PageInfo<>();
            entityPageInfo.setList(new ArrayList<>());
            entityPageInfo.setTotal(pageInfo.getTotal());
            entityPageInfo.setPageNum(page);
            entityPageInfo.setPageSize(limit);
            map.put("pageInfo",entityPageInfo);
        }else {
            map.put("pageInfo",pageInfo);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Adverts adverts){
        AjaxResult ajaxResult = new AjaxResult();
        if(adverts.getId() != null){
            adverts.setImgPath(null);
            advertsDaoMapper.update(adverts);
        }else{
            adverts.setId(TimestampGenerator.generateSerialNumber());
            adverts.setIsShow(1);
            adverts.setUpdateTime(new Date());
            adverts.setCreateTime(new Date());
            adverts.setDeleteFlag(0);
            advertsDaoMapper.save(adverts);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    // 上传文件
    @PostMapping("/file/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file, String advertsId) throws IOException {
        AjaxResult ajaxResult = new AjaxResult();
        //获取文件名
        String fileName = file.getOriginalFilename();
        int lastIndex = fileName.lastIndexOf('.');
        String fileExtension = fileName.substring(lastIndex + 1);
        // 远程文件夹地址
        String folderPathRemote = uploadPathRemote+"/adverts";
        // 远程文件地址
        String filePathRemote = folderPathRemote + "/" + advertsId + "." + fileExtension;
        Adverts adverts = advertsDaoMapper.getById(advertsId);
        adverts.setImgPath(filePathRemote);
        adverts.setUpdateTime(new Date());
        advertsDaoMapper.update(adverts);
        // 发送文件
        try {
            // 本地文件地址
            String imgPath = uploadFile(file, fileExtension, advertsId);
            // 读取文件
            byte[] fileBytes = Files.readAllBytes(Paths.get(imgPath));
            // 创建文件消息对象
            FileMessage fileMessage = new FileMessage(folderPathRemote, advertsId+"." + fileExtension, fileBytes);
            FileSendClient.sendFile(fileMessage);
        } catch (Exception e) {
            logger.info("Error in Send: " + e.getMessage());
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    public String uploadFile(MultipartFile file, String fileExtension, String id) {
        String path = "";
        if (file != null) {
            //目录不存在则直接创建
            File filePath = new File(uploadPath + "/adverts");
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            //创建年月日文件夹
            //File ymdFile = new File(uploadPath + "/adverts" + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()));
            //目录不存在则直接创建
//            if (!ymdFile.exists()) {
//                ymdFile.mkdirs();
//            }
            //path = ymdFile.getPath() + "/" + id+"."+fileExtension;
            path = uploadPath + "/adverts" + "/" + id +"."+ fileExtension;
            try {
                file.transferTo(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    @RequestMapping(value = "/isShow",method = RequestMethod.GET)
    public AjaxResult isShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        Adverts adverts = advertsDaoMapper.getById(id);
        advertsDaoMapper.notIsShowByProNum(adverts.getProNum());
        advertsDaoMapper.isShow(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/notIsShow",method = RequestMethod.GET)
    public AjaxResult notIsShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        advertsDaoMapper.notIsShow(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        // 删除数据
        advertsDaoMapper.delete(id);
        // 删除图片
        Adverts adverts = advertsDaoMapper.getById(id);
        if(StringUtils.isNotBlank(adverts.getImgPath())){
            File file = new File(adverts.getImgPath());
            file.delete();
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}

