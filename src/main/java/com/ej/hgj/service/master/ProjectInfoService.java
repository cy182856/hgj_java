package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.ProjectInfo;

import java.util.List;

public interface ProjectInfoService {

    List<ProjectInfo> getList(ProjectInfo projectInfo);

    void save(ProjectInfo projectInfo);

    void update(ProjectInfo projectInfo);

    void delete(String id);


}
