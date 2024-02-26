package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.ArchiveType;

import java.util.List;

public interface ArchiveTypeService {

    List<ArchiveType> getList(ArchiveType archiveType);

    void save(ArchiveType archiveType);

    void update(ArchiveType archiveType);

    void delete(String id);


}
