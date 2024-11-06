package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.CouponGrant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CouponGrantDaoMapper {

    CouponGrant getById(String id);

    List<CouponGrant> getList(CouponGrant couponGrant);

    void save(CouponGrant couponGrant);

    void update(CouponGrant couponGrant);

    void updateByBatchId(CouponGrant couponGrant);

    void delete(String id);

    void deleteByBatchId(String batchId);

    void insertList(@Param("list") List<CouponGrant> couponGrantList);

    List<CouponGrant> getListByExpDate(String startTime);


}
