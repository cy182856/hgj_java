package com.ej.hgj.dao.cst;

import com.ej.hgj.entity.contract.HgjContract;
import com.ej.hgj.entity.contract.SyContract;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cst.SyCst;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.repair.RepairLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjCstDaoMapper {

    HgjCst findByCstCode(String cstCode);

    void insertList(@Param("list") List<SyCst> syCstList);

    void delete();

    List<HgjCst> getList(HgjCst hgjCst);

    HgjCst getCstNameByResId(String resId);


}
