package com.ej.hgj.controller.identity;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.identity.IdentityDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.dao.user.UserRoleDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.identity.Identity;
import com.ej.hgj.entity.identity.IdentityMenu;
import com.ej.hgj.entity.tag.*;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.tag.TagCstService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/identity")
public class IdentityController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdentityDaoMapper identityDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(Identity identity){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Identity> list = identityDaoMapper.getList(identity);
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/selectByProNums",method = RequestMethod.POST)
    public AjaxResult delete(@RequestBody Identity identity){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Identity> list = new ArrayList<>();
        if(identity != null && identity.getProNums() != null && identity.getProNums().size() > 0){
            list = identityDaoMapper.getList(identity);
        }
        map.put("list",list);
        ajaxResult.setData(map);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/selectByProNum",method = RequestMethod.GET)
    public AjaxResult selectByProNum(@RequestParam(required=false, value = "proNum") String proNum){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Identity> list = identityDaoMapper.getListByProNum(proNum);
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

}
