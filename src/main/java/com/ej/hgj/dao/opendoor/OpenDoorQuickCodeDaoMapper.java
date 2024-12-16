package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorQuickCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface OpenDoorQuickCodeDaoMapper {

    QuickCodeInfo getByQuickCodeApi(String quickCode, String expDate);

    OpenDoorQuickCode getByQuickCode(String quickCode);

    List<OpenDoorQuickCode> getList(OpenDoorQuickCode openDoorQuickCode);

    List<OpenDoorQuickCode> getQrCodeByExpDate(OpenDoorQuickCode openDoorQuickCode);

    List<OpenDoorQuickCode> getQrCodeByExpDate2(OpenDoorQuickCode openDoorQuickCode);

    void updateByQuickCode(OpenDoorQuickCode openDoorQuickCode);

    void deleteQuickCode();

}
