package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CouponDaoMapper {

    Coupon getById(String id);

    List<Coupon> getList(Coupon coupon);

    void save(Coupon coupon);

    void update(Coupon coupon);

    void delete(String id);

}
