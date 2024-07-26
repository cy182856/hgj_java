package com.ej.hgj.controller.config;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.corp.CorpDaoMapper;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.corp.Corp;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.config.ProConfService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/config")
public class ProConfController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProConfService proConfServic;

    @Autowired
    private CorpDaoMapper corpDaoMapper;

    @Autowired
    private UserDaoMapper userDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(ProConfig proConfig){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<ProConfig> list = proConfServic.getList(new ProConfig());
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/project",method = RequestMethod.GET)
    public AjaxResult project(HttpServletRequest request, ProConfig proConfig){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        String userId = TokenUtils.getUserId(request);
        List<User> userList = userDaoMapper.queryUserByUserId(userId);
        List<String> proNumList = new ArrayList<>();
        if(!userList.isEmpty()){
            User user = userList.get(0);
            User pram = new User();
            pram.setMobile(user.getMobile());
            List<User> list = userDaoMapper.getList(pram);
            if(!list.isEmpty()){
                for(User u : list){
                    proNumList.add(u.getProjectNum());
                }
            }
        }
        proConfig.setProNumList(proNumList);
        List<ProConfig> list = proConfServic.getList(proConfig);
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/corp",method = RequestMethod.GET)
    public AjaxResult corp(){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Corp> list = corpDaoMapper.getList(new Corp());
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

//    @RequestMapping(value = "/save",method = RequestMethod.POST)
//    public AjaxResult save(@RequestBody ProConfig proConfig){
//        AjaxResult ajaxResult = new AjaxResult();
//        if(proConfig.getId() != null){
//            proConfServic.update(proConfig);
//        }else{
//            proConfig.setId(GenerateUniqueIdUtil.getGuid());
//            proConfig.setProjectNum(Constant.PROJECT_NUM);
//            proConfig.setUpdateTime(new Date());
//            proConfig.setCreateTime(new Date());
//            proConfig.setDeleteFlag(0);
//            proConfServic.save(proConfig);
//        }
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }

}
