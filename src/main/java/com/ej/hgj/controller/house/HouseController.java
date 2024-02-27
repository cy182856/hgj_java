package com.ej.hgj.controller.house;


import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.service.house.HouseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/house")
public class HouseController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HouseService houseService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           HgjHouse house){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<HgjHouse> list = houseService.getList(house);
        // logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<HgjHouse> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<HgjHouse> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/createQrCode",method = RequestMethod.GET)
    public AjaxResult creatCode(HgjCst hgjCst){
        logger.info("生成入住二维码参数:"+ JSONObject.toJSONString(hgjCst));
        GetTempQrcodeRequest getTempQrcodeRequest = new GetTempQrcodeRequest();
        getTempQrcodeRequest.setHouseId(hgjCst.getId());
        getTempQrcodeRequest.setProNum(hgjCst.getOrgId());
        GetTempQrcodeResult getTempQrcodeResult = houseService.qrcode(getTempQrcodeRequest);
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        map.put("imgUrl", getTempQrcodeResult.getImgUrl());
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }
}
