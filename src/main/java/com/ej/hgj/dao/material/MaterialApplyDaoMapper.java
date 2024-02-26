package com.ej.hgj.dao.material;

import com.ej.hgj.entity.workord.MaterialApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MaterialApplyDaoMapper {

    void insertList(@Param("list") List<MaterialApply> materialApplyList);


}
