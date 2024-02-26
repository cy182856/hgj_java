package com.ej.hgj.dao.cron;

import com.ej.hgj.entity.cron.Cron;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CronDaoMapper {

    Cron getByType(String type);

}
