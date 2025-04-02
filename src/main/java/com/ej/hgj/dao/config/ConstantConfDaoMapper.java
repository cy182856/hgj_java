package com.ej.hgj.dao.config;

import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ConstantConfDaoMapper {

    ConstantConfig getByKey(String configKey);

    ConstantConfig getByProNumAndKey(String proNum, String configKey);

    List<ConstantConfig> getList(ConstantConfig constantConfig);

    void update(ConstantConfig constantConfig);

}
