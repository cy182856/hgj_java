package com.ej.hgj.dao.parkpay;

import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.parkpay.ParkPayCstHour;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ParkPayCstHourDaoMapper {

    ParkPayCstHour getById(String id);

    List<ParkPayCstHour> getList(ParkPayCstHour parkPayCstHour);

    void insertList(@Param("list") List<ParkPayCstHour> parkPayCstHourList);

    void updateTotalNumByTag(ParkPayCstHour parkPayCstHour);

    void updateStartEndTimeByTag(ParkPayCstHour parkPayCstHour);

    void update(ParkPayCstHour parkPayCstHour);

}
