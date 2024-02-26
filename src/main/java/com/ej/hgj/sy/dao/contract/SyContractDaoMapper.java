package com.ej.hgj.sy.dao.contract;

import com.ej.hgj.entity.contract.SyContract;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SyContractDaoMapper {

    List<SyContract> getList();

}
