package com.ej.hgj.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.active.CouponQrCodeDaoMapper;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.coupon.CouponSubDetailDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.entity.active.CouponQrCode;
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
                // 游泳券开门后次数扣减
                CouponQrCode couponQrCodeParam = new CouponQrCode();
                couponQrCodeParam.setCardNo(cardNo);
                couponQrCodeParam.setIsExpire(1);
                // 根据卡号查询有效券二维码记录
                List<CouponQrCode> list = couponQrCodeDaoMapper.getList(couponQrCodeParam);
                if(!list.isEmpty()){
                    CouponQrCode couponQrCode = list.get(0);
                    CouponGrant couponGrant = couponGrantDaoMapper.getById(couponQrCode.getCouponId());
                    // 查询当天扣减记录
                    List<CouponSubDetail> couponSubDetailList = couponSubDetailDaoMapper.getByQrCodeIdList(couponQrCode.getId());
                    Integer expNum = couponGrant.getExpNum();
                    // 有效次数大于等于1，并且当天没扣减过
                    if(expNum >= 1 && couponSubDetailList.isEmpty()){
                        // 扣减券次数
                        couponGrant.setExpNum(expNum - 1);
                        couponGrant.setUpdateTime(new Date());
                        couponGrantDaoMapper.update(couponGrant);

                        // 插入扣减记录
                        CouponSubDetail couponSubDetail = new CouponSubDetail();
                        couponSubDetail.setId(TimestampGenerator.generateSerialNumber());
                        couponSubDetail.setQrCodeId(couponQrCode.getId());
                        couponSubDetail.setSubNum(1);
                        couponSubDetail.setCreateTime(new Date());
                        couponSubDetail.setUpdateTime(new Date());
                        couponSubDetail.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                        couponSubDetailDaoMapper.save(couponSubDetail);

//                        // 如果券可用次数为0，调用接口删除二维码，将二维码改为失效
//                        if(couponGrant.getExpNum() == 0){
//                            ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
//                            String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + couponQrCode.getNeighNo() + "&cardNo=" + couponQrCode.getCardNo() + "&unitNumber=" + couponQrCode.getUnitNum();
//                            JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
//                            logger.info("删除二维码返回jsonObject:" + jsonObject);
//                            String result = jsonObject.get("result").toString();
//                            String message = jsonObject.getString("message");
//                            // 成功
//                            if("1".equals(result)){
//                                // 更新二维码记录为失效
//                                couponQrCode.setIsExpire(0);
//                                couponQrCode.setUpdateTime(new Date());
//                                couponQrCodeDaoMapper.update(couponQrCode);
//                            }else {
//                                couponQrCode.setErrorMsg("调用接口删除二维码失败");
//                                couponQrCode.setUpdateTime(new Date());
//                                couponQrCodeDaoMapper.update(couponQrCode);
//                                ajaxResult.setResCode(0);
//                                ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
//                                return ajaxResult;
//                            }
//                        }
                    }

                    // 当天同一张卡进门超过N次，调用接口删除二维码，将二维码改为失效
                    List<OpenDoorLog> byCardNoAndIsUnlock = openDoorLogDaoMapper.getByCardNoAndIsUnlock(cardNo);
                    ConstantConfig byKey = constantConfDaoMapper.getByKey(Constant.COUPON_QR_CODE_OPEN_DOOR_SIZE);
                    Integer openDoorSize = Integer.valueOf(byKey.getConfigValue());
                    if(!byCardNoAndIsUnlock.isEmpty() && byCardNoAndIsUnlock.size() >= openDoorSize){
                        ConstantConfig constantConfigUrl = constantConfDaoMapper.getByKey(Constant.OPEN_DOOR_QR_CODE_URL);
                        String user_info_url = constantConfigUrl.getConfigValue()+"/Delete?" + "neighNo=" + neighNo + "&cardNo=" + cardNo + "&unitNumber=" + couponQrCode.getUnitNum();
                        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(user_info_url));
                        logger.info("删除二维码返回jsonObject:" + jsonObject);
                        String result = jsonObject.get("result").toString();
                        String message = jsonObject.getString("message");
                        // 成功
                        if("1".equals(result)){
                            // 更新二维码记录为失效
                            couponQrCode.setIsExpire(0);
                            couponQrCode.setUpdateTime(new Date());
                            couponQrCodeDaoMapper.update(couponQrCode);
                        }else {
                            couponQrCode.setErrorMsg("调用接口删除二维码失败");
                            couponQrCode.setUpdateTime(new Date());
                            couponQrCodeDaoMapper.update(couponQrCode);
                            ajaxResult.setResCode(0);
                            ajaxResult.setResMsg("调用接口删除二维码失败:" + message);
                            return ajaxResult;
                        }
                    }
                }
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

}

