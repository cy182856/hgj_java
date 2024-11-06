package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.cstInto.CstInto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardCstDaoMapper {

    CardCst getById(String id);

    List<CardCst> getList(CardCst cardCst);

    void insertList(@Param("list") List<CardCst> cardCstList);

    void updateTotalNumByCardIdAndTag(CardCst cardCst);

    void updateStartEndTimeByCardIdAndTag(CardCst cardCst);

    void update(CardCst cardCst);

    void updateIsExp(CardCst cardCst);

    List<CardCst> batchRechargeCstList(CardCst cardCst);


}
