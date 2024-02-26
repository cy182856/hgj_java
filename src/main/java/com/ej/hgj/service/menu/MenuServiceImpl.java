package com.ej.hgj.service.menu;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.menu.MenuDaoMapper;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MenuDaoMapper menuDaoMapper;

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

}
