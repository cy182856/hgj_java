package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.DeptInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DeptInfoDaoMapper {

    List<DeptInfo> getList(DeptInfo deptInfo);

    void save(DeptInfo deptInfo);

    void update(DeptInfo deptInfo);

    void delete(String id);

}
