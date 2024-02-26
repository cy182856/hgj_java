package com.ej.hgj.service.test;

import com.ej.hgj.entity.test.TestEntity;

import java.util.List;

public interface TestService {

    TestEntity getById(Integer id);

    List<TestEntity> getList();

    void save(TestEntity testEntity);

    void update(TestEntity testEntity);

    void delete(Integer id);
}
