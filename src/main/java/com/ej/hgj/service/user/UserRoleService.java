package com.ej.hgj.service.user;



import com.ej.hgj.entity.user.UserRole;

import java.util.List;

public interface UserRoleService {

    UserRole getById(String id);

    UserRole getByStaffId(String staffId);

    List<UserRole> getList(UserRole userRole);

    void save(UserRole userRole);

    void update(UserRole userRole);

    void delete(String id);


}
