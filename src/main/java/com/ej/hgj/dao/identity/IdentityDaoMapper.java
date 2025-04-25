package com.ej.hgj.dao.identity;

import com.ej.hgj.entity.identity.Identity;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface IdentityDaoMapper {

    Identity getByCode(String code);

    List<Identity> getList(Identity identity);

    List<Identity> getListByProNum(String proNum);

}
