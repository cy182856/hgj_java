package com.ej.hgj.controller.coupon;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.StopCouponDaoMapper;
import com.ej.hgj.dao.coupon.StopCouponGrantDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.tag.TagCst;
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
@RequestMapping("/stopCouponGrant")
public class StopCouponGrantController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StopCouponGrantDaoMapper stopCouponGrantDaoMapper;

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           StopCouponGrant stopCouponGrant){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<StopCouponGrant> list = stopCouponGrantDaoMapper.getList(stopCouponGrant);
        PageInfo<StopCouponGrant> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<StopCouponGrant> entityPageInfo = new PageInfo<>();
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
    public AjaxResult select(StopCouponGrant stopCouponGrant){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<StopCouponGrant> list = stopCouponGrantDaoMapper.getList(stopCouponGrant);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody StopCouponGrant stopCouponGrant){
        AjaxResult ajaxResult = new AjaxResult();
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(stopCouponGrant.getStartDate(), formatter);
//        Date date = Date.from(zonedDateTime.toInstant());
//        stopCouponGrant.setStartTime(date);
        if(stopCouponGrant.getId() != null){
            stopCouponGrantDaoMapper.update(stopCouponGrant);
        }else{
            stopCouponGrant.setId(TimestampGenerator.generateSerialNumber());
            stopCouponGrant.setUpdateTime(new Date());
            stopCouponGrant.setCreateTime(new Date());
            stopCouponGrant.setDeleteFlag(0);
            stopCouponGrantDaoMapper.save(stopCouponGrant);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        stopCouponGrantDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
