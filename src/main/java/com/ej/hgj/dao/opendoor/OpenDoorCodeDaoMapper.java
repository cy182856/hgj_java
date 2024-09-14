package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorCodeDaoMapper {

    OpenDoorCode getById(String id);

    OpenDoorCode getByRandNum(String randNum);

    QuickCodeInfo getByQuickCode(String quickCode);

    List<OpenDoorCode> getList(OpenDoorCode openDoorCode);

    List<OpenDoorCode> getQrCodeByExpDate(OpenDoorCode openDoorCode);

    List<OpenDoorCode> getQrCodeByExpDate2(OpenDoorCode openDoorCode);

    List<OpenDoorCode> getCstRanNumList(OpenDoorCode openDoorCode);

    void save(OpenDoorCode openDoorCode);

    void update(OpenDoorCode openDoorCode);

    void updateByRandNum(String randNum);

    void delete(String id);

}
