package com.ej.hgj.dao.cstInto;

import com.ej.hgj.entity.contract.SyContract;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstIntoHouseDaoMapper {


    List<CstIntoHouse> getList(CstIntoHouse cstIntoHouse);

    void delete(String id);

    void save(CstIntoHouse cstIntoHouse);

    void deleteByCstCodeAndWxOpenId(String cstCode, String wxOpenId);

    CstIntoHouse getById(String id);

    List<CstIntoHouse> getByCstIntoIdAndIntoStatus(String cstIntoId);

    void insertList(@Param("list") List<CstIntoHouse> cstIntoHouseList);

    void updateById(CstIntoHouse cstIntoHouse);





}
