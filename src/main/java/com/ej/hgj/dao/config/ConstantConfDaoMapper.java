package com.ej.hgj.dao.config;

import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.config.ProConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ConstantConfDaoMapper {

    ConstantConfig getByKey(String configKey);

    ConstantConfig getByProNumAndKey(String proNum, String configKey);

    List<ConstantConfig> getList(ConstantConfig constantConfig);

}
