package com.ej.hgj.controller.cst;


import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.service.cst.CstService;
import com.ej.hgj.service.cstInto.CstIntoService;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.vo.IntoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/cst")
public class CstController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstService cstService;

    @Autowired
    private CstIntoService cstIntoService;

    @Autowired
    private TagDaoMapper tagDaoMapper;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private CstIntoHouseDaoMapper cstIntoHouseDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           HgjCst cst){
        AjaxResult ajaxResult = new AjaxResult();
        List<Tag> tagList = tagDaoMapper.getCstTag(new Tag());
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<HgjCst> list = cstService.getList(cst);
        //logger.info("list:"+ JSON.toJSONString(list));
        for(HgjCst hgjCst : list){
            List<Tag> tagListFilter = tagList.stream().filter(tag -> tag.getCstCode().equals(hgjCst.getCode())).collect(Collectors.toList());
            hgjCst.setTagList(tagListFilter);
        }
        PageInfo<HgjCst> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<HgjCst> entityPageInfo = new PageInfo<>();
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
        logger.info("生成入住二维码参数:"+JSONObject.toJSONString(hgjCst));
        GetTempQrcodeRequest getTempQrcodeRequest = new GetTempQrcodeRequest();
        getTempQrcodeRequest.setCstCode(hgjCst.getCode());
        getTempQrcodeRequest.setProNum(hgjCst.getOrgId());
        GetTempQrcodeResult getTempQrcodeResult = cstService.qrcode(getTempQrcodeRequest);
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        map.put("imgUrl", getTempQrcodeResult.getImgUrl());
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/createIntoCstQrCode",method = RequestMethod.POST)
    public AjaxResult createIntoCstQrCode(@RequestBody IntoVo intoVo){
        logger.info("生成入住二维码参数:"+JSONObject.toJSONString(intoVo));
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        String cstIntoId = cstIntoService.saveCstIntoInfo(intoVo);
        if(StringUtils.isNotBlank(cstIntoId)){
            GetTempQrcodeRequest getTempQrcodeRequest = new GetTempQrcodeRequest();
            getTempQrcodeRequest.setCstIntoId(cstIntoId);
            getTempQrcodeRequest.setProNum(intoVo.getOrgId());
            GetTempQrcodeResult getTempQrcodeResult = cstService.cstIntoQrcode(getTempQrcodeRequest);
            map.put("imgUrl", getTempQrcodeResult.getImgUrl());
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/saveCstMenu",method = RequestMethod.POST)
    public AjaxResult saveRoleMenu(@RequestBody HgjCst hgjCst){
        AjaxResult ajaxResult = new AjaxResult();
        cstService.saveCstMenu(hgjCst);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
