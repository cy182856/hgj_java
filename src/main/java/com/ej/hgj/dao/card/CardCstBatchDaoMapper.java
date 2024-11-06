package com.ej.hgj.dao.card;

import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.card.CardCstBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CardCstBatchDaoMapper {

    CardCstBatch getById(String id);

    List<CardCstBatch> getList(CardCstBatch cardCstBatch);

    void insertList(@Param("list") List<CardCstBatch> CardCstBatchList);

    void update(CardCstBatch cardCstBatch);

    void batchRecharge(CardCstBatch cardCstBatch);

    void recharge(CardCstBatch cardCstBatch);

}
