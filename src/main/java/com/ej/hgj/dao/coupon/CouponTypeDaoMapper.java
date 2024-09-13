package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.coupon.CouponType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CouponTypeDaoMapper {

    CouponType getById(String id);

    List<CouponType> getList(CouponType couponType);


}
