package com.ej.hgj.dao.carrenew;

import com.ej.hgj.entity.carpay.ParkPayOrder;
import com.ej.hgj.entity.carrenew.CarRenewOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CarRenewOrderDaoMapper {

    CarRenewOrder getById(String id);

    List<CarRenewOrder> getList (CarRenewOrder carRenewOrder);

}
