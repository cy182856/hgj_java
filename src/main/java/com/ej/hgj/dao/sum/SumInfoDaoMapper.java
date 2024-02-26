package com.ej.hgj.dao.sum;

import com.ej.hgj.entity.sum.SumInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Mapper
@Component
public interface SumInfoDaoMapper {

    List<SumInfo> getList(SumInfo sumInfo);

    void save(SumInfo sumInfo);

    void update(SumInfo sumInfo);

    void delete(String id);

    SumInfo findById(String id);

}
