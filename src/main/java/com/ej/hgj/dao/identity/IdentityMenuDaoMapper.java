package com.ej.hgj.dao.identity;

import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.identity.IdentityMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface IdentityMenuDaoMapper {

    void delete(Integer menuId);

    void insertList(@Param("list") List<IdentityMenu> identityMenuList);

    List<IdentityMenu> getList(IdentityMenu identityMenu);

}
