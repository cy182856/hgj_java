package com.ej.hgj.dao.opendoor;

import com.ej.hgj.entity.opendoor.ExternalPersonInfo;
import com.ej.hgj.entity.opendoor.OpenDoorCodeService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ExternalPersonInfoDaoMapper {

    List<ExternalPersonInfo> getList(ExternalPersonInfo externalPersonInfo);

    void save(ExternalPersonInfo externalPersonInfo);

    void delete(ExternalPersonInfo externalPersonInfo);

}
