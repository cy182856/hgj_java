package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.opendoor.OpenDoorLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorLogDaoMapper {

    List<OpenDoorLog> getByCardNoList(String cardNo);

    List<OpenDoorLog> getByCardNoAndIsUnlock(String cardNo, String deviceNo);

    List<OpenDoorLog> getList(OpenDoorLog openDoorLog);

    void save(OpenDoorLog openDoorLog);

}
