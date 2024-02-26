package com.ej.hgj.dao.role;

import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoleMenuDaoMapper {

    void delete(String roleId);

    void insertList(@Param("list") List<RoleMenu> roleMenuList);


}
