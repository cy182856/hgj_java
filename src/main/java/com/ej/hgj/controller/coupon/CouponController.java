package com.ej.hgj.controller.coupon;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.CouponDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
import com.ej.hgj.service.coupon.CouponService;
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
@RequestMapping("/coupon")
public class CouponController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponDaoMapper couponDaoMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponGrantDaoMapper couponGrantDaoMapper;

    @Autowired
    private CouponGrantBatchDaoMapper couponGrantBatchDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Coupon coupon){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Coupon> list = couponDaoMapper.getList(coupon);
//        for(StopCoupon s : list){
//            ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 3, 21, 16, 0, 0, 0, ZoneOffset.UTC);
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//            String formattedDate = zonedDateTime.format(formatter);
//            s.setStartDate(formattedDate);
//        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Coupon> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Coupon> entityPageInfo = new PageInfo<>();
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
        CouponGrantBatch cuponGrantBatch = new CouponGrantBatch();
        cuponGrantBatch.setCouponId(id);
        List<CouponGrantBatch> list = couponGrantBatchDaoMapper.getList(cuponGrantBatch);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(Coupon coupon){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Coupon> list = couponDaoMapper.getList(coupon);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Coupon coupon){
        AjaxResult ajaxResult = new AjaxResult();
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(stopCoupon.getStartDate(), formatter);
//        Date date = Date.from(zonedDateTime.toInstant());
//        stopCoupon.setStartTime(date);
        if(coupon.getId() != null){
            couponDaoMapper.update(coupon);
        }else{
            coupon.setId(TimestampGenerator.generateSerialNumber());
            coupon.setUpdateTime(new Date());
            coupon.setCreateTime(new Date());
            coupon.setDeleteFlag(0);
            couponDaoMapper.save(coupon);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/grant",method = RequestMethod.POST)
    public AjaxResult grant(@RequestBody Coupon coupon){
        couponService.couponGrant(coupon);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询卷发放记录
        CouponGrantBatch stopCouponGrantBatch = new CouponGrantBatch();
        stopCouponGrantBatch.setCouponId(id);
        List<CouponGrantBatch> list = couponGrantBatchDaoMapper.getList(stopCouponGrantBatch);
        if(!list.isEmpty()){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("该券有发放记录，不能删除！");
        }else {
            couponDaoMapper.delete(id);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        }
        return ajaxResult;
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.GET)
    public AjaxResult batchDelete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        couponService.deleteBatch(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
