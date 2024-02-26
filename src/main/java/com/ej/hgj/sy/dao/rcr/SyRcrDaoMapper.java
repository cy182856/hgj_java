package com.ej.hgj.sy.dao.rcr;

import com.ej.hgj.entity.rcr.SyRcr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SyRcrDaoMapper {

    List<SyRcr> getList();

}
