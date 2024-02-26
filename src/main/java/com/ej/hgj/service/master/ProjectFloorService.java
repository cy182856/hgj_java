package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.ProjectFloor;

import java.util.List;

public interface ProjectFloorService {

    List<ProjectFloor> getList(ProjectFloor projectFloor);

    void save(ProjectFloor projectFloor);

    void update(ProjectFloor projectFloor);

    void delete(String id);


}
