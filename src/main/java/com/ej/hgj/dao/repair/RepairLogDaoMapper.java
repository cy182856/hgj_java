package com.ej.hgj.dao.repair;

import com.ej.hgj.entity.repair.RepairLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RepairLogDaoMapper {

    RepairLog getByRepNum(String id);

    List<RepairLog> getList(RepairLog repairLog);

    void save(RepairLog repairLog);

    void update(RepairLog repairLog);

    void delete(String id);

    List<RepairLog> getListByStatusAndTime(RepairLog repairLog);

}
