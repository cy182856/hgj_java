package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.StopCoupon;
import com.ej.hgj.entity.gonggao.GonggaoType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StopCouponDaoMapper {

    StopCoupon getById(String id);

    List<StopCoupon> getList(StopCoupon stopCoupon);

    void save(StopCoupon stopCoupon);

    void update(StopCoupon stopCoupon);

    void delete(String id);

}
