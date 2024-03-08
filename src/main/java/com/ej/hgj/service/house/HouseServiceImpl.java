package com.ej.hgj.service.house;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.enums.QrSceneEnum;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.service.user.UserRoleService;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.wechat.WechatPubNumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class HouseServiceImpl implements HouseService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private MenuMiniDaoMapper menuMiniDaoMapper;

    @Autowired
    private CstPayPerDaoMapper cstPayPerDaoMapper;


//    public List<HgjHouse> getList(HgjHouse hgjHouse){
//        return hgjHouseDaoMapper.getList(hgjHouse);
//    }

    @Override
    public GetTempQrcodeResult qrcode(GetTempQrcodeRequest getTempQrcodeRequest) {
        logger.info("------------生成入住二维码实现----------");
        GetTempQrcodeResult getTempQrcodeResult = null;
        String param = null;
        try {
            param = getQrcodeParam(getTempQrcodeRequest);
            ConstantConfig constantConfig = constantConfDaoMapper.getByProNumAndKey(getTempQrcodeRequest.getProNum(),Constant.WE_CHAT_PUB_APP);
            // 获取token
            String token = getToken(constantConfig.getAppId(), constantConfig.getAppSecret());
            // 获取二维码失效时间
            ConstantConfig configQrDefaultSecond = constantConfDaoMapper.getByKey(Constant.QR_DEFAULT_SECOND);
            logger.info("租户入住二维码失效时间:" + configQrDefaultSecond.getConfigValue()+"秒");
            logger.info("生成入住二维码 AppId:"+constantConfig.getAppId()+"----AppSecret:" + constantConfig.getAppSecret());
            logger.info("---------token:----------"+token);
            String qrcode = WechatPubNumUtils.qrcode(QrSceneEnum.QR_STR_SCENE.getCode(),
                    configQrDefaultSecond.getConfigValue() , param,
                    StringUtils.isBlank(getTempQrcodeRequest.getQrRespType()) ? QrRespEnum.QR_IMG.getCode() : getTempQrcodeRequest.getQrRespType(),
                    token);
            logger.info("---------qrcode:----------"+qrcode);
            getTempQrcodeResult = new GetTempQrcodeResult();
            if (StringUtils.isBlank(getTempQrcodeRequest.getQrRespType()) || StringUtils.equals(QrRespEnum.QR_IMG.getCode(), getTempQrcodeRequest.getQrRespType())) {
                getTempQrcodeResult.setImgUrl(qrcode);
            } else {
                getTempQrcodeResult.setCodeUrl(qrcode);
            }
        } catch (ParseException e) {
            e.getMessage();
        } catch (Exception e) {
            e.getMessage();
        }
        return getTempQrcodeResult;
    }

    private String getQrcodeParam(GetTempQrcodeRequest getTempQrcodeRequest)  throws ParseException {
        //生成二维码时，如果验证码超过了有效期，重新生成房屋信息验证码，之前生成的二维码将失效，
        //如果没有超过有效期，则验证码保持原样，有效期重新设置为10分钟
        // areaId|buildingId|houseNo|huRole|huOpenMode|checkCode|usrSeqId|usrType|custId
        StringBuffer sb = new StringBuffer();
        sb.append(ScanQrEnum.S01.getCode());
        sb.append(getTempQrcodeRequest.getHouseId());
        String param = sb.toString();
        return param;
    }

    public String getToken(String appid, String secret) throws Exception {
        String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET".replaceAll("APPID", appid).replaceAll("APPSECRET",secret);
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(access_token_url));
        logger.info("获取token返回jsonObject" + jsonObject);
        String access_token = jsonObject.getString("access_token");
        return access_token;
    }

}
