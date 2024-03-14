package com.ej.hgj.dao.gonggao;

import com.ej.hgj.entity.gonggao.Gonggao;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GonggaoDaoMapper {

    Gonggao getById(String id);

    List<Gonggao> getList(Gonggao gonggao);

    void save(Gonggao gonggao);

    void update(Gonggao gonggao);

    void delete(String id);

    void isShow(String id);

    void notIsShow(String id);

    void insertList(@Param("list") List<Gonggao> gonggaoList);


}
