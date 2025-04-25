package com.ej.hgj.dao.carpay;

import com.ej.hgj.entity.carpay.ParkPayOrder;
import com.ej.hgj.entity.cstInto.CstInto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ParkPayOrderDaoMapper {

    ParkPayOrder getById(String id);

    List<ParkPayOrder> getList (ParkPayOrder parkPayOrder);

}
