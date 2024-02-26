package com.ej.hgj.service.test;

import com.ej.hgj.dao.test.TestDaoMapper;
import com.ej.hgj.entity.test.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestDaoMapper testDaoMapper;

    public TestEntity getById(Integer id){
        return testDaoMapper.getById(id);
    }

    public List<TestEntity> getList(){
        return testDaoMapper.getList();
    }

    public void save(TestEntity testEntity){
        testDaoMapper.save(testEntity);
    }

    public void update(TestEntity testEntity){
        testDaoMapper.update(testEntity);
    }

    public void delete(Integer id){
        testDaoMapper.delete(id);
    }
}
