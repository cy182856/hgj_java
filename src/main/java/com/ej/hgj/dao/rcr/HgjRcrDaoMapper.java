package com.ej.hgj.dao.rcr;

import com.ej.hgj.entity.rcr.SyRcr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjRcrDaoMapper {

    void insertList(@Param("list") List<SyRcr> syRcrList);

    void delete();

}
