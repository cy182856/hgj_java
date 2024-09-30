package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardDaoMapper {

    Card getById(Integer id);

    List<Card> getList(Card card);
}
