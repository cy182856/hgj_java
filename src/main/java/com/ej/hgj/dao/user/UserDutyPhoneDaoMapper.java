package com.ej.hgj.dao.user;

import com.ej.hgj.entity.user.UserDutyPhone;
import com.ej.hgj.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserDutyPhoneDaoMapper {

    UserDutyPhone getByMobile(String mobile);

    UserDutyPhone getByUserId(String userId);

    UserDutyPhone getByMobileAndCorpId(String mobile, String corpId);

    List<UserDutyPhone> getList(UserDutyPhone userDutyPhone);

    void save(UserDutyPhone userDutyPhone);

    void update(UserDutyPhone userDutyPhone);

    void delete(String mobile);

    void deleteByUserId(String userId);

    void deleteByMobileAndCorpId(String mobile, String corpId);

}
