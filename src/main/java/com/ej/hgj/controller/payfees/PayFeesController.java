package com.ej.hgj.controller.payfees;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.carpay.ParkPayOrderDaoMapper;
import com.ej.hgj.dao.payfees.PaymentRecordDaoMapper;
import com.ej.hgj.entity.carpay.ParkPayOrder;
import com.ej.hgj.entity.payfees.PaymentRecord;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/payFees")
public class PayFeesController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaymentRecordDaoMapper paymentRecordDaoMapper;


    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           PaymentRecord paymentRecord){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(paymentRecord.getEndTime())){
            paymentRecord.setEndTime(paymentRecord.getEndTime() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<PaymentRecord> list = paymentRecordDaoMapper.getList(paymentRecord);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        for(PaymentRecord p : list){
            if(StringUtils.isNotBlank(p.getSuccessTime())) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(p.getSuccessTime(), formatter);
                String formatted = zonedDateTime.format(DateUtils.formatter_ymd_hms);
                p.setSuccessTime(formatted);
            }
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<PaymentRecord> pageInfo = new PageInfo<>(list);
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
