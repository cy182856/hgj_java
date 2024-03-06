package com.ej.hgj.dao.menu;

import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MenuDaoMapper {

    List<Menu> menuList(Menu menu);

    List<Menu> getMenuList(Menu menu);

    List<Menu> getUserMenuList(String userId);

    List<Menu> findMenuByRoleId(String roleId);

}
