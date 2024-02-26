package com.ej.hgj.dao.house;

import com.ej.hgj.entity.house.CstInto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstIntoMapper {

    CstInto getById(String id);

    CstInto getByCstCode(String cstCode);

    List<CstInto> getList(CstInto cstInto);

    void save(CstInto cstInto);

    void update(CstInto cstInto);

    void delete(String id);
}