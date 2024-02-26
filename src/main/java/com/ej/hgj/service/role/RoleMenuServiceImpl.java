package com.ej.hgj.service.role;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.role.RoleMenuDaoMapper;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.service.menu.MenuService;
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
public class RoleMenuServiceImpl implements RoleMenuService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleMenuDaoMapper roleMenuDaoMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private MenuService menuService;

    @Override
    public void delete(String roleId) {
        roleMenuDaoMapper.delete(roleId);
    }

    @Override
    public void insertList(List<RoleMenu> roleMenuList) {
        roleMenuDaoMapper.insertList(roleMenuList);
    }

    @Override
    public void saveRoleMenu(Role role) {
        // 查询所有权限
        List<Menu> menuList = menuService.getMenuList(new Menu());
        // 删除权限
        roleMenuService.delete(role.getId());
        logger.info("删除权限成功roleId:" + role.getId());
        List<Integer> webMenuIds = role.getWebMenuIds();
        List<RoleMenu> roleMenuList = new ArrayList<>();
        if(webMenuIds != null){
            for(int i = 0; i<webMenuIds.size(); i++){
                RoleMenu webRoleMenu = new RoleMenu();
                Integer webMenuId = webMenuIds.get(i);
                webRoleMenu.setId(TimestampGenerator.generateSerialNumber());
                //webRoleMenu.setProjectNum(Constant.PROJECT_NUM);
                webRoleMenu.setRoleId(role.getId());
                webRoleMenu.setMenuId(webMenuId);
                webRoleMenu.setCreateBy("");
                webRoleMenu.setCreateTime(new Date());
                webRoleMenu.setUpdateBy("");
                webRoleMenu.setUpdateTime(new Date());
                webRoleMenu.setDeleteFlag(0);
                // 如果不是父节点就保存
                List<Menu> menus = menuList.stream().filter(menu -> menu.getId().equals(webMenuId)).collect(Collectors.toList());
                Menu menu = menus.get(0);
                if(menu.getParentId() != 0){
                    roleMenuList.add(webRoleMenu);
                }
            }
        }

        List<Integer> weComMenuIds = role.getWeComMenuIds();
        if(!weComMenuIds.isEmpty()) {
            for (int i = 0; i < weComMenuIds.size(); i++) {
                RoleMenu weComRoleMenu = new RoleMenu();
                Integer weComMenuId = weComMenuIds.get(i);
                weComRoleMenu.setId(TimestampGenerator.generateSerialNumber());
                //weComRoleMenu.setProjectNum(Constant.PROJECT_NUM);
                weComRoleMenu.setRoleId(role.getId());
                weComRoleMenu.setMenuId(weComMenuId);
                weComRoleMenu.setCreateBy("");
                weComRoleMenu.setCreateTime(new Date());
                weComRoleMenu.setUpdateBy("");
                weComRoleMenu.setUpdateTime(new Date());
                weComRoleMenu.setDeleteFlag(0);
                // 如果不是父节点就保存
                List<Menu> menus = menuList.stream().filter(menu -> menu.getId().equals(weComMenuId)).collect(Collectors.toList());
                Menu menu = menus.get(0);
                if(menu.getParentId() != 0){
                    roleMenuList.add(weComRoleMenu);
                }
            }
        }
        // 新增权限
        if(!roleMenuList.isEmpty()){
            roleMenuService.insertList(roleMenuList);
            logger.info("新增权限成功:"+ JSONObject.toJSONString(roleMenuList));
        }
    }

}
