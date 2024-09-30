package com.ej.hgj.controller.coupon;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.active.CouponQrCodeDaoMapper;
import com.ej.hgj.dao.coupon.CouponTypeDaoMapper;
import com.ej.hgj.entity.active.CouponQrCode;
import com.ej.hgj.entity.coupon.CouponType;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.visit.VisitLog;
import com.ej.hgj.service.config.ProConfService;
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
@RequestMapping("/coupon/qrCode")
public class CouponQrCodeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProConfService proConfServic;

    @Autowired
    private CouponTypeDaoMapper couponTypeDaoMapper;

    @Autowired
    private CouponQrCodeDaoMapper couponQrCodeDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           CouponQrCode couponQrCode){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(couponQrCode.getEndDate())){
            couponQrCode.setEndDate(couponQrCode.getEndDate() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<CouponQrCode> list = couponQrCodeDaoMapper.getList(couponQrCode);
        PageInfo<CouponQrCode> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<VisitLog> entityPageInfo = new PageInfo<>();
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
