package com.ej.hgj.service.menu;

import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.entity.tag.Tag;

import java.util.List;

public interface MenuService {

    List<Menu> menuList(Menu menu);

    List<Menu> getMenuList(Menu menu);

    List<Menu> getUserMenuList(String staffId);

    List<Menu> findMenuByRoleId(String roleId);

    void saveAppletCstMenu(MenuMini menuMini);


}
