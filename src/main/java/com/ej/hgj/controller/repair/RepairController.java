package com.ej.hgj.controller.repair;


import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.repair.RepairLogDaoMapper;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.repair.RepairLog;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.service.house.HouseService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
import com.ej.hgj.sy.dao.house.SyHouseDaoMapper;
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
@RequestMapping("/repair")
public class RepairController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepairLogDaoMapper repairLogDaoMapper;


    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           RepairLog repairLog){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        if(StringUtils.isNotBlank(repairLog.getEndTime())){
            repairLog.setEndTime(repairLog.getEndTime() + " 23:59:59");
        }
        List<RepairLog> list = repairLogDaoMapper.getListAll(repairLog);
        for(RepairLog rep : list){
            if("WOSta_Sub".equals(rep.getRepairStatus())){
                rep.setRepairStatusName("已提交");
            }else if("WOSta_Proc".equals(rep.getRepairStatus())){
                rep.setRepairStatusName("处理中");
            }else if("WOSta_Finish".equals(rep.getRepairStatus())){
                rep.setRepairStatusName("已完工");
            }else if("WOSta_Visit".equals(rep.getRepairStatus())){
                rep.setRepairStatusName("已回访");
            }else if("WOSta_Close".equals(rep.getRepairStatus())){
                rep.setRepairStatusName("已关闭");
            }
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<RepairLog> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<HgjHouse> entityPageInfo = new PageInfo<>();
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
        return ajaxResult;
    }

}
