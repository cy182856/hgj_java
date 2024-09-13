package com.ej.hgj.dao.config;

import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.config.ProNeighConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProNeighConfDaoMapper {

    ProNeighConfig getById(String id);

    ProNeighConfig getByProjectNum(String id);

    List<ProNeighConfig> getList(ProNeighConfig proNeighConfig);

    ProNeighConfig getByNeighNo(String neighNo);

}
