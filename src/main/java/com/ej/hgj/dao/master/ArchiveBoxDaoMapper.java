package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.ArchiveBox;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ArchiveBoxDaoMapper {

    List<ArchiveBox> getList(ArchiveBox archiveBox);

    void save(ArchiveBox archiveBox);

    void update(ArchiveBox archiveBox);

    void delete(String id);

}
