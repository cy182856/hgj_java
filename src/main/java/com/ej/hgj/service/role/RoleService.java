package com.ej.hgj.service.role;



import com.ej.hgj.entity.role.Role;

import java.util.List;

public interface RoleService {

    Role getById(String id);

    List<Role> getList(Role user);

    void save(Role user);

    void update(Role user);

    void delete(String id);


}
