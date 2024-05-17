package com.ej.hgj.service.menu;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.menu.MenuDaoMapper;
import com.ej.hgj.dao.menu.cstmenumini.CstMenuMiniDaoMapper;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MenuDaoMapper menuDaoMapper;

    @Autowired
    private CstMenuMiniDaoMapper cstMenuMiniDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;


    @Override
    public List<Menu> menuList(Menu menu) {
        return menuDaoMapper.menuList(menu);
    }

    @Override
    public List<Menu> getMenuList(Menu menu) {
        return menuDaoMapper.getMenuList(menu);
    }

    @Override
    public List<Menu> getUserMenuList(String staffId) {
        return menuDaoMapper.getUserMenuList(staffId);
    }

    @Override
    public List<Menu> findMenuByRoleId(String roleId) {
        return menuDaoMapper.findMenuByRoleId(roleId);
    }

    @Override
    public void saveAppletCstMenu(MenuMini menuMini) {
        Integer menuMiniId = menuMini.getId();
        List<String> cstCodes = menuMini.getCstCodes();
        cstMenuMiniDaoMapper.delete(menuMiniId);
        logger.info("删除客户小程序菜单成功menuMiniId:" + menuMiniId);
        List<CstMenu> cstMenuList = new ArrayList<>();
        if(cstCodes != null){
            List<HgjCst> cstList = hgjCstDaoMapper.getList(new HgjCst());
            for(int i = 0; i<cstCodes.size(); i++){
                CstMenu cstMenu = new CstMenu();
                String cstCode = cstCodes.get(i);
                cstMenu.setId(TimestampGenerator.generateSerialNumber());
                cstMenu.setMenuId(menuMiniId);
                cstMenu.setCstCode(cstCode);
                cstMenu.setCreateBy("");
                cstMenu.setCreateTime(new Date());
                cstMenu.setUpdateBy("");
                cstMenu.setUpdateTime(new Date());
                cstMenu.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据客户编号查询客户表，如果查不到就不保存
                List<HgjCst> cstListFilter = cstList.stream().filter(hgjCst -> hgjCst.getCode().equals(cstCode)).collect(Collectors.toList());
                if(!cstListFilter.isEmpty()){
                    cstMenuList.add(cstMenu);
                }
            }
        }
        // 新增客户标签
        if(!cstMenuList.isEmpty()){
            cstMenuMiniDaoMapper.insertList(cstMenuList);
            logger.info("新增小程序客户菜单成功:"+ JSONObject.toJSONString(cstMenuList));
        }
    }

}
