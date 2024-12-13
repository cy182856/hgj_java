package com.ej.hgj.task.service;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardCstBatchDaoMapper;
import com.ej.hgj.dao.card.CardCstDaoMapper;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.card.CardCstBatch;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CardTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CardDaoMapper cardDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private CardCstDaoMapper cardCstDaoMapper;

    @Autowired
    private CardCstBatchDaoMapper cardCstBatchDaoMapper;

    public void cardTask() {
        logger.info("----------------------发卡定时任务处理开始--------------------------- ");
        // 当前年
        Integer year = Integer.valueOf(DateUtils.strY());
        // 发卡年份
        String[] years = {year+""};
        // 发卡月份
        String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
        // 查询所有卡
        List<Card> cardList = cardDaoMapper.getList(new Card());
        for (Card card : cardList) {
            List<String> expDate = new ArrayList<>();
            Integer cardId = Integer.valueOf(card.getId());

            // 游泳卡
            if(cardId == 1){
                expDate = Arrays.asList(years);
            }
            // 停车卡
            if (cardId == 2) {
                for(int i = 0 ; i< years.length; i++){
                    for(int j = 0; j<months.length; j++){
                        expDate.add(years[i] + "-" + months[j]);
                    }
                }
            }
            for (String ed : expDate) {
                sendCard(card,ed);
            }
        }

        // 删除无效卡批次
        cardCstBatchDaoMapper.deleteByExpDate(year+"");
        logger.info("----------------------发卡定时任务处理结束--------------------------- ");
    }

    public void sendCard(Card card,String expDate){
        Integer cardId = Integer.valueOf(card.getId());
        // 根据项目号查询客户
        List<HgjCst> hgjCstList = hgjCstDaoMapper.findByProNum(card.getProNum());
        // 根据卡id查询所有发卡客户
        CardCst cardCstParam = new CardCst();
        cardCstParam.setProNum(card.getProNum());
        cardCstParam.setCardType(card.getType());
        cardCstParam.setCardId(cardId);
        List<CardCst> cardCstListByCardId = cardCstDaoMapper.getList(cardCstParam);
        // 根据卡id、有效年月查询所有发卡客户批次
        CardCstBatch cardCstBatchParam = new CardCstBatch();
        cardCstBatchParam.setCardId(cardId);
        cardCstBatchParam.setExpDate(expDate);
        List<CardCstBatch> cardCstBatchListByCardIdAndExpDate = cardCstBatchDaoMapper.getList(cardCstBatchParam);
        // 发卡客户
        List<CardCst> cardCstList = new ArrayList<>();
        // 发卡客户批次
        List<CardCstBatch> cardCstBatchList = new ArrayList<>();
        // 插入客户卡
        for (HgjCst hgjCst : hgjCstList) {
            String cstCode = hgjCst.getCode();
            String cardCode = DateUtils.strYmd() + card.getType() + cstCode;
            CardCst cc = new CardCst();
            cc.setId(TimestampGenerator.generateSerialNumber());
            cc.setProNum(card.getProNum());
            cc.setCardId(cardId);
            cc.setCardType(card.getType());
            cc.setCardCode(cardCode);
            cc.setCstCode(cstCode);
            cc.setIsExp(1);
            cc.setCreateTime(new Date());
            cc.setCreateBy("");
            cc.setUpdateTime(new Date());
            cc.setUpdateBy("");
            cc.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            // 根据客户编号查询发卡客户
            List<CardCst> cardCstListByCardIdFilter = cardCstListByCardId.stream().filter(cardCst -> cardCst.getCstCode().equals(cstCode)).collect(Collectors.toList());
            // 如果未查询到卡信息
            if (cardCstListByCardIdFilter.isEmpty()) {
                // 保存卡新增集合
                cardCstList.add(cc);
                // 新增卡批次
                cardCstBatchList = savaCardBatch(cardCstBatchListByCardIdAndExpDate,cardCstBatchList,card,cstCode,expDate,cardCode,cardId);
            }else {
                // 客户卡已存在处理
                // 查询已存在卡信息
                CardCst cardCst = cardCstListByCardIdFilter.get(0);
                // 新增卡批次
                cardCstBatchList = savaCardBatch(cardCstBatchListByCardIdAndExpDate,cardCstBatchList,card,cstCode,expDate,cardCst.getCardCode(),cardId);
            }

        }
        if (!cardCstList.isEmpty()) {
            cardCstDaoMapper.insertList(cardCstList);
            logger.info("发卡成功:" + JSONObject.toJSONString(cardCstList));
        }
        if (!cardCstBatchList.isEmpty()) {
            cardCstBatchDaoMapper.insertList(cardCstBatchList);
            logger.info("发卡批次成功:" + JSONObject.toJSONString(cardCstBatchList));
        }

    }

    public List<CardCstBatch> savaCardBatch(List<CardCstBatch> cardCstBatchListByCardIdAndExpDate, List<CardCstBatch> cardCstBatchList, Card card, String cstCode, String expDate, String cardCode, Integer cardId){
        // 新增卡批次
        CardCstBatch batch = new CardCstBatch();
        batch.setId(TimestampGenerator.generateSerialNumber());
        batch.setProNum(card.getProNum());
        batch.setCardType(card.getType());
        batch.setCardId(cardId);
        batch.setCardCode(cardCode);
        batch.setCstCode(cstCode);
        batch.setTotalNum(0);
        batch.setApplyNum(0);
        batch.setExpDate(expDate);
        batch.setCreateTime(new Date());
        batch.setCreateBy("");
        batch.setUpdateTime(new Date());
        batch.setUpdateBy("");
        batch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        // 根据客户编号查询发卡批次客户
        List<CardCstBatch> cardCstBatchListByCardIdAndExpDateFilter = cardCstBatchListByCardIdAndExpDate.stream().filter(cardCstBatch -> cardCstBatch.getCstCode().equals(cstCode)).collect(Collectors.toList());
        // 如果未查到卡批次，添加卡批次集合
        if (cardCstBatchListByCardIdAndExpDateFilter.isEmpty()) {
            cardCstBatchList.add(batch);
        }
        return cardCstBatchList;
    }

}
