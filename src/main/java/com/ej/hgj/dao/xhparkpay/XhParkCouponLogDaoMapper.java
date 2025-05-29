package com.ej.hgj.dao.xhparkpay;

import com.ej.hgj.entity.xhparkpay.XhParkCouponLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface XhParkCouponLogDaoMapper {

    List<XhParkCouponLog> getList(XhParkCouponLog xhParkCouponLog);

}
