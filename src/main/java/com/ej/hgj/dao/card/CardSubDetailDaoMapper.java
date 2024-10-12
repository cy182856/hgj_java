package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.CardSubDetail;
import com.ej.hgj.entity.coupon.CouponSubDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardSubDetailDaoMapper {

    List<CardSubDetail> getList(CardSubDetail cardSubDetail);

    List<CardSubDetail> getByQrCodeIdList(String qrCodeId);

    void save(CardSubDetail cardSubDetail);

}
