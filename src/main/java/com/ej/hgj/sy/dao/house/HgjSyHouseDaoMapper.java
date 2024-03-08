package com.ej.hgj.sy.dao.house;

import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.house.SyHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjSyHouseDaoMapper {

    List<HgjHouse> getList(HgjHouse hgjHouse);


}
