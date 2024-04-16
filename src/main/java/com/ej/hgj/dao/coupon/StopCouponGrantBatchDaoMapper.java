package com.ej.hgj.dao.coupon;

import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.coupon.StopCouponGrantBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StopCouponGrantBatchDaoMapper {

    StopCouponGrantBatch getById(String id);

    List<StopCouponGrantBatch> getList(StopCouponGrantBatch stopCouponGrantBatch);

    void save(StopCouponGrantBatch stopCouponGrantBatch);

    void update(StopCouponGrantBatch stopCouponGrantBatch);

    void delete(String id);

    void insertList(@Param("list") List<StopCouponGrantBatch> stopCouponGrantBatchList);


}
