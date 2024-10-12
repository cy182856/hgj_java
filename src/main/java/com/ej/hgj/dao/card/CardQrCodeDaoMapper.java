package com.ej.hgj.dao.card;

import com.ej.hgj.entity.active.CouponQrCode;
import com.ej.hgj.entity.card.CardQrCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardQrCodeDaoMapper {

    List<CardQrCode> getList(CardQrCode cardQrCode);

    void update(CardQrCode cardQrCode);
}
