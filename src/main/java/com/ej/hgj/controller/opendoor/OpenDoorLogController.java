package com.ej.hgj.controller.opendoor;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
import com.ej.hgj.entity.opendoor.ExternalPersonInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorLog;
import com.ej.hgj.entity.visit.VisitLog;
import com.ej.hgj.utils.file.FileSendClient;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
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

    /**
     * 门禁记录列表
     * @param page
     * @param limit
     * @param openDoorLog
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           OpenDoorLog openDoorLog){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(openDoorLog.getEndDate())){
            openDoorLog.setEndDate(openDoorLog.getEndDate() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<OpenDoorLog> openDoorLogList = openDoorLogDaoMapper.getListAll(openDoorLog);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        for(OpenDoorLog doorLog : openDoorLogList) {
            // 时间转换
            Long eventTime = doorLog.getEventTime();
            Date date = new Date(eventTime);
            String formattedDate = sdf.format(date);
            doorLog.setOpenDoorTime(formattedDate);
        }
        PageInfo<OpenDoorLog> pageInfo = new PageInfo<>(openDoorLogList);
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
