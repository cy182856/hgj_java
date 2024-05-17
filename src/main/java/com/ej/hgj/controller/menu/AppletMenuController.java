package com.ej.hgj.controller.menu;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.menu.cstmenumini.CstMenuMiniDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.tag.*;
import com.ej.hgj.service.menu.MenuService;
import com.ej.hgj.service.tag.TagCstService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序菜单
 */

@CrossOrigin
@RestController
@RequestMapping("/applet/menu")
public class AppletMenuController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProConfDaoMapper proConfDaoMapper;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private HgjSyHouseDaoMapper hgjSyHouseDaoMapper;

    @Autowired
    private TagCstService tagCstService;

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private MenuMiniDaoMapper menuMiniDaoMapper;

    @Autowired
    private CstMenuMiniDaoMapper cstMenuMiniDaoMapper;

    @Autowired
    private MenuService menuService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           MenuMini menuMini){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<MenuMini> list = menuMiniDaoMapper.getByParentId(menuMini);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<MenuMini> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<GonggaoType> entityPageInfo = new PageInfo<>();
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

    /**
     * 客户树结构查询
     * @param menuId
     * @return
     */
    @RequestMapping(value = "/selectCstTree",method = RequestMethod.GET)
    public AjaxResult selectUserMenu(@RequestParam("menuId") String menuId){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询项目
        List<ProConfig> proList = proConfDaoMapper.getList(new ProConfig());
        List<OneTreeData> oneTreeDataList = new ArrayList<>();
        for(ProConfig proConfig : proList){
            OneTreeData oneTreeData = new OneTreeData();
            oneTreeData.setId(proConfig.getProjectNum());
            oneTreeData.setLabel(proConfig.getProjectName());
            // 获取二级-楼栋
            List<HgjHouse> budList = hgjHouseDaoMapper.queryBuilding(proConfig.getProjectNum());
            List<TwoChildren> twoChildrenList = new ArrayList<>();
            for(HgjHouse bud : budList){
                TwoChildren twoChildren = new TwoChildren();
                twoChildren.setId(bud.getBudId());
                twoChildren.setLabel(bud.getBudName());
                // 获取三级-房号
                List<HgjHouse> houseList = hgjSyHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<ThreeChildren> threeChildrenList = new ArrayList<>();
                for(HgjHouse house : houseList){
                    ThreeChildren threeChildren = new ThreeChildren();
                    threeChildren.setId(house.getCstCode());
                    threeChildren.setLabel(house.getCstName());
                    threeChildrenList.add(threeChildren);
                }
                twoChildren.setChildren(threeChildrenList);
                twoChildrenList.add(twoChildren);
            }
            oneTreeData.setChildren(twoChildrenList);
            oneTreeDataList.add(oneTreeData);
        }

        HashMap map = new HashMap();
        map.put("cstTreeData",oneTreeDataList);

        // 获取已被选中的客户树
        CstMenu cstMenuPram = new CstMenu();
        cstMenuPram.setMenuId(Integer.valueOf(menuId));
        List<CstMenu> cstMenuList = cstMenuMiniDaoMapper.getList(cstMenuPram);
        // list转数组满足前端需求
        List<String> cstCodes = cstMenuList.stream().map(tagCst -> tagCst.getCstCode()).collect(Collectors.toList());
        String[] miniMenuExpandedKeys = cstCodes.toArray(new String[cstCodes.size()]);
        String[] miniMenuCheckedKeys = cstCodes.toArray(new String[cstCodes.size()]);
        // 展开菜单数组
        map.put("miniMenuExpandedKeys",miniMenuExpandedKeys);
        // 选中的菜单数组
        map.put("miniMenuCheckedKeys",miniMenuCheckedKeys);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/saveAppletCstMenu",method = RequestMethod.POST)
    public AjaxResult saveAppletCstMenu(@RequestBody MenuMini menuMini){
        AjaxResult ajaxResult = new AjaxResult();
        menuService.saveAppletCstMenu(menuMini);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

}
