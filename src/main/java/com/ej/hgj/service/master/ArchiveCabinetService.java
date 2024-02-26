package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.ArchiveCabinet;

import java.util.List;

public interface ArchiveCabinetService {

    List<ArchiveCabinet> getList(ArchiveCabinet archiveCabinet);

    void save(ArchiveCabinet archiveCabinet);

    void update(ArchiveCabinet archiveCabinet);

    void delete(String id);


}
