package com.ej.hgj.dao.carpay;

import com.ej.hgj.entity.carpay.ParkPayOrderTemp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ParkPayOrderTempDaoMapper {

    ParkPayOrderTemp getParkPayOrderTemp(String orderId);

    void save(ParkPayOrderTemp parkPayOrderTemp);

    void update(ParkPayOrderTemp parkPayOrderTemp);
}
