package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ProjectUnitDaoMapper;
import com.ej.hgj.entity.master.ProjectUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectUnitServiceImpl implements ProjectUnitService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectUnitDaoMapper projectUnitDaoMapper;


    public List<ProjectUnit> getList(ProjectUnit projectUnit){
        return projectUnitDaoMapper.getList(projectUnit);
    }

    @Override
    public void save(ProjectUnit projectUnit) {
        projectUnitDaoMapper.save(projectUnit);
    }

    @Override
    public void update(ProjectUnit projectUnit) {
        projectUnitDaoMapper.update(projectUnit);
    }

    @Override
    public void delete(String id) {
        projectUnitDaoMapper.delete(id);
    }

}
