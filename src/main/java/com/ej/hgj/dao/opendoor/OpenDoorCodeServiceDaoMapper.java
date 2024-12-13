package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorCodeService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorCodeServiceDaoMapper {

    List<OpenDoorCodeService> getList(OpenDoorCodeService openDoorCodeService);

    void save(OpenDoorCodeService openDoorCodeService);

}
