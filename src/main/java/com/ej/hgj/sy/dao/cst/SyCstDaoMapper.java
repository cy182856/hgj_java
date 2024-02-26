package com.ej.hgj.sy.dao.cst;

import com.ej.hgj.entity.cst.SyCst;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SyCstDaoMapper {

    List<SyCst> getList();

}
