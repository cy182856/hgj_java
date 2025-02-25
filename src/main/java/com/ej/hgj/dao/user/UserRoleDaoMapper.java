package com.ej.hgj.dao.user;

import com.ej.hgj.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserRoleDaoMapper {

    UserRole getById(String id);

    //UserRole getByStaffId(String staffId);

    UserRole getByUserId(String userId);

    //UserRole getByStaffIdAndCorpId(String staffId, String corpId);

    List<UserRole> getList(UserRole userRole);

    List<UserRole> getByUserAndRole(String userId);

    List<UserRole> getUserRoleByMobile(String mobile);

    void save(UserRole userRole);

    void update(UserRole userRole);

    //void updateByStaffIdAndCorpId(UserRole userRole);

    //void delete(String id);

    void deleteByUserId(String userId);

    //void deleteByStaffIdAndCorpId(String staffId, String corpId);

}
