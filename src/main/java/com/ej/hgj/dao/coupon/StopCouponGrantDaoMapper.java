package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.StopCoupon;
import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.gonggao.Gonggao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StopCouponGrantDaoMapper {

    StopCouponGrant getById(String id);

    List<StopCouponGrant> getList(StopCouponGrant stopCouponGrant);

    void save(StopCouponGrant stopCouponGrant);

    void update(StopCouponGrant stopCouponGrant);

    void delete(String id);

    void insertList(@Param("list") List<StopCouponGrant> stopCouponGrantList);


}
