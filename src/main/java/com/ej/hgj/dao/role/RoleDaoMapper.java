package com.ej.hgj.dao.role;

import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoleDaoMapper {

    Role getById(String id);

    List<Role> getList(Role role);

    void save(Role role);

    void update(Role role);

    void delete(String id);

}
