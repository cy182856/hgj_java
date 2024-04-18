package com.ej.hgj.dao.cstInto;

import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstIntoDaoMapper {

    List<CstInto> getByCstCode(String cstCode);

    List<CstInto> getByCstCodeAndTime(String cstCode);

    List<CstInto> getList(CstInto cstInto);

    void delete(String id);

    void save(CstInto cstInto);

    void deleteByCstCodeAndWxOpenId(String cstCode, String wxOpenId);

    CstInto getById(String id);

    void update(CstInto cstInto);

}
