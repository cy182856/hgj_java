package com.ej.hgj.dao.sum;

import com.ej.hgj.entity.sum.SumFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SumFileDaoMapper {

    List<SumFile> getList(SumFile sumFile);

    void save(SumFile sumFile);

    void update(SumFile sumFile);

    void delete(String id);

    SumFile findById(String id);

}
