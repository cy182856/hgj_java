package com.ej.hgj.controller.master;

import com.alibaba.fastjson.JSON;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.master.ArchiveCabinet;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.master.ArchiveCabinetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/archive/cabinet")
public class ArchiveCabinetController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArchiveCabinetService archiveCabinetService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           ArchiveCabinet archiveCabinet){

        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<ArchiveCabinet> list = archiveCabinetService.getList(archiveCabinet);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<ArchiveCabinet> pageInfo = new PageInfo<>(list);
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
    public AjaxResult select(ArchiveCabinet archiveCabinet){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<ArchiveCabinet> list = archiveCabinetService.getList(archiveCabinet);
        logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody ArchiveCabinet archiveCabinet){
        AjaxResult ajaxResult = new AjaxResult();
        if(archiveCabinet.getId() != null){
            archiveCabinetService.update(archiveCabinet);
        }else{
            archiveCabinet.setId(System.currentTimeMillis()+"");
            archiveCabinet.setUpdateTime(new Date());
            archiveCabinet.setCreateTime(new Date());
            archiveCabinet.setDeleteFlag(0);
            archiveCabinetService.save(archiveCabinet);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        archiveCabinetService.delete(id);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }
}
