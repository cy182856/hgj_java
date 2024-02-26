package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.FileSecLevel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FileSecLevelDaoMapper {

    List<FileSecLevel> getList(FileSecLevel fileSecLevel);

    void save(FileSecLevel fileSecLevel);

    void update(FileSecLevel fileSecLevel);

    void delete(String id);

}
