package com.ej.hgj.dao.menu.cstmenumini;

import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.tag.TagCst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstMenuMiniDaoMapper {

    void delete(Integer menuId);

    void insertList(@Param("list") List<CstMenu> cstMenuList);

    List<CstMenu> getList(CstMenu cstMenu);

}
