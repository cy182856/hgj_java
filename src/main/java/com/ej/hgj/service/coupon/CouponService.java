package com.ej.hgj.service.coupon;



import com.ej.hgj.entity.coupon.Coupon;

public interface CouponService {

    void couponGrant(Coupon coupon);

    void deleteBatch(String id);

}
