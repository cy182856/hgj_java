package com.ej.hgj.dao.config;

import com.ej.hgj.entity.config.ProConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProConfDaoMapper {

    ProConfig getById(String id);

    ProConfig getByProjectNum(String id);

    ProConfig getByCorpId(String corpId);

    List<ProConfig> getList(ProConfig proConfig);

    void save(ProConfig proConfig);

    void update(ProConfig proConfig);

    void delete(String id);

}
