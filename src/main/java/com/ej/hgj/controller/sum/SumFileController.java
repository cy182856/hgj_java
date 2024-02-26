package com.ej.hgj.controller.sum;

import com.alibaba.fastjson.JSON;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.sum.SumFile;
import com.ej.hgj.entity.sum.SumInfo;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserLog;
import com.ej.hgj.service.sum.SumFileService;
import com.ej.hgj.service.sum.SumInfoService;
import com.ej.hgj.service.user.UserLogService;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/sum/file")
public class SumFileController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SumFileService sumFileService;

    @Autowired
    private SumInfoService sumInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLogService userLogService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           SumFile sumFile){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<SumFile> list = sumFileService.getList(sumFile);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<SumFile> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<User> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(@RequestParam(required=false, value = "id") String id){
        SumInfo sumInfo = sumInfoService.findById(id);
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        SumFile sumFile = new SumFile();
        sumFile.setSumId(id);
        List<SumFile> list = sumFileService.getList(sumFile);
        for (SumFile sf : list){
            sf.setPositionNum(sumInfo.getDepositCabinetNum()+sumInfo.getDepositNum()+sumInfo.getDepositBoxNum()+sf.getDirNum());
        }
        logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody SumFile sumFile){
        AjaxResult ajaxResult = new AjaxResult();
        if(sumFile.getId() != null){
            sumFileService.update(sumFile);
        }else{
            sumFile.setId(System.currentTimeMillis()+"");
            sumFile.setUpdateTime(new Date());
            sumFile.setCreateTime(new Date());
            sumFile.setDeleteFlag(0);
            sumFileService.save(sumFile);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(HttpServletRequest request, @RequestParam(required=false, value = "id") String id){
        String userId = userService.getById(TokenUtils.getUserId(request)).getId();
        AjaxResult ajaxResult = new AjaxResult();
        // 查询附件信息
        SumFile sumFile = sumFileService.findById(id);
        // 删除附件
        sumFileService.delete(id);
        //更新文件数量
        SumInfo sumInfo = sumInfoService.findById(sumFile.getSumId());
        Integer fileNum = sumInfo.getFileNum();
        if (fileNum > 0){
            sumInfo.setFileNum(fileNum - 1);
            sumInfoService.update(sumInfo);
        }

        UserLog userLog = new UserLog();
        userLog.setId(System.currentTimeMillis()+"");
        userLog.setOperateUrl("/sum/file/delete");
        userLog.setOperateMenu("汇总列表");
        userLog.setUserId(userId);
        userLog.setCreateTime(new Date());
        userLog.setUpdateTime(new Date());
        userLog.setDeleteFlag(0);
        userLog.setOperateContent("附件删除");
        userLog.setOperateId(id);
        userLogService.save(userLog);

        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }
}
