package com.ej.hgj.dao.house;

import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.house.SyHouse;
import com.ej.hgj.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjHouseDaoMapper {

    HgjHouse findById(String id);

    void insertList(@Param("list") List<HgjHouse> syHouseList);

    void delete();

    // List<HgjHouse> getList(HgjHouse hgjHouse);


    List<HgjHouse> queryBuilding (String orgId);

    List<HgjHouse> getByCstIntoId(String cstIntoId);








}
