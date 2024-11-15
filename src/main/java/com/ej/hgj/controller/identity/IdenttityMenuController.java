package com.ej.hgj.controller.identity;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.menu.cstmenumini.CstMenuMiniDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.identity.IdentityMenu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.tag.OneTreeData;
import com.ej.hgj.entity.tag.ThreeChildren;
import com.ej.hgj.entity.tag.TwoChildren;
import com.ej.hgj.service.identity.IdentityService;
import com.ej.hgj.service.menu.MenuService;
import com.ej.hgj.service.tag.TagCstService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序菜单
 */

@CrossOrigin
@RestController
@RequestMapping("/identity/menu")
public class IdenttityMenuController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdentityService identityService;

    @RequestMapping(value = "/saveIdentityMenu",method = RequestMethod.POST)
    public AjaxResult saveAppletCstMenu(@RequestBody IdentityMenu identityMenu){
        AjaxResult ajaxResult = new AjaxResult();
        identityService.saveIdentityMenu(identityMenu);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

}