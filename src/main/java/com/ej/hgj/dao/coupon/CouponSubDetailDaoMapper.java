package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.CouponSubDetail;
import com.ej.hgj.entity.coupon.CouponType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CouponSubDetailDaoMapper {

    List<CouponSubDetail> getList(CouponSubDetail couponSubDetail);

    List<CouponSubDetail> getByQrCodeIdList(String qrCodeId);

    void save(CouponSubDetail couponSubDetail);

}
