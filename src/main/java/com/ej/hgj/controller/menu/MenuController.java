package com.ej.hgj.controller.menu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.entity.cst.CstPayPer;
import com.ej.hgj.entity.master.ArchiveBox;
import com.ej.hgj.entity.menu.Children;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.Meta;
import com.ej.hgj.entity.menu.TreeData;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.service.menu.MenuService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuMiniDaoMapper menuMiniDaoMapper;

    @Autowired
    private CstPayPerDaoMapper cstPayPerDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Menu menu){

        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Menu> list = menuService.menuList(menu);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Menu> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Menu> entityPageInfo = new PageInfo<>();
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
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/load",method = RequestMethod.GET)
    public AjaxResult menu(@RequestParam("token") String token){
        String staffId = JWT.decode(token).getAudience().get(0);
        AjaxResult ajaxResult = new AjaxResult();
        List<Menu> menuListAll = new ArrayList<>();
        // 查询当前用户所有的web菜单
        List<Menu> allMenuList = menuService.getUserMenuList(staffId);
//        Menu m = new Menu();
//        m.setPlatForm(Constant.PLAT_FORM_WEB);
//        List<Menu> allMenuList = menuService.getMenuList(m);
        // 查询一级菜单
        List<Menu> menuList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == 0).collect(Collectors.toList());
        for(Menu menu:menuList){
            List<Menu> childrenMenuList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == menu.getId()).collect(Collectors.toList());
            for(Menu childrenMenu : childrenMenuList){
                String childrenMetaStr = childrenMenu.getMetaStr();
                Map map = JSON.parseObject(childrenMetaStr, Map.class);
                Meta childrenMate = JSONObject.parseObject(JSONObject.toJSONString(map), Meta.class);
                childrenMenu.setMeta(childrenMate);
            }
            String metaMenu = menu.getMetaStr();
            Map map = JSON.parseObject(metaMenu, Map.class);
            Meta menuMate = JSONObject.parseObject(JSONObject.toJSONString(map), Meta.class);
            menu.setMeta(menuMate);
            menu.setChildren(childrenMenuList);
            menuListAll.add(menu);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setMenuList(menuListAll);
        logger.info("加载菜单:"+ JSON.toJSONString(menuListAll));
        return ajaxResult;
    }

    /**
     * 角色权限查询
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/selectMenu",method = RequestMethod.GET)
    public AjaxResult selectUserMenu(@RequestParam("roleId") String roleId){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询所有菜单
        List<Menu> allMenuList = menuService.getMenuList(new Menu());
        /*** web权限部分*/
        // 查询web权限父级菜单
        List<Menu> webMenuParentList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == 0 && allMenu.getPlatForm() == Constant.PLAT_FORM_WEB).collect(Collectors.toList());
        // web权限树
        List<TreeData> webTreeDataList = new ArrayList<>();
        for(Menu menu : webMenuParentList){
            TreeData treeData = new TreeData();
            treeData.setId(menu.getId());
            treeData.setLabel(menu.getName());
            // 获取二级菜单
            List<Menu> subMenuList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == menu.getId()).collect(Collectors.toList());
            List<Children> childrenList = new ArrayList<>();
            for(Menu menuSub : subMenuList){
                Children children = new Children();
                children.setId(menuSub.getId());
                children.setLabel(menuSub.getName());
                childrenList.add(children);
            }
            treeData.setChildren(childrenList);
            webTreeDataList.add(treeData);
        }

        // --------------------------------------------------------------------------------------------------

        /*** 企微权限部分*/
        // 查询企微权限父级菜单
        List<Menu> weComMenuParentList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == 0 && allMenu.getPlatForm() == Constant.PLAT_FORM_WE_COM).collect(Collectors.toList());
        // 企微权限树
        List<TreeData> weComTreeDataList = new ArrayList<>();
        for(Menu menu : weComMenuParentList){
            TreeData treeData = new TreeData();
            treeData.setId(menu.getId());
            treeData.setLabel(menu.getName());
            // 获取二级菜单
            List<Menu> subMenuList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == menu.getId()).collect(Collectors.toList());
            List<Children> childrenList = new ArrayList<>();
            for(Menu menuSub : subMenuList){
                Children children = new Children();
                children.setId(menuSub.getId());
                children.setLabel(menuSub.getName());
                childrenList.add(children);
            }
            treeData.setChildren(childrenList);
            weComTreeDataList.add(treeData);
        }

        HashMap map = new HashMap();
        // web所有菜单
        map.put("webTreeData",webTreeDataList);
        // 企微所有菜单
        map.put("weComTreeData",weComTreeDataList);

        // --------------------------------------------------------------------------------------

        /**菜单树被选中部分*/
        // 查询所选角色已拥有的权限
        List<Menu> alreadyMenuListAll = menuService.findMenuByRoleId(roleId);
        // 获取web端已拥有权限
        List<Menu> webAlreadyMenuList = alreadyMenuListAll.stream().filter(all -> all.getPlatForm() == Constant.PLAT_FORM_WEB).collect(Collectors.toList());
        // list转数组满足前端需求
        List<Integer> webMenuIds = webAlreadyMenuList.stream().map(webAlreadyMenu -> webAlreadyMenu.getId()).collect(Collectors.toList());
        Integer[] webExpandedKeys = webMenuIds.toArray(new Integer[webMenuIds.size()]);
        Integer[] webCheckedKeys = webMenuIds.toArray(new Integer[webMenuIds.size()]);
        // 展开菜单数组
        map.put("webExpandedKeys",webExpandedKeys);
        // 选中的菜单数组
        map.put("webCheckedKeys",webCheckedKeys);
        // ----------------------------------------------------------------------------------------
        // 获取企微端已拥有权限
        List<Menu> weComAlreadyMenuList = alreadyMenuListAll.stream().filter(all -> all.getPlatForm() == Constant.PLAT_FORM_WE_COM).collect(Collectors.toList());
        // list转数组满足前端需求
        List<Integer> weComMenuIds = weComAlreadyMenuList.stream().map(weComAlreadyMenu -> weComAlreadyMenu.getId()).collect(Collectors.toList());
        Integer[] weComExpandedKeys = weComMenuIds.toArray(new Integer[weComMenuIds.size()]);
        Integer[] weComCheckedKeys = weComMenuIds.toArray(new Integer[weComMenuIds.size()]);
        // 展开菜单数组
        map.put("weComExpandedKeys",weComExpandedKeys);
        // 选中的菜单数组
        map.put("weComCheckedKeys",weComCheckedKeys);

        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }


    /**
     * 客户权限查询
     * @param code
     * @return
     */
    @RequestMapping(value = "/selectMenuMini",method = RequestMethod.GET)
    public AjaxResult selectMenuMini(@RequestParam("code") String code){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询所有菜单
        List<MenuMini> allMenuList = menuMiniDaoMapper.getList(new MenuMini());
        /*** web权限部分*/
        // 查询web权限父级菜单
        List<MenuMini> webMenuParentList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == 0).collect(Collectors.toList());
        // web权限树
        List<TreeData> webTreeDataList = new ArrayList<>();
        for(MenuMini menu : webMenuParentList){
            TreeData treeData = new TreeData();
            treeData.setId(menu.getId());
            treeData.setLabel(menu.getFunName());
            // 获取二级菜单
            List<MenuMini> subMenuList = allMenuList.stream().filter(allMenu -> allMenu.getParentId() == menu.getId()).collect(Collectors.toList());
            List<Children> childrenList = new ArrayList<>();
            for(MenuMini menuSub : subMenuList){
                Children children = new Children();
                children.setId(menuSub.getId());
                children.setLabel(menuSub.getFunName());
                childrenList.add(children);
            }
            treeData.setChildren(childrenList);
            webTreeDataList.add(treeData);
        }
        HashMap map = new HashMap();
        // web所有菜单
        map.put("webTreeData",webTreeDataList);


        // --------------------------------------------------------------------------------------

        /**菜单树被选中部分*/
        // 查询所选客户已拥有的权限
        List<MenuMini> webAlreadyMenuList = menuMiniDaoMapper.findMenuByCstCode(code);
        // list转数组满足前端需求
        List<Integer> webMenuIds = webAlreadyMenuList.stream().map(webAlreadyMenu -> webAlreadyMenu.getId()).collect(Collectors.toList());
        Integer[] webExpandedKeys = webMenuIds.toArray(new Integer[webMenuIds.size()]);
        Integer[] webCheckedKeys = webMenuIds.toArray(new Integer[webMenuIds.size()]);
        // 展开菜单数组
        map.put("webExpandedKeys",webExpandedKeys);
        // 选中的菜单数组
        map.put("webCheckedKeys",webCheckedKeys);

        // 获取客户已拥有的账单权限
        List<CstPayPer> weComAlreadyMenuList = cstPayPerDaoMapper.findByCstCode(code);
        // list转数组满足前端需求
        List<Integer> weComMenuIds = weComAlreadyMenuList.stream().map(weComAlreadyMenu -> weComAlreadyMenu.getFunctionId()).collect(Collectors.toList());
        Integer[] weComExpandedKeys = weComMenuIds.toArray(new Integer[weComMenuIds.size()]);
        Integer[] weComCheckedKeys = weComMenuIds.toArray(new Integer[weComMenuIds.size()]);
        // 展开菜单数组
        map.put("weComExpandedKeys",weComExpandedKeys);
        // 选中的菜单数组
        map.put("weComCheckedKeys",weComCheckedKeys);

        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

}
