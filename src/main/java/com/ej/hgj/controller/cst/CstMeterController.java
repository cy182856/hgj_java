package com.ej.hgj.controller.cst;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.cst.CstMeterDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cst.CstMeter;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.tag.OneTreeData;
import com.ej.hgj.entity.tag.ThreeChildren;
import com.ej.hgj.entity.tag.TwoChildren;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.service.role.RoleService;
import com.ej.hgj.service.user.UserRoleService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/cst/meter")
public class CstMeterController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstMeterDaoMapper cstMeterDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           CstMeter cstMeter){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<CstMeter> list = cstMeterDaoMapper.getList(cstMeter);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<CstMeter> pageInfo = new PageInfo<>(list);
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

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody CstMeter cstMeter){
        AjaxResult ajaxResult = new AjaxResult();
        CstMeter cm = cstMeterDaoMapper.getByCstCodeAndUserId(cstMeter.getCstCode(), cstMeter.getUserId());
        if(cstMeter.getId() != null){
            CstMeter cstMeterById = cstMeterDaoMapper.getById(cstMeter.getId());
            if(cm != null && !cstMeterById.getUserId().equals(cstMeter.getUserId())){
                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
                ajaxResult.setMessage("不能重复添加");
                return ajaxResult;
            }
            cstMeterDaoMapper.update(cstMeter);
        }else{
            if(cm != null){
                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
                ajaxResult.setMessage("不能重复添加");
                return ajaxResult;
            }
            cstMeter.setId(TimestampGenerator.generateSerialNumber());
            cstMeter.setProNum("10000");
            cstMeter.setUserId(cstMeter.getUserId());
            cstMeter.setCstCode(cstMeter.getCstCode());
            cstMeter.setAccountDate(cstMeter.getAccountDate());
            cstMeter.setUpdateTime(new Date());
            cstMeter.setCreateTime(new Date());
            cstMeter.setDeleteFlag(0);
            cstMeterDaoMapper.save(cstMeter);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        cstMeterDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

}
