package com.ej.hgj.service.user;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.user.UserBuildDaoMapper;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.dao.user.UserDutyPhoneDaoMapper;
import com.ej.hgj.dao.user.UserRoleDaoMapper;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserBuild;
import com.ej.hgj.entity.user.UserDutyPhone;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TimestampGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserDaoMapper userDaoMapper;

    @Autowired
    private UserBuildDaoMapper userBuildDaoMapper;

    @Autowired
    private UserDutyPhoneDaoMapper userDutyPhoneDaoMapper;

    @Autowired
    private UserRoleDaoMapper userRoleDaoMapper;

    @Override
    public User queryUser(String userName, String password) {
        return userDaoMapper.queryUser(userName, password);
    }

    @Override
    public User getById(String id){
        return userDaoMapper.getById(id);
    }

    public List<User> getList(User user){
        return userDaoMapper.getList(user);
    }

    @Override
    public List<User> getDeptList(User user) {
        return userDaoMapper.getDeptList(user);
    }

    @Override
    public void insertList(List<User> users) {
        userDaoMapper.insertList(users);
    }

    @Override
    public void save(User user) {
        userDaoMapper.save(user);
    }

    @Override
    public void update(User user) {
        userDaoMapper.update(user);
    }

    @Override
    public void delete(String id) {
        userDaoMapper.delete(id);
    }

    @Override
    public AjaxResult updateRolePro(AjaxResult ajaxResult, User user, String loginUserId) {
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        User u = userService.getById(user.getStaffId());
        // 角色
        if (StringUtils.isBlank(user.getRoleId())) {
            userRoleService.delete(user.getStaffId());
        } else {
            // 更新用户角色关联数据
            UserRole ur = userRoleService.getByStaffId(user.getStaffId());
            if (ur != null) {
                ur.setRoleId(user.getRoleId());
                ur.setUpdateTime(new Date());
                ur.setUpdateBy(loginUserId);
                userRoleService.update(ur);
            } else {
                // 新增用户角色关联数据
                UserRole userRole = new UserRole();
                userRole.setId(TimestampGenerator.generateSerialNumber());
                //userRole.setProjectNum(Constant.PROJECT_NUM);
                userRole.setStaffId(u.getStaffId());
                userRole.setRoleId(user.getRoleId());
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
                userRole.setCreateBy(loginUserId);
                userRole.setUpdateBy(loginUserId);
                userRole.setDeleteFlag(0);
                userRoleService.save(userRole);
            }
        }

        // 楼栋
        String[] build = user.getBudId();
        if(build == null || build.length == 0){
            userBuildDaoMapper.delete(user.getStaffId());
        }else{
            // 更新用户楼栋关联数据
            UserBuild userBuild = new UserBuild();
            userBuild.setMobile(user.getStaffId());
            List<UserBuild> userBuildList = userBuildDaoMapper.getList(userBuild);
            if (!userBuildList.isEmpty() ) {
                // 如果有楼栋绑定数据先删除
                userBuildDaoMapper.delete(user.getStaffId());
            }
            // 插入
            saveUserBuilds(build, user);
        }

        // 值班电话
        if(StringUtils.isBlank(user.getPhone())){
            userDutyPhoneDaoMapper.delete(user.getMobile());
        }else{
            // 更新用户值班电话
            UserDutyPhone userDutyPhone = userDutyPhoneDaoMapper.getByMobile(user.getMobile());
            if (userDutyPhone != null ) {
                // 先删除
                userDutyPhoneDaoMapper.delete(user.getMobile());
            }
            // 插入
            UserDutyPhone dutyPhone = new UserDutyPhone();
            dutyPhone.setId(TimestampGenerator.generateSerialNumber());
            dutyPhone.setMobile(user.getMobile());
            dutyPhone.setPhone(user.getPhone());
            dutyPhone.setCreateTime(new Date());
            dutyPhone.setUpdateTime(new Date());
            dutyPhone.setCreateBy("");
            dutyPhone.setUpdateBy("");
            dutyPhone.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            userDutyPhoneDaoMapper.save(dutyPhone);
        }

        // 项目
        u.setUpdateTime(new Date());
        if (StringUtils.isBlank(user.getProjectNum())) {
            u.setProjectNum("");
        } else {
            u.setProjectNum(user.getProjectNum());
        }
        userService.update(u);

        return ajaxResult;
    }

    public void saveUserBuilds(String[] build, User user){
        List<UserBuild> userBuilds = new ArrayList<>();
        for(int i = 0; i<build.length; i++){
            UserBuild ub = new UserBuild();
            ub.setId(TimestampGenerator.generateSerialNumber());
            ub.setProjectNum(user.getProjectNum());
            ub.setBudId(build[i]);
            ub.setMobile(user.getStaffId());
            ub.setCreateTime(new Date());
            ub.setUpdateTime(new Date());
            ub.setCreateBy("");
            ub.setUpdateBy("");
            ub.setDeleteFlag(0);
            userBuilds.add(ub);
        }
        userBuildDaoMapper.insertList(userBuilds);
    }


//    @Override
//    public AjaxResult updateRolePro(AjaxResult ajaxResult, User user, String loginUserId) {
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        User u = userDaoMapper.getByStaffIdAndCorpId(user.getStaffId(), user.getCorpId());
//        // 角色
//        if (StringUtils.isBlank(user.getRoleId())) {
//            userRoleDaoMapper.deleteByStaffIdAndCorpId(user.getStaffId(), user.getCorpId());
//        } else {
//            // 更新用户角色关联数据
//            UserRole ur = userRoleDaoMapper.getByStaffIdAndCorpId(user.getStaffId(), user.getCorpId());
//            if (ur != null) {
//                ur.setRoleId(user.getRoleId());
//                ur.setUpdateTime(new Date());
//                ur.setUpdateBy(loginUserId);
//                userRoleDaoMapper.updateByStaffIdAndCorpId(ur);
//            } else {
//                // 新增用户角色关联数据
//                UserRole userRole = new UserRole();
//                userRole.setId(TimestampGenerator.generateSerialNumber());
//                userRole.setCorpId(user.getCorpId());
//                //userRole.setProjectNum(Constant.PROJECT_NUM);
//                userRole.setStaffId(u.getStaffId());
//                userRole.setRoleId(user.getRoleId());
//                userRole.setCreateTime(new Date());
//                userRole.setUpdateTime(new Date());
//                userRole.setCreateBy(loginUserId);
//                userRole.setUpdateBy(loginUserId);
//                userRole.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//                userRoleService.save(userRole);
//            }
//        }
//
//        // 楼栋
//        String[] build = user.getBudId();
//        if(build == null || build.length == 0){
//            userBuildDaoMapper.deleteByMobileAndCorpId(user.getStaffId(),user.getCorpId());
//        }else{
//            // 更新用户楼栋关联数据
//            UserBuild userBuild = new UserBuild();
//            userBuild.setMobile(user.getStaffId());
//            userBuild.setCorpId(user.getCorpId());
//            List<UserBuild> userBuildList = userBuildDaoMapper.getList(userBuild);
//            if (!userBuildList.isEmpty() ) {
//                // 如果有楼栋绑定数据先删除
//                userBuildDaoMapper.deleteByMobileAndCorpId(user.getStaffId(), user.getCorpId());
//            }
//            // 插入
//            saveUserBuilds(build, user);
//        }
//
//        // 值班电话
//        if(StringUtils.isBlank(user.getPhone())){
//            userDutyPhoneDaoMapper.deleteByMobileAndCorpId(user.getMobile(), user.getCorpId());
//        }else{
//            // 更新用户值班电话
//            UserDutyPhone userDutyPhone = userDutyPhoneDaoMapper.getByMobileAndCorpId(user.getMobile(),user.getCorpId());
//            if (userDutyPhone != null ) {
//                // 先删除
//                userDutyPhoneDaoMapper.deleteByMobileAndCorpId(user.getMobile(), user.getCorpId());
//            }
//            // 插入
//            UserDutyPhone dutyPhone = new UserDutyPhone();
//            dutyPhone.setId(TimestampGenerator.generateSerialNumber());
//            dutyPhone.setCorpId(user.getCorpId());
//            dutyPhone.setMobile(user.getMobile());
//            dutyPhone.setPhone(user.getPhone());
//            dutyPhone.setCreateTime(new Date());
//            dutyPhone.setUpdateTime(new Date());
//            dutyPhone.setCreateBy("");
//            dutyPhone.setUpdateBy("");
//            dutyPhone.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//            userDutyPhoneDaoMapper.save(dutyPhone);
//        }
//
//        // 项目
////        u.setUpdateTime(new Date());
////        if (StringUtils.isBlank(user.getProjectNum())) {
////            u.setProjectNum("");
////        } else {
////            u.setProjectNum(user.getProjectNum());
////        }
//        userService.update(u);
//
//        return ajaxResult;
//    }

//    public void saveUserBuilds(String[] build, User user){
//        List<UserBuild> userBuilds = new ArrayList<>();
//        for(int i = 0; i<build.length; i++){
//            UserBuild ub = new UserBuild();
//            ub.setId(TimestampGenerator.generateSerialNumber());
//            ub.setCorpId(user.getCorpId());
//            ub.setProjectNum(user.getProjectNum());
//            ub.setBudId(build[i]);
//            ub.setMobile(user.getStaffId());
//            ub.setCreateTime(new Date());
//            ub.setUpdateTime(new Date());
//            ub.setCreateBy("");
//            ub.setUpdateBy("");
//            ub.setDeleteFlag(0);
//            userBuilds.add(ub);
//        }
//        userBuildDaoMapper.insertList(userBuilds);
//    }
}
