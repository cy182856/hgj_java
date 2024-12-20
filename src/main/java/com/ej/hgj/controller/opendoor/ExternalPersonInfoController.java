package com.ej.hgj.controller.opendoor;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.opendoor.ExternalPersonInfoDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorCodeServiceDaoMapper;
import com.ej.hgj.entity.opendoor.ExternalPersonInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCodeService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/opendoor")
public class ExternalPersonInfoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExternalPersonInfoDaoMapper externalPersonInfoDaoMapper;

    @RequestMapping(value = "/personInfo/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           ExternalPersonInfo externalPersonInfo){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(externalPersonInfo.getEndDate())){
            externalPersonInfo.setEndDate(externalPersonInfo.getEndDate() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<ExternalPersonInfo> list = externalPersonInfoDaoMapper.getList(externalPersonInfo);
        for(ExternalPersonInfo o : list){
            if(StringUtils.isNotBlank(o.getFacePicPath())){
                // 获取图片路径
                String imgPath = o.getFacePicPath();
                String base64Img = "";
                try {
                    // 创建BufferedReader对象，从本地文件中读取
                    BufferedReader reader = new BufferedReader(new FileReader(imgPath));
                    // 逐行读取文件内容
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        base64Img += line;
                    }
                    // 关闭文件
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                base64Img = "data:image/jpeg;base64," + base64Img;
                o.setFacePicPath(base64Img);
            }
        }
        PageInfo<ExternalPersonInfo> pageInfo = new PageInfo<>(list);
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
