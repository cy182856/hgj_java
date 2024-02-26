package com.ej.hgj.service.role;



import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuService {

    void delete(String roleId);

    void insertList(@Param("list") List<RoleMenu> roleMenuList);

    void saveRoleMenu(Role role);

}
