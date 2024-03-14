package com.ej.hgj.dao.gonggao;

import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GonggaoTypeDaoMapper {

    GonggaoType getById(String id);

    List<GonggaoType> getList(GonggaoType gonggaoType);

    void save(GonggaoType gonggaoType);

    void update(GonggaoType gonggaoType);

    void delete(String id);

}
