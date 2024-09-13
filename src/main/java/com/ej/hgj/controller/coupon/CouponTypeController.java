package com.ej.hgj.controller.coupon;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.corp.CorpDaoMapper;
import com.ej.hgj.dao.coupon.CouponTypeDaoMapper;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.corp.Corp;
import com.ej.hgj.entity.coupon.CouponType;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.config.ProConfService;
import com.ej.hgj.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/coupon/type")
public class CouponTypeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProConfService proConfServic;

    @Autowired
    private CouponTypeDaoMapper couponTypeDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(CouponType couponType){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<CouponType> list = couponTypeDaoMapper.getList(new CouponType());
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }
}
