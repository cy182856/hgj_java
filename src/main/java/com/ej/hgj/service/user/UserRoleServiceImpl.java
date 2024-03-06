package com.ej.hgj.service.user;

import com.ej.hgj.dao.user.UserRoleDaoMapper;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.user.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRoleDaoMapper userRoleDaoMapper;

    @Override
    public UserRole getById(String id){
        return userRoleDaoMapper.getById(id);
    }

//    @Override
//    public UserRole getByStaffId(String staffId) {
//        return userRoleDaoMapper.getByStaffId(staffId);
//    }

    public List<UserRole> getList(UserRole userRole){
        return userRoleDaoMapper.getList(userRole);
    }
    @Override
    public void save(UserRole userRole) {
        userRoleDaoMapper.save(userRole);
    }

    @Override
    public void update(UserRole userRole) {
        userRoleDaoMapper.update(userRole);
    }

//    @Override
//    public void delete(String id) {
//        userRoleDaoMapper.delete(id);
//    }

}
