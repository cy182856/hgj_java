package com.ej.hgj.dao.config;

import com.ej.hgj.entity.config.ConstantConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ConstantConfDaoMapper {

    ConstantConfig getByKey(String configKey);

    ConstantConfig getByProNumAndKey(String proNum, String configKey);

}
