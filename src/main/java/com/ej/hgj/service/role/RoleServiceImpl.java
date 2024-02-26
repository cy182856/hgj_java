package com.ej.hgj.service.role;

import com.ej.hgj.dao.role.RoleDaoMapper;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleDaoMapper roleDaoMapper;

    @Override
    public Role getById(String id){
        return roleDaoMapper.getById(id);
    }

    public List<Role> getList(Role role){
        return roleDaoMapper.getList(role);
    }

    @Override
    public void save(Role role) {
        roleDaoMapper.save(role);
    }

    @Override
    public void update(Role role) {
        roleDaoMapper.update(role);
    }

    @Override
    public void delete(String id) {
        roleDaoMapper.delete(id);
    }

}
