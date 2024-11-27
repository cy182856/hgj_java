package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.CardCstBatch;
import com.ej.hgj.entity.card.CardCstBill;
import com.ej.hgj.entity.card.CardSubDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardCstBillDaoMapper {

    void insertList(@Param("list") List<CardCstBill> CardCstBillList);

    void save(CardCstBill cardCstBill);

    CardCstBill getByCardCodeAndProNum(String cardCode, String proNum);

    List<CardCstBill> getList(CardCstBill cardCstBill);

}
