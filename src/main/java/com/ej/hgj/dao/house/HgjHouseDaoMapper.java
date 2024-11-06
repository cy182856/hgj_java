package com.ej.hgj.dao.house;

import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.house.HgjHouse;
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

    List<HgjHouse> queryBuilding (String orgId);

    List<HgjHouseUnit> queryUnit (String orgId);

    List<HgjHouseFloor> queryFloor (String orgId);

    List<HgjHouse> getByCstIntoId(String cstIntoId);

    List<HgjHouse> queryRoomNum (String budId);

    List<HgjHouseRoomInfo> queryRoomNumAll(String orgId);

    List<HgjHouse> getList(HgjHouse hgjHouse);

}
