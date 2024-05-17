package com.ej.hgj.dao.menu.mini;

import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MenuMiniDaoMapper {

    List<MenuMini> getList(MenuMini menuMini);

    List<MenuMini> getByParentId(MenuMini menuMini);

    void delete(String cstCode);

    void insertList(@Param("list") List<CstMenu> cstMenuList);

    List<MenuMini> findMenuByCstCode(String cstCode);


}
