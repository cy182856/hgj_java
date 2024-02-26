package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.ArchiveType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ArchiveTypeDaoMapper {

    List<ArchiveType> getList(ArchiveType archiveType);

    void save(ArchiveType archiveType);

    void update(ArchiveType archiveType);

    void delete(String id);

}
