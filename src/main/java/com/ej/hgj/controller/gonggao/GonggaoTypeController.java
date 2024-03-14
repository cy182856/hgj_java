package com.ej.hgj.controller.gonggao;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.gonggao.GonggaoTypeDaoMapper;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.utils.TimestampGenerator;
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
@RequestMapping("/gonggaoType")
public class GonggaoTypeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GonggaoTypeDaoMapper gonggaoTypeDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           GonggaoType gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<GonggaoType> list = gonggaoTypeDaoMapper.getList(gonggao);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<GonggaoType> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<GonggaoType> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(GonggaoType gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<GonggaoType> list = gonggaoTypeDaoMapper.getList(gonggao);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody GonggaoType gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        if(gonggao.getId() != null){
            gonggaoTypeDaoMapper.update(gonggao);
        }else{
            gonggao.setId(TimestampGenerator.generateSerialNumber());
            gonggao.setUpdateTime(new Date());
            gonggao.setCreateTime(new Date());
            gonggao.setDeleteFlag(0);
            gonggaoTypeDaoMapper.save(gonggao);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        gonggaoTypeDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
