package com.ej.hgj.service.user;

import com.ej.hgj.dao.user.UserLogDaoMapper;
import com.ej.hgj.entity.user.UserLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLogServiceImpl implements UserLogService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserLogDaoMapper userLogDaoMapper;

    @Override
    public UserLog queryUserLog(String userName, String password) {
        return userLogDaoMapper.queryUserLog(userName, password);
    }

    @Override
    public UserLog getById(String id){
        return userLogDaoMapper.getById(id);
    }

    public List<UserLog> getList(UserLog userLog){
        return userLogDaoMapper.getList(userLog);
    }

    @Override
    public void save(UserLog userLog) {
        userLogDaoMapper.save(userLog);
    }

    @Override
    public void update(UserLog userLog) {
        userLogDaoMapper.update(userLog);
    }

    @Override
    public void delete(String id) {
        userLogDaoMapper.delete(id);
    }


}
