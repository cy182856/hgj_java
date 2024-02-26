package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.ProjectFloor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProjectFloorDaoMapper {

    List<ProjectFloor> getList(ProjectFloor projectFloor);

    void save(ProjectFloor projectFloor);

    void update(ProjectFloor projectFloor);

    void delete(String id);

}
