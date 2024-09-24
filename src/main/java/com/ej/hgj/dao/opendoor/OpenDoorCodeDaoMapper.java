package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorCodeDaoMapper {

    // 接口调用-查询当天有效快速码
    QuickCodeInfo getByQuickCode(String quickCode, String expDate);

    List<OpenDoorCode> getList(OpenDoorCode openDoorCode);

    void save(OpenDoorCode openDoorCode);

    void update(OpenDoorCode openDoorCode);

}
