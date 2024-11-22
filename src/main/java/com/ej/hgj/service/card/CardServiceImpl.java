package com.ej.hgj.service.card;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardCstBatchDaoMapper;
import com.ej.hgj.dao.card.CardCstBillDaoMapper;
import com.ej.hgj.dao.card.CardCstDaoMapper;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.qn.QnDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubMenuDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.card.CardCstBatch;
import com.ej.hgj.entity.card.CardCstBill;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.qn.Qn;
import com.ej.hgj.service.qn.QnService;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.vo.card.CardReqVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CardServiceImpl implements CardService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CardDaoMapper cardDaoMapper;

    @Autowired
    private CardCstDaoMapper cardCstDaoMapper;

    @Autowired
    private CardCstBatchDaoMapper cardCstBatchDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private CardCstBillDaoMapper cardCstBillDaoMapper;

    @Override
    public AjaxResult cardBulkOperation(CardReqVo cardReqVo, String userId) {
        Integer cardId = cardReqVo.getCardId();
        Integer cardOption = cardReqVo.getCardOption();
        // 卡充值才会有
        Integer totalNum = cardReqVo.getTotalNum();
        String expDate = cardReqVo.getExpDate();
        // 卡信息
        Card card = cardDaoMapper.getById(cardId);
        // 根据项目号查询客户
        HgjCst hgjCst = new HgjCst();
        hgjCst.setOrgId(card.getProNum());
        List<HgjCst> hgjCstList = hgjCstDaoMapper.getList(hgjCst);
        // 根据卡id查询所有发卡客户
        CardCst cardCstParam = new CardCst();
        cardCstParam.setCardId(cardId);
        cardCstParam.setIsExp(1);
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
        // 已选择客户
        List<String> cstCodeList = cardReqVo.getCstCodeList();
        // 发卡
        if(cardOption == 1){
            for(int i = 0; i<cstCodeList.size(); i++){
                // 保存客户卡
                String cstCode = cstCodeList.get(i);
                CardCst cc = new CardCst();
                cc.setId(TimestampGenerator.generateSerialNumber());
                cc.setProNum(card.getProNum());
                cc.setCardId(cardId);
                cc.setCardType(card.getType());
                cc.setCardCode(DateUtils.strYmd() + card.getType() + cstCode);
                cc.setCstCode(cstCode);
                cc.setIsExp(1);
                cc.setCreateTime(new Date());
                cc.setCreateBy("");
                cc.setUpdateTime(new Date());
                cc.setUpdateBy("");
                cc.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 查询客户对应项目号，客户项目号与卡的项目号相同才可发卡
                List<HgjCst> hgjCstListFilter = hgjCstList.stream().filter(hgjCst1 -> hgjCst1.getCode().equals(cstCode)).collect(Collectors.toList());
                // 根据客户编号查询发卡客户,如果有数据就不再发卡
                List<CardCst> cardCstListByCardIdFilter = cardCstListByCardId.stream().filter(cardCst -> cardCst.getCstCode().equals(cstCode)).collect(Collectors.toList());
                if(cardCstListByCardIdFilter.isEmpty() && !hgjCstListFilter.isEmpty()) {
                    cardCstList.add(cc);
                }
                // 保存卡批次
                CardCstBatch batch = new CardCstBatch();
                batch.setId(TimestampGenerator.generateSerialNumber());
                batch.setProNum(card.getProNum());
                batch.setCardType(card.getType());
                batch.setCardId(cardId);
                batch.setCardCode(DateUtils.strYmd() + card.getType() + cstCode);
                batch.setCstCode(cstCode);
                batch.setTotalNum(0);
                batch.setApplyNum(0);
                batch.setExpDate(expDate);
                batch.setCreateTime(new Date());
                batch.setCreateBy("");
                batch.setUpdateTime(new Date());
                batch.setUpdateBy("");
                batch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据客户编号查询发卡客户,如果有数据就不再发卡
                List<CardCstBatch> cardCstBatchListByCardIdAndExpDateFilter = cardCstBatchListByCardIdAndExpDate.stream().filter(cardCstBatch -> cardCstBatch.getCstCode().equals(cstCode)).collect(Collectors.toList());
                if(cardCstBatchListByCardIdAndExpDateFilter.isEmpty() && !hgjCstListFilter.isEmpty()) {
                    cardCstBatchList.add(batch);
                }
            }
            if(!cardCstList.isEmpty()){
                cardCstDaoMapper.insertList(cardCstList);
                logger.info("发卡成功:"+ JSONObject.toJSONString(cardCstList));
            }
            if(!cardCstBatchList.isEmpty()){
                cardCstBatchDaoMapper.insertList(cardCstBatchList);
                logger.info("发卡批次成功:"+ JSONObject.toJSONString(cardCstBatchList));
            }
        }

        // 卡禁用
        if(cardOption == 2){
            CardCst cardCstParamDis = new CardCst();
            cardCstParamDis.setIsExp(0);
            cardCstParamDis.setProNum(card.getProNum());
            cardCstParamDis.setCardId(cardId);
            cardCstParamDis.setCstCodeList(cstCodeList);
            cardCstParamDis.setUpdateTime(new Date());
            cardCstParamDis.setUpdateBy(userId);
            cardCstDaoMapper.updateIsExp(cardCstParamDis);
            logger.info("卡禁用成功:"+ JSONObject.toJSONString(cstCodeList));
        }

        // 卡恢复
        if(cardOption == 3){
            CardCst cardCstParamRestore = new CardCst();
            cardCstParamRestore.setIsExp(1);
            cardCstParamRestore.setProNum(card.getProNum());
            cardCstParamRestore.setCardId(cardId);
            cardCstParamRestore.setCstCodeList(cstCodeList);
            cardCstParamRestore.setUpdateTime(new Date());
            cardCstParamRestore.setUpdateBy(userId);
            cardCstDaoMapper.updateIsExp(cardCstParamRestore);
            logger.info("卡恢复成功:"+ JSONObject.toJSONString(cstCodeList));
        }

        // 批量充值
        if(cardOption == 4){
            // 过滤符合充值要求条件的客户编号
            CardCst cardCst = new CardCst();
            cardCst.setProNum(card.getProNum());
            cardCst.setCardId(cardId);
            cardCst.setCstCodeList(cstCodeList);
            List<CardCst> cardCstRechargeList = cardCstDaoMapper.batchRechargeCstList(cardCst);
            List<String> cstCodes = new ArrayList<>();
            List<CardCstBill> cardCstBillList = new ArrayList<>();
            if(!cardCstRechargeList.isEmpty()){
                for(CardCst c : cardCstRechargeList){
                    cstCodes.add(c.getCstCode());
                    // 卡账单
                    CardCstBill cardCstBill = new CardCstBill();
                    cardCstBill.setId(TimestampGenerator.generateSerialNumber());
                    cardCstBill.setProNum(card.getProNum());
                    cardCstBill.setCardType(card.getType());
                    cardCstBill.setCardId(cardId);
                    cardCstBill.setCardCode(c.getCardCode());
                    cardCstBill.setCstCode(c.getCstCode());
                    cardCstBill.setBillNum(totalNum);
                    cardCstBill.setBillType(1);
                    cardCstBill.setCreateTime(new Date());
                    cardCstBill.setCreateBy(userId);
                    cardCstBill.setUpdateTime(new Date());
                    cardCstBill.setUpdateBy(userId);
                    cardCstBill.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                    cardCstBillList.add(cardCstBill);
                }
            }
            if(!cstCodes.isEmpty()) {
                CardCstBatch cardCstBatchParamRecharge = new CardCstBatch();
                cardCstBatchParamRecharge.setCardId(cardId);
                cardCstBatchParamRecharge.setExpDate(expDate);
                cardCstBatchParamRecharge.setRechargeNum(totalNum);
                cardCstBatchParamRecharge.setCstCodeList(cstCodes);
                cardCstBatchParamRecharge.setProNum(card.getProNum());
                cardCstBatchParamRecharge.setUpdateTime(new Date());
                cardCstBatchParamRecharge.setUpdateBy(userId);
                cardCstBatchDaoMapper.batchRecharge(cardCstBatchParamRecharge);
                logger.info("卡充值成功:" + JSONObject.toJSONString(cstCodes));
            }else {
                logger.info("卡充值错误:没有符合充值条件的客户");
            }

            // 插入卡账单数据
            if(!cardCstBillList.isEmpty()) {
                cardCstBillDaoMapper.insertList(cardCstBillList);
                logger.info("卡账单插入成功:" + JSONObject.toJSONString(cardCstBillList));
            }
        }

        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @Override
    public AjaxResult cardRecharge(CardCst cardCst, String userId) {
        CardCst cc = cardCstDaoMapper.getById(cardCst.getId());
        if(cc != null && cc.getIsExp() == 1){
            CardCstBatch cardCstBatch = new CardCstBatch();
            cardCstBatch.setProNum(cc.getProNum());
            cardCstBatch.setCardType(cc.getCardType());
            cardCstBatch.setCardId(cc.getCardId());
            cardCstBatch.setCardCode(cc.getCardCode());
            cardCstBatch.setCstCode(cc.getCstCode());
            cardCstBatch.setExpDate(cardCst.getExpDate());
            cardCstBatch.setRechargeNum(cardCst.getRechargeNum());
            cardCstBatch.setUpdateTime(new Date());
            cardCstBatch.setUpdateBy(userId);
            cardCstBatchDaoMapper.recharge(cardCstBatch);
            logger.info("充值成功:"+ JSONObject.toJSONString(cc));

            CardCstBill cardCstBill = new CardCstBill();
            cardCstBill.setId(TimestampGenerator.generateSerialNumber());
            cardCstBill.setProNum(cc.getProNum());
            cardCstBill.setCardType(cc.getCardType());
            cardCstBill.setCardId(cc.getCardId());
            cardCstBill.setCardCode(cc.getCardCode());
            cardCstBill.setCstCode(cc.getCstCode());
            cardCstBill.setBillNum(cardCst.getRechargeNum());
            cardCstBill.setBillType(1);
            cardCstBill.setCreateTime(new Date());
            cardCstBill.setCreateBy(userId);
            cardCstBill.setUpdateTime(new Date());
            cardCstBill.setUpdateBy(userId);
            cardCstBill.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            cardCstBillDaoMapper.save(cardCstBill);
            logger.info("卡账单插入成功:" + JSONObject.toJSONString(cardCstBill));
        }
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
