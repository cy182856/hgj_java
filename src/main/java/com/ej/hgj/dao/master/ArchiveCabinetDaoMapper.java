package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.ArchiveCabinet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ArchiveCabinetDaoMapper {

    List<ArchiveCabinet> getList(ArchiveCabinet archiveCabinet);

    void save(ArchiveCabinet archiveCabinet);

    void update(ArchiveCabinet archiveCabinet);

    void delete(String id);

}
