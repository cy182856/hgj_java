package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ProjectInfoDaoMapper;
import com.ej.hgj.entity.master.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectInfoDaoMapper projectInfoDaoMapper;


    public List<ProjectInfo> getList(ProjectInfo projectInfo){
        return projectInfoDaoMapper.getList(projectInfo);
    }

    @Override
    public void save(ProjectInfo projectInfo) {
        projectInfoDaoMapper.save(projectInfo);
    }

    @Override
    public void update(ProjectInfo projectInfo) {
        projectInfoDaoMapper.update(projectInfo);
    }

    @Override
    public void delete(String id) {
        projectInfoDaoMapper.delete(id);
    }

}
