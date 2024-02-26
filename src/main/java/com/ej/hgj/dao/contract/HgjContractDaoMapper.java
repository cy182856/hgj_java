package com.ej.hgj.dao.contract;

import com.ej.hgj.entity.contract.HgjContract;
import com.ej.hgj.entity.contract.SyContract;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjContractDaoMapper {

    void insertList(@Param("list") List<SyContract> syContractList);

    void delete();

    List<HgjContract> getList(HgjContract hgjContract);

}
