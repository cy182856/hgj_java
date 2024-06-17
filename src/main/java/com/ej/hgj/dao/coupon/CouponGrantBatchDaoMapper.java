package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.CouponGrantBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CouponGrantBatchDaoMapper {

    CouponGrantBatch getById(String id);

    List<CouponGrantBatch> getList(CouponGrantBatch stopCouponGrantBatch);

    void save(CouponGrantBatch couponGrantBatch);

    void update(CouponGrantBatch couponGrantBatch);

    void delete(String id);

    void insertList(@Param("list") List<CouponGrantBatch> couponGrantBatchList);


}
