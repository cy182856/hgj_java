package com.ej.hgj.dao.user;

import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.entity.user.UserBuild;
import com.ej.hgj.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserBuildDaoMapper {

    UserBuild getById(String id);

    UserBuild getByMobile(String staffId);

    List<UserBuild> getList(UserBuild userBuild);

    void save(UserBuild userBuild);

    void update(UserBuild userBuild);

    void delete(String mobile);

    void deleteByUserId(String userId);

    void deleteByMobileAndCorpId(String mobile, String corpId);

    void insertList(@Param("list") List<UserBuild> userBuildList);

}
