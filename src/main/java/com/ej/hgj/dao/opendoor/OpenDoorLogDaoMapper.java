package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.opendoor.OpenDoorLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorLogDaoMapper {

    OpenDoorLog getById(String id);

    List<OpenDoorLog> getList(OpenDoorLog openDoorLog);

    void save(OpenDoorLog openDoorLog);

    void update(OpenDoorLog openDoorLog);

    void delete(String id);

}
