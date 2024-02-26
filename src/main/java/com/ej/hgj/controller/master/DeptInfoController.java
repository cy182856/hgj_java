package com.ej.hgj.controller.master;

import com.alibaba.fastjson.JSON;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.master.DeptInfo;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.master.DeptInfoService;
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
@RequestMapping("/dept/info")
public class DeptInfoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeptInfoService deptInfoService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           DeptInfo deptInfo){

        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<DeptInfo> list = deptInfoService.getList(deptInfo);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<DeptInfo> pageInfo = new PageInfo<>(list);
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
    public AjaxResult select(DeptInfo deptInfo){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<DeptInfo> list = deptInfoService.getList(deptInfo);
        logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody DeptInfo deptInfo){
        AjaxResult ajaxResult = new AjaxResult();
        if(deptInfo.getId() != null){
            deptInfoService.update(deptInfo);
        }else{
            deptInfo.setId(System.currentTimeMillis()+"");
            deptInfo.setUpdateTime(new Date());
            deptInfo.setCreateTime(new Date());
            deptInfo.setDeleteFlag(0);
            deptInfoService.save(deptInfo);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        deptInfoService.delete(id);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }
}
