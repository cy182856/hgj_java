package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardTypeDaoMapper {

    CardType getById(Integer id);

    List<CardType> getList(CardType cardType);
}
