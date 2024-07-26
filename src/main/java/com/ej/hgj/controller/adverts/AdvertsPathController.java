package com.ej.hgj.controller.adverts;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.adverts.AdvertsDaoMapper;
import com.ej.hgj.dao.adverts.AdvertsPathDaoMapper;
import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.adverts.AdvertsPath;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/advertsPath")
public class AdvertsPathController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdvertsPathDaoMapper advertsPathDaoMapper;

    @Autowired
    private AdvertsDaoMapper advertsDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           AdvertsPath advertsPath){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<AdvertsPath> list = advertsPathDaoMapper.getList(advertsPath);

        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<AdvertsPath> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<AdvertsPath> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(AdvertsPath advertsPath){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<AdvertsPath> list = advertsPathDaoMapper.getList(new AdvertsPath());
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody AdvertsPath advertsPath){
        AjaxResult ajaxResult = new AjaxResult();
        if(advertsPath.getId() != null){
            advertsPathDaoMapper.update(advertsPath);
        }else{
            advertsPath.setId(TimestampGenerator.generateSerialNumber());
            advertsPath.setUpdateTime(new Date());
            advertsPath.setCreateTime(new Date());
            advertsPath.setDeleteFlag(0);
            advertsPathDaoMapper.save(advertsPath);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        // 检验广告地址是否与广告有关联
        Adverts adverts = new Adverts();
        adverts.setAdvertsPathId(id);
        List<Adverts> list = advertsDaoMapper.getList(adverts);
        if(list.isEmpty()){
            // 删除数据
            advertsPathDaoMapper.delete(id);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        }else {
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("与广告有关联无法删除！");
        }
        return ajaxResult;
    }
}

