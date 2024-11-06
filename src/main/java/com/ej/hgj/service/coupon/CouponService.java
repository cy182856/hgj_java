package com.ej.hgj.service.coupon;



import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.coupon.CouponGrantBatch;

public interface CouponService {

    void couponGrant(CouponGrantBatch couponGrantBatch);

    void deleteBatch(String id);

}
