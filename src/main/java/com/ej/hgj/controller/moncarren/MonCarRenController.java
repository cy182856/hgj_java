package com.ej.hgj.controller.moncarren;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.carrenew.CarRenewOrderDaoMapper;
import com.ej.hgj.dao.moncarren.MonCarRenOrderDaoMapper;
import com.ej.hgj.entity.carrenew.CarRenewOrder;
import com.ej.hgj.entity.moncarren.MonCarRenOrder;
import com.ej.hgj.entity.user.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/monCarRen")
public class MonCarRenController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MonCarRenOrderDaoMapper monCarRenOrderDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           MonCarRenOrder monCarRenOrder){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(monCarRenOrder.getEndDate())){
            monCarRenOrder.setEndDate(monCarRenOrder.getEndDate() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<MonCarRenOrder> list = monCarRenOrderDaoMapper.getList(monCarRenOrder);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<MonCarRenOrder> pageInfo = new PageInfo<>(list);
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
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

}
