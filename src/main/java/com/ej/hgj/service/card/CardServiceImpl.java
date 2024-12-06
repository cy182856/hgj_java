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
import com.ej.hgj.entity.config.ConstantConfig;
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

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Override
    public AjaxResult cardBulkOperation(CardReqVo cardReqVo, String userId) {
        AjaxResult ajaxResult = new AjaxResult();
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
            ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.CARD_RECHARGE_MAX_NUM);
            Integer maxNum = Integer.valueOf(constantConfig.getConfigValue());
            if(totalNum > maxNum){
                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
                ajaxResult.setMessage("充值数不能大于" + maxNum);
                return ajaxResult;
            }
            // 查询需要充值的客户
            CardCst cardCst = new CardCst();
            cardCst.setProNum(card.getProNum());
            cardCst.setCardId(cardId);
            cardCst.setCstCodeList(cstCodeList);
            cardCst.setCardType(card.getType());
            List<CardCst> cardCstRechargeList = cardCstDaoMapper.batchRechargeCstList(cardCst);
            // 需要新增卡客户集合
            List<CardCst> cardCstList1 = new ArrayList<>();
            // 需要新增卡批次集合
            List<CardCstBatch> cardCstBatchList1 = new ArrayList<>();
            // 需要新增卡账单集合
            List<CardCstBill> cardCstBillList1 = new ArrayList<>();
            // 需要更新卡批次数量的客户编号
            List<String> updateCardBatchTotalNumList = new ArrayList<>();
            // 判断需要充值的客户不为空
            if(!cardCstRechargeList.isEmpty()){
                for(CardCst cstRecharge : cardCstRechargeList){
                    String cardCode = DateUtils.strYmd() + card.getType() + cstRecharge.getCstCode();
                    String cstCode = cstRecharge.getCstCode();
                    // 查询客户卡信息
                    List<CardCst> cardCstRechargeListFilter = cardCstRechargeList.stream().filter(cardCst1 -> cardCst1.getCstCode().equals(cstRecharge.getCstCode())).collect(Collectors.toList());
                    // 无卡处理
                    if(cardCstRechargeListFilter.isEmpty()){
                        // 新增客户卡
                        CardCst cardCst2 = new CardCst();
                        cardCst2.setId(TimestampGenerator.generateSerialNumber());
                        cardCst2.setProNum(card.getProNum());
                        cardCst2.setCardId(cardId);
                        cardCst2.setCardType(card.getType());
                        cardCst2.setCardCode(cardCode);
                        cardCst2.setCstCode(cstCode);
                        cardCst2.setIsExp(1);
                        cardCst2.setCreateTime(new Date());
                        cardCst2.setCreateBy("");
                        cardCst2.setUpdateTime(new Date());
                        cardCst2.setUpdateBy("");
                        cardCst2.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                        cardCstList1.add(cardCst2);
                        // 新增卡批次
                        CardCstBatch cardCstBatchAdd1 = addCardCstBatch(card.getProNum(),card.getType(),cardCode,cstCode,cardId,userId,totalNum,expDate);
                        cardCstBatchList1.add(cardCstBatchAdd1);
                        // 新增账单
                        CardCstBill cardCstBill1 = addCardCstBill(cardCstBatchAdd1.getId(), card.getProNum(),card.getType(),cardCode,cstCode,cardId,userId,totalNum);
                        cardCstBillList1.add(cardCstBill1);
                    // 有卡处理
                    }else {
                        // 根据卡号、类型、有效期、卡ID、项目、客户号集合查询卡批次
                        CardCstBatch cardCstBatch = new CardCstBatch();
                        cardCstBatch.setCardCode(cardCode);
                        cardCstBatch.setCardType(card.getType());
                        cardCstBatch.setCardId(cardId);
                        cardCstBatch.setExpDate(expDate);
                        cardCstBatch.setCstCodeList(cstCodeList);
                        cardCstBatch.setProNum(card.getProNum());
                        List<CardCstBatch> cstBatchList = cardCstBatchDaoMapper.getList(cardCstBatch);
                        // 整个卡批次有数据
                        if(!cstBatchList.isEmpty()){
                            List<CardCstBatch> cstBatchListFilter = cstBatchList.stream().filter(cardCstBatch1 -> cardCstBatch1.getCstCode().equals(cstCode)).collect(Collectors.toList());
                            // 客户无卡批次处理
                            if(cstBatchListFilter.isEmpty()){
                                // 查询客户卡信息
                                CardCst cardCst2 = cardCstRechargeListFilter.get(0);
                                // 新增卡批次
                                CardCstBatch cardCstBatchAdd2 = addCardCstBatch(card.getProNum(),card.getType(),cardCst2.getCardCode(),cstCode,cardId,userId,totalNum,expDate);
                                cardCstBatchList1.add(cardCstBatchAdd2);
                                // 新增账单
                                CardCstBill cardCstBill2 = addCardCstBill(cardCstBatchAdd2.getId(), card.getProNum(),card.getType(),cardCst2.getCardCode(),cstCode,cardId,userId,totalNum);
                                cardCstBillList1.add(cardCstBill2);
                            // 客户有卡批次
                            }else {
                                // 查询卡批次信息
                                CardCstBatch cardCstBatch3 = cstBatchListFilter.get(0);
                                // 更新卡批次次数
                                updateCardBatchTotalNumList.add(cstCode);
                                // 新增账单
                                CardCstBill cardCstBill3 = addCardCstBill(cardCstBatch3.getId(),card.getProNum(),card.getType(),cardCstBatch3.getCardCode(),cstCode,cardId,userId,totalNum);
                                cardCstBillList1.add(cardCstBill3);
                            }
                        // 整个卡批次无数据
                        }else{
                            // 查询客户卡信息
                            CardCst cardCst3 = cardCstRechargeListFilter.get(0);
                            // 新增卡批次
                            CardCstBatch cardCstBatchAdd3 = addCardCstBatch(card.getProNum(),card.getType(),cardCst3.getCardCode(),cstCode,cardId,userId,totalNum,expDate);
                            cardCstBatchList1.add(cardCstBatchAdd3);
                            // 新增账单
                            CardCstBill cardCstBill4 = addCardCstBill(cardCstBatchAdd3.getId(),card.getProNum(),card.getType(),cardCst3.getCardCode(),cstCode,cardId,userId,totalNum);
                            cardCstBillList1.add(cardCstBill4);
                        }
                    }
                }

                // 新增卡
                if(!cardCstList1.isEmpty()){
                    cardCstDaoMapper.insertList(cardCstList1);
                    logger.info("新增卡成功:"+ JSONObject.toJSONString(cardCstList1));
                }
                // 新增卡批次
                if (!cardCstBatchList1.isEmpty()) {
                    cardCstBatchDaoMapper.insertList(cardCstBatchList1);
                    logger.info("新增卡批次成功:" + JSONObject.toJSONString(cardCstBatchList1));
                }
                // 更新卡批次次数
                if(!updateCardBatchTotalNumList.isEmpty()) {
                    CardCstBatch cardCstBatchParamRecharge = new CardCstBatch();
                    cardCstBatchParamRecharge.setCardId(cardId);
                    cardCstBatchParamRecharge.setExpDate(expDate);
                    cardCstBatchParamRecharge.setRechargeNum(totalNum);
                    cardCstBatchParamRecharge.setCstCodeList(updateCardBatchTotalNumList);
                    cardCstBatchParamRecharge.setProNum(card.getProNum());
                    cardCstBatchParamRecharge.setUpdateTime(new Date());
                    cardCstBatchParamRecharge.setUpdateBy(userId);
                    cardCstBatchDaoMapper.batchRecharge(cardCstBatchParamRecharge);
                    logger.info("卡充值成功:" + JSONObject.toJSONString(updateCardBatchTotalNumList));
                }
                // 新增账单
                if(!cardCstBillList1.isEmpty()) {
                    cardCstBillDaoMapper.insertList(cardCstBillList1);
                    logger.info("新增卡账单成功:" + JSONObject.toJSONString(cardCstBillList1));
                }
            }
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @Override
    public AjaxResult cardRecharge(CardCst cardCstReq, String userId) {
        AjaxResult ajaxResult = new AjaxResult();
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.CARD_RECHARGE_MAX_NUM);
        Integer maxNum = Integer.valueOf(constantConfig.getConfigValue());
        Integer rechargeNum = cardCstReq.getRechargeNum();
        if(rechargeNum > maxNum){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("充值数不能大于" + maxNum);
            return ajaxResult;
        }
        CardCst cardCst = cardCstDaoMapper.getById(cardCstReq.getId());
        Card card = cardDaoMapper.getById(cardCst.getCardId());
        Integer cardId = Integer.valueOf(card.getId());
        CardCstBatch cardCstBatchPram = new CardCstBatch();
        cardCstBatchPram.setCardCode(cardCst.getCardCode());
        cardCstBatchPram.setExpDate(cardCstReq.getExpDate());
        cardCstBatchPram.setCardId(cardId);
        cardCstBatchPram.setCstCode(cardCst.getCstCode());
        cardCstBatchPram.setCardType(card.getType());
        cardCstBatchPram.setProNum(cardCst.getProNum());
        // 根据卡号、类型、有效期、卡ID、项目、客户号查询卡批次
        List<CardCstBatch> cardCstBatchList = cardCstBatchDaoMapper.getList(cardCstBatchPram);
        if(!cardCstBatchList.isEmpty()){
            if(cardCst != null && cardCst.getIsExp() == 1){
                CardCstBatch cardCstBatch = new CardCstBatch();
                cardCstBatch.setProNum(cardCst.getProNum());
                cardCstBatch.setCardType(cardCst.getCardType());
                cardCstBatch.setCardId(cardCst.getCardId());
                cardCstBatch.setCardCode(cardCst.getCardCode());
                cardCstBatch.setCstCode(cardCst.getCstCode());
                cardCstBatch.setExpDate(cardCstReq.getExpDate());
                cardCstBatch.setRechargeNum(rechargeNum);
                cardCstBatch.setUpdateTime(new Date());
                cardCstBatch.setUpdateBy(userId);
                cardCstBatchDaoMapper.recharge(cardCstBatch);
                logger.info("充值成功:"+ JSONObject.toJSONString(cardCst));
                // 新增卡账单
                saveCardCstBill(cardCstBatchList.get(0).getId(),card.getProNum(),card.getType(),cardCst.getCardCode(),cardCst.getCstCode(),cardId,userId,rechargeNum);
            }
        }else {
            // 充值年月不在卡批次中,新增卡批次、账单
            // 新增卡批次
            CardCstBatch batch = new CardCstBatch();
            batch.setId(TimestampGenerator.generateSerialNumber());
            batch.setProNum(card.getProNum());
            batch.setCardType(card.getType());
            batch.setCardId(cardId);
            batch.setCardCode(cardCst.getCardCode());
            batch.setCstCode(cardCst.getCstCode());
            batch.setTotalNum(rechargeNum);
            batch.setApplyNum(0);
            batch.setExpDate(cardCstReq.getExpDate());
            batch.setCreateTime(new Date());
            batch.setCreateBy("");
            batch.setUpdateTime(new Date());
            batch.setUpdateBy("");
            batch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            List<CardCstBatch> cstBatchList = new ArrayList<>();
            cstBatchList.add(batch);
            cardCstBatchDaoMapper.insertList(cstBatchList);
            logger.info("新增卡批次成功:"+ JSONObject.toJSONString(cstBatchList));
            // 新增卡账单
            saveCardCstBill(batch.getId(),card.getProNum(),card.getType(),cardCst.getCardCode(),cardCst.getCstCode(),cardId,userId,rechargeNum);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    public void saveCardCstBill(String cardCstBatchId, String proNum, Integer cardType, String cardCode, String cstCode, Integer cardId, String userId, Integer rechargeNum){
        CardCstBill cardCstBill = new CardCstBill();
        cardCstBill.setId(TimestampGenerator.generateSerialNumber());
        cardCstBill.setCardCstBatchId(cardCstBatchId);
        cardCstBill.setProNum(proNum);
        cardCstBill.setCardType(cardType);
        cardCstBill.setCardId(cardId);
        cardCstBill.setCardCode(cardCode);
        cardCstBill.setCstCode(cstCode);
        cardCstBill.setBillNum(rechargeNum);
        cardCstBill.setBillType(1);
        cardCstBill.setCreateTime(new Date());
        cardCstBill.setCreateBy(userId);
        cardCstBill.setUpdateTime(new Date());
        cardCstBill.setUpdateBy(userId);
        cardCstBill.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        List<CardCstBill> cardCstBillList = new ArrayList<>();
        cardCstBillList.add(cardCstBill);
        cardCstBillDaoMapper.insertList(cardCstBillList);
        logger.info("新增卡账单成功:" + JSONObject.toJSONString(cardCstBillList));
    }

    public CardCstBill addCardCstBill(String cardCstBatchId, String proNum, Integer cardType, String cardCode, String cstCode, Integer cardId, String userId, Integer rechargeNum){
        CardCstBill cardCstBill = new CardCstBill();
        cardCstBill.setId(TimestampGenerator.generateSerialNumber());
        cardCstBill.setCardCstBatchId(cardCstBatchId);
        cardCstBill.setProNum(proNum);
        cardCstBill.setCardType(cardType);
        cardCstBill.setCardId(cardId);
        cardCstBill.setCardCode(cardCode);
        cardCstBill.setCstCode(cstCode);
        cardCstBill.setBillNum(rechargeNum);
        cardCstBill.setBillType(1);
        cardCstBill.setCreateTime(new Date());
        cardCstBill.setCreateBy(userId);
        cardCstBill.setUpdateTime(new Date());
        cardCstBill.setUpdateBy(userId);
        cardCstBill.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        return cardCstBill;
    }

    public CardCstBatch addCardCstBatch(String proNum, Integer cardType, String cardCode, String cstCode, Integer cardId, String userId, Integer totalNum, String expDate){
        CardCstBatch batch = new CardCstBatch();
        batch.setId(TimestampGenerator.generateSerialNumber());
        batch.setProNum(proNum);
        batch.setCardType(cardType);
        batch.setCardId(cardId);
        batch.setCardCode(cardCode);
        batch.setCstCode(cstCode);
        batch.setTotalNum(totalNum);
        batch.setApplyNum(0);
        batch.setExpDate(expDate);
        batch.setCreateTime(new Date());
        batch.setCreateBy(userId);
        batch.setUpdateTime(new Date());
        batch.setUpdateBy(userId);
        batch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        return batch;
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
