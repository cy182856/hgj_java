package com.ej.hgj.dao.visit;

import com.ej.hgj.entity.visit.VisitLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface VisitLogDaoMapper {

    List<VisitLog> getList(VisitLog visitLog);

}
