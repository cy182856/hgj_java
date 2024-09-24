package com.ej.hgj.controller.opendoor;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.dao.visit.VisitLogDaoMapper;
import com.ej.hgj.entity.adverts.Adverts;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/opendoor")
public class OpenDoorCodeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OpenDoorCodeDaoMapper openDoorCodeDaoMapper;

    @RequestMapping(value = "/qrCode/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           OpenDoorCode openDoorCode){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(openDoorCode.getEndDate())){
            openDoorCode.setEndDate(openDoorCode.getEndDate() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<OpenDoorCode> list = openDoorCodeDaoMapper.getList(openDoorCode);
        PageInfo<OpenDoorCode> pageInfo = new PageInfo<>(list);
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
