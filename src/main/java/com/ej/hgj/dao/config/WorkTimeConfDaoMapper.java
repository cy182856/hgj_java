package com.ej.hgj.dao.config;

import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.config.WorkTimeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WorkTimeConfDaoMapper {

    WorkTimeConfig getWorkTime(Integer type);

    List<WorkTimeConfig> getList(WorkTimeConfig workTimeConfig);

    void update(WorkTimeConfig workTimeConfig);
}
