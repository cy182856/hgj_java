package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.DeptInfoDaoMapper;
import com.ej.hgj.entity.master.DeptInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptInfoServiceImpl implements DeptInfoService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeptInfoDaoMapper deptInfoDaoMapper;


    public List<DeptInfo> getList(DeptInfo deptInfo){
        return deptInfoDaoMapper.getList(deptInfo);
    }

    @Override
    public void save(DeptInfo deptInfo) {
        deptInfoDaoMapper.save(deptInfo);
    }

    @Override
    public void update(DeptInfo deptInfo) {
        deptInfoDaoMapper.update(deptInfo);
    }

    @Override
    public void delete(String id) {
        deptInfoDaoMapper.delete(id);
    }

}
