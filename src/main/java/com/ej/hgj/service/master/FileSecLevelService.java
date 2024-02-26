package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.FileSecLevel;

import java.util.List;

public interface FileSecLevelService {

    List<FileSecLevel> getList(FileSecLevel fileSecLevel);

    void save(FileSecLevel fileSecLevel);

    void update(FileSecLevel fileSecLevel);

    void delete(String id);


}
