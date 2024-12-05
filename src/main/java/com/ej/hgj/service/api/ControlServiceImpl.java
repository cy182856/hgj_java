package com.ej.hgj.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.active.CouponQrCodeDaoMapper;
import com.ej.hgj.dao.card.*;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.coupon.CouponSubDetailDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.entity.active.CouponQrCode;
import com.ej.hgj.entity.card.*;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.coupon.CouponSubDetail;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorLog;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.enums.QrSceneEnum;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.service.cst.CstService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.wechat.WechatPubNumUtils;
import com.ej.hgj.vo.QrCodeLogResVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ControlServiceImpl implements ControlService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private OpenDoorLogDaoMapper openDoorLogDaoMapper;

    @Autowired
    private CouponQrCodeDaoMapper couponQrCodeDaoMapper;

    @Autowired
    private CouponGrantDaoMapper couponGrantDaoMapper;

    @Autowired
    private CouponSubDetailDaoMapper couponSubDetailDaoMapper;

    @Autowired
    private CardQrCodeDaoMapper cardQrCodeDaoMapper;

    @Autowired
    private CardCstDaoMapper cardCstDaoMapper;

    @Autowired
    private CardSubDetailDaoMapper cardSubDetailDaoMapper;

    @Autowired
    private CardCstBatchDaoMapper cardCstBatchDaoMapper;

    @Autowired
    private CardCstBillDaoMapper cardCstBillDaoMapper;

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public AjaxResultApi saveOpenDoorLog(QrCodeLogResVo qrCodeLogResVo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        String neighNo = qrCodeLogResVo.getNeighNo();
        String cardNo = qrCodeLogResVo.getCardNo();
        String deviceNo = qrCodeLogResVo.getDeviceNo();
        Integer isUnlock = qrCodeLogResVo.getIsUnlock();
        Long eventTime = qrCodeLogResVo.getEventTime();
        if(StringUtils.isBlank(neighNo) || StringUtils.isBlank(cardNo) ||
                StringUtils.isBlank(deviceNo)|| isUnlock == null || eventTime == null ){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("请求参数错误");
            return ajaxResult;
        }
        try {
            OpenDoorLog openDoorLog = new OpenDoorLog();
            openDoorLog.setId(TimestampGenerator.generateSerialNumber());
            openDoorLog.setNeighNo(neighNo);
            openDoorLog.setCardNo(cardNo);
            openDoorLog.setDeviceNo(deviceNo);
            openDoorLog.setIsUnlock(isUnlock);
            openDoorLog.setEventTime(eventTime);
            openDoorLog.setCreateTime(new Date());
            openDoorLog.setUpdateTime(new Date());
            openDoorLog.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            openDoorLogDaoMapper.save(openDoorLog);

            // 2-进门
            if(isUnlock == 2){
                // 根据卡号、event_time分组查询开门记录
                OpenDoorLog od = new OpenDoorLog();
                od.setCardNo(cardNo);
                od.setEventTime(eventTime);
                List<OpenDoorLog> openDoorLogList = openDoorLogDaoMapper.getByCardNoAndEventTime(od);
                // 需要扣次数的设备号
                // ConstantConfig configDeviceNo = constantConfDaoMapper.getByKey(Constant.SWIM_DEVICE_NO);
                // if(configDeviceNo.getConfigValue().equals(deviceNo)){
                    // 游泳卡开门后次数扣减
                    CardQrCode cardQrCodeParam = new CardQrCode();
                    cardQrCodeParam.setCardNo(cardNo);
                    //cardQrCodeParam.setIsExp(1);
                    // 根据卡号查询游泳卡二维码记录
                    List<CardQrCode> list = cardQrCodeDaoMapper.getList(cardQrCodeParam);
                    if(!list.isEmpty() && openDoorLogList != null && openDoorLogList.size() == 1){
                        CardQrCode cardQrCode = list.get(0);
                        CardCstBatch cardCstBatch = cardCstBatchDaoMapper.getById(cardQrCode.getCardCstBatchId());
                        // 根据卡号项目号查询账单当天扣减记录
                        //CardCstBill cardCstBill = cardCstBillDaoMapper.getByCardCodeAndProNum(cardCstBatch.getCardCode(),cardCstBatch.getProNum());
                        // 总次数
                        //Integer totalNum = cardCstBatch.getTotalNum();
                        // 已用次数
                        Integer applyNum = cardCstBatch.getApplyNum();
                        // 剩余可用次数
                        //Integer expNum = totalNum - applyNum;
                        // 剩余可用次数大于等于1，并且当天没扣减过
                        //if(expNum >= 1 && cardCstBill == null){
                        //if(expNum >= 1){
                            // 已用次数 + 1
                            cardCstBatch.setApplyNum(applyNum + 1);
                            cardCstBatch.setUpdateTime(new Date());
                            cardCstBatchDaoMapper.update(cardCstBatch);

                            // 新增卡账单扣减记录
                            CardCstBill cardCstBillInsert = new CardCstBill();
                            cardCstBillInsert.setId(TimestampGenerator.generateSerialNumber());
                            cardCstBillInsert.setProNum(cardCstBatch.getProNum());
                            cardCstBillInsert.setCardType(cardCstBatch.getCardType());
                            cardCstBillInsert.setCardId(cardCstBatch.getCardId());
                            cardCstBillInsert.setCardCode(cardCstBatch.getCardCode());
                            cardCstBillInsert.setCstCode(cardCstBatch.getCstCode());
                            cardCstBillInsert.setBillNum(-1);
                            cardCstBillInsert.setBillType(2);
                            cardCstBillInsert.setWxOpenId(cardQrCode.getWxOpenId());
                            cardCstBillInsert.setCreateTime(new Date());
                            cardCstBillInsert.setCreateBy("");
                            cardCstBillInsert.setUpdateTime(new Date());
                            cardCstBillInsert.setUpdateBy("");
                            cardCstBillInsert.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                            cardCstBillDaoMapper.save(cardCstBillInsert);
                            logger.info("门禁回调，卡账单插入成功:" + JSONObject.toJSONString(cardCstBillInsert));
                        //}

                        // 当天同一张卡进门超过N次，调用接口删除二维码，将二维码改为失效
                        //List<OpenDoorLog> byCardNoAndIsUnlock = openDoorLogDaoMapper.getByCardNoAndIsUnlock(cardNo, configDeviceNo.getConfigValue());
                        //List<OpenDoorLog> byCardNoAndIsUnlock = openDoorLogDaoMapper.getByCardNoAndIsUnlock(cardNo);
                        //ConstantConfig configOpenDoorSize = constantConfDaoMapper.getByKey(Constant.CARD_QR_CODE_OPEN_DOOR_SIZE);
                        //Integer openDoorSize = Integer.valueOf(configOpenDoorSize.getConfigValue());
                        //if(!byCardNoAndIsUnlock.isEmpty() && byCardNoAndIsUnlock.size() >= openDoorSize){
                            //deleteQrCode(neighNo,cardNo,cardQrCode,ajaxResult);

                            // 进门后删除二维码
                            ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
                            String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + neighNo + "&cardNo=" + cardNo + "&unitNumber=" + cardQrCode.getUnitNum();
                            JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
                            logger.info("删除二维码返回jsonObject:" + jsonObject);
                            String result = jsonObject.get("result").toString();
                            String message = jsonObject.getString("message");
                            // 成功
                            if("1".equals(result)){
                                // 更新二维码记录为失效
                                cardQrCode.setIsExp(0);
                                cardQrCode.setUpdateTime(new Date());
                                cardQrCodeDaoMapper.update(cardQrCode);
                            }else {
                                cardQrCode.setErrorMsg("调用接口删除二维码失败");
                                cardQrCode.setUpdateTime(new Date());
                                cardQrCodeDaoMapper.update(cardQrCode);
                                ajaxResult.setResCode(0);
                                ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
                                return ajaxResult;
                            }
                        //}

                        // 如果卡剩余次数只有一次，那么需要将所有二维码设为失效
//                        if(expNum == 1){
//                            // 根据批次ID查询
//                            CardQrCode qrCode = new CardQrCode();
//                            qrCode.setCardCstBatchId(cardCstBatch.getId());
//                            qrCode.setIsExp(1);
//                            List<CardQrCode> cardQrCodes = cardQrCodeDaoMapper.getList(qrCode);
//                            if(!cardQrCodes.isEmpty()){
//                                for(CardQrCode cardQrCode1 : cardQrCodes){
//                                    deleteQrCode(cardQrCode1.getNeighNo(),cardQrCode1.getCardNo(),cardQrCode1,ajaxResult);
//                                }
//                            }
//                        }
                    }
               // }
            }
            ajaxResult.setResCode(1);
            ajaxResult.setResMsg("成功");
        }catch (Exception e){
            e.printStackTrace();
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }

    private AjaxResultApi deleteQrCode(String neighNo, String cardNo, CardQrCode cardQrCode, AjaxResultApi ajaxResult){
        ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
        String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + neighNo + "&cardNo=" + cardNo + "&unitNumber=" + cardQrCode.getUnitNum();
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
        logger.info("删除二维码返回jsonObject:" + jsonObject);
        String result = jsonObject.get("result").toString();
        String message = jsonObject.getString("message");
        // 成功
        if("1".equals(result)){
            // 更新二维码记录为失效
            cardQrCode.setIsExp(0);
            cardQrCode.setUpdateTime(new Date());
            cardQrCodeDaoMapper.update(cardQrCode);
        }else {
            cardQrCode.setErrorMsg("调用接口删除二维码失败");
            cardQrCode.setUpdateTime(new Date());
            cardQrCodeDaoMapper.update(cardQrCode);
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
            return ajaxResult;
        }
        return ajaxResult;
    }

//    @Override
//    //@Transactional(rollbackFor = Exception.class)
//    public AjaxResultApi saveOpenDoorLog(QrCodeLogResVo qrCodeLogResVo){
//        AjaxResultApi ajaxResult = new AjaxResultApi();
//        String neighNo = qrCodeLogResVo.getNeighNo();
//        String cardNo = qrCodeLogResVo.getCardNo();
//        String deviceNo = qrCodeLogResVo.getDeviceNo();
//        Integer isUnlock = qrCodeLogResVo.getIsUnlock();
//        Long eventTime = qrCodeLogResVo.getEventTime();
//        if(StringUtils.isBlank(neighNo) || StringUtils.isBlank(cardNo) ||
//                StringUtils.isBlank(deviceNo)|| isUnlock == null || eventTime == null ){
//            ajaxResult.setResCode(0);
//            ajaxResult.setResMsg("请求参数错误");
//            return ajaxResult;
//        }
//        try {
//            OpenDoorLog openDoorLog = new OpenDoorLog();
//            openDoorLog.setId(TimestampGenerator.generateSerialNumber());
//            openDoorLog.setNeighNo(neighNo);
//            openDoorLog.setCardNo(cardNo);
//            openDoorLog.setDeviceNo(deviceNo);
//            openDoorLog.setIsUnlock(isUnlock);
//            openDoorLog.setEventTime(eventTime);
//            openDoorLog.setCreateTime(new Date());
//            openDoorLog.setUpdateTime(new Date());
//            openDoorLog.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//            openDoorLogDaoMapper.save(openDoorLog);
//
//            // 2-进门
//            if(isUnlock == 2){
//                // 游泳券开门后次数扣减
//                CouponQrCode couponQrCodeParam = new CouponQrCode();
//                couponQrCodeParam.setCardNo(cardNo);
//                couponQrCodeParam.setIsExpire(1);
//                // 根据卡号查询有效券二维码记录
//                List<CouponQrCode> list = couponQrCodeDaoMapper.getList(couponQrCodeParam);
//                if(!list.isEmpty()){
//                    CouponQrCode couponQrCode = list.get(0);
//                    CouponGrant couponGrant = couponGrantDaoMapper.getById(couponQrCode.getCouponId());
//                    // 查询当天扣减记录
//                    List<CouponSubDetail> couponSubDetailList = couponSubDetailDaoMapper.getByQrCodeIdList(couponQrCode.getId());
//                    Integer expNum = couponGrant.getExpNum();
//                    // 有效次数大于等于1，并且当天没扣减过
//                    if(expNum >= 1 && couponSubDetailList.isEmpty()){
//                        // 扣减券次数
//                        couponGrant.setExpNum(expNum - 1);
//                        couponGrant.setUpdateTime(new Date());
//                        couponGrantDaoMapper.update(couponGrant);
//
//                        // 插入扣减记录
//                        CouponSubDetail couponSubDetail = new CouponSubDetail();
//                        couponSubDetail.setId(TimestampGenerator.generateSerialNumber());
//                        couponSubDetail.setQrCodeId(couponQrCode.getId());
//                        couponSubDetail.setSubNum(1);
//                        couponSubDetail.setCreateTime(new Date());
//                        couponSubDetail.setUpdateTime(new Date());
//                        couponSubDetail.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//                        couponSubDetailDaoMapper.save(couponSubDetail);
//
////                        // 如果券可用次数为0，调用接口删除二维码，将二维码改为失效
////                        if(couponGrant.getExpNum() == 0){
////                            ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
////                            String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + couponQrCode.getNeighNo() + "&cardNo=" + couponQrCode.getCardNo() + "&unitNumber=" + couponQrCode.getUnitNum();
////                            JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
////                            logger.info("删除二维码返回jsonObject:" + jsonObject);
////                            String result = jsonObject.get("result").toString();
////                            String message = jsonObject.getString("message");
////                            // 成功
////                            if("1".equals(result)){
////                                // 更新二维码记录为失效
////                                couponQrCode.setIsExpire(0);
////                                couponQrCode.setUpdateTime(new Date());
////                                couponQrCodeDaoMapper.update(couponQrCode);
////                            }else {
////                                couponQrCode.setErrorMsg("调用接口删除二维码失败");
////                                couponQrCode.setUpdateTime(new Date());
////                                couponQrCodeDaoMapper.update(couponQrCode);
////                                ajaxResult.setResCode(0);
////                                ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
////                                return ajaxResult;
////                            }
////                        }
//                    }
//
//                    // 当天同一张卡进门超过N次，调用接口删除二维码，将二维码改为失效
//                    List<OpenDoorLog> byCardNoAndIsUnlock = openDoorLogDaoMapper.getByCardNoAndIsUnlock(cardNo);
//                    ConstantConfig byKey = constantConfDaoMapper.getByKey(Constant.COUPON_QR_CODE_OPEN_DOOR_SIZE);
//                    Integer openDoorSize = Integer.valueOf(byKey.getConfigValue());
//                    if(!byCardNoAndIsUnlock.isEmpty() && byCardNoAndIsUnlock.size() >= openDoorSize){
//                        ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
//                        String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + neighNo + "&cardNo=" + cardNo + "&unitNumber=" + couponQrCode.getUnitNum();
//                        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
//                        logger.info("删除二维码返回jsonObject:" + jsonObject);
//                        String result = jsonObject.get("result").toString();
//                        String message = jsonObject.getString("message");
//                        // 成功
//                        if("1".equals(result)){
//                            // 更新二维码记录为失效
//                            couponQrCode.setIsExpire(0);
//                            couponQrCode.setUpdateTime(new Date());
//                            couponQrCodeDaoMapper.update(couponQrCode);
//                        }else {
//                            couponQrCode.setErrorMsg("调用接口删除二维码失败");
//                            couponQrCode.setUpdateTime(new Date());
//                            couponQrCodeDaoMapper.update(couponQrCode);
//                            ajaxResult.setResCode(0);
//                            ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
//                            return ajaxResult;
//                        }
//                    }
//                }
//            }
//            ajaxResult.setResCode(1);
//            ajaxResult.setResMsg("成功");
//        }catch (Exception e){
//            e.printStackTrace();
//            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            ajaxResult.setResCode(0);
//            ajaxResult.setResMsg(e.toString());
//        }
//        return ajaxResult;
//    }

}

