package com.ej.hgj.controller.coupon;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.StopCouponDaoMapper;
import com.ej.hgj.dao.coupon.StopCouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.StopCouponGrantDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.coupon.StopCoupon;
import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.coupon.StopCouponGrantBatch;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.service.coupon.CouponService;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.xml.bind.v2.schemagen.xmlschema.SchemaTop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/stopCoupon")
public class StopCouponController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StopCouponDaoMapper stopCouponDaoMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private StopCouponGrantBatchDaoMapper stopCouponGrantBatchDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           StopCoupon stopCoupon){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<StopCoupon> list = stopCouponDaoMapper.getList(stopCoupon);
//        for(StopCoupon s : list){
//            ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 3, 21, 16, 0, 0, 0, ZoneOffset.UTC);
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//            String formattedDate = zonedDateTime.format(formatter);
//            s.setStartDate(formattedDate);
//        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<StopCoupon> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<StopCoupon> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/batchList",method = RequestMethod.GET)
    public AjaxResult batchList(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        StopCouponGrantBatch stopCouponGrantBatch = new StopCouponGrantBatch();
        stopCouponGrantBatch.setStopCouponId(id);
        List<StopCouponGrantBatch> list = stopCouponGrantBatchDaoMapper.getList(stopCouponGrantBatch);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(StopCoupon stopCoupon){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<StopCoupon> list = stopCouponDaoMapper.getList(stopCoupon);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody StopCoupon stopCoupon){
        AjaxResult ajaxResult = new AjaxResult();
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(stopCoupon.getStartDate(), formatter);
//        Date date = Date.from(zonedDateTime.toInstant());
//        stopCoupon.setStartTime(date);
        if(stopCoupon.getId() != null){
            stopCouponDaoMapper.update(stopCoupon);
        }else{
            stopCoupon.setId(TimestampGenerator.generateSerialNumber());
            stopCoupon.setUpdateTime(new Date());
            stopCoupon.setCreateTime(new Date());
            stopCoupon.setDeleteFlag(0);
            stopCouponDaoMapper.save(stopCoupon);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/grant",method = RequestMethod.POST)
    public AjaxResult grant(@RequestBody StopCoupon stopCoupon){
        couponService.couponGrant(stopCoupon);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        stopCouponDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
