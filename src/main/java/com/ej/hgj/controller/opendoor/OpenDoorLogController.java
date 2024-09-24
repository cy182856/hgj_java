package com.ej.hgj.controller.opendoor;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorLog;
import com.ej.hgj.entity.visit.VisitLog;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/opendoor")
public class OpenDoorLogController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OpenDoorLogDaoMapper openDoorLogDaoMapper;

    @RequestMapping(value = "/log",method = RequestMethod.GET)
    public AjaxResult batchList(@RequestParam(required=false, value = "cardNo") String cardNo){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<OpenDoorLog> openDoorLogList = openDoorLogDaoMapper.getByCardNoList(cardNo);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        for(OpenDoorLog doorLog : openDoorLogList) {
            // 时间转换
            Long eventTime = doorLog.getEventTime();
            Date date = new Date(eventTime);
            String formattedDate = sdf.format(date);
            doorLog.setOpenDoorTime(formattedDate);
        }
        map.put("openDoorLogList",openDoorLogList);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }
}
