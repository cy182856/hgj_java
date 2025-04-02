package com.ej.hgj.service.cst;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.CstPayPer;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.enums.QrSceneEnum;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CstServiceImpl implements CstService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private MenuMiniDaoMapper menuMiniDaoMapper;

    @Autowired
    private CstPayPerDaoMapper cstPayPerDaoMapper;


    public List<HgjCst> getList(HgjCst hgjCst){
        return hgjCstDaoMapper.getList(hgjCst);
    }

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
            logger.info("客户入住二维码失效时间:" + configQrDefaultSecond.getConfigValue()+"秒");
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

    @Override
    public GetTempQrcodeResult cstIntoQrcode(GetTempQrcodeRequest getTempQrcodeRequest) {
        logger.info("------------生成入住二维码实现----------");
        GetTempQrcodeResult getTempQrcodeResult = null;
        String param = null;
        try {
            param = getCstIntoQrcodeParam(getTempQrcodeRequest);
            ConstantConfig constantConfig = constantConfDaoMapper.getByProNumAndKey(getTempQrcodeRequest.getProNum(),Constant.WE_CHAT_PUB_APP);
            // 获取token
            String token = getToken(constantConfig.getAppId(), constantConfig.getAppSecret());
            // 获取二维码失效时间
            ConstantConfig configQrDefaultSecond = constantConfDaoMapper.getByKey(Constant.QR_DEFAULT_SECOND);
            logger.info("客户入住二维码失效时间:" + configQrDefaultSecond.getConfigValue()+"秒");
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

    @Override
    public GetTempQrcodeResult cstIntoStaffQrcode(GetTempQrcodeRequest getTempQrcodeRequest) {
        logger.info("------------生成入住二维码实现----------");
        GetTempQrcodeResult getTempQrcodeResult = null;
        String param = null;
        try {
            param = getCstIntoQrcodeParam(getTempQrcodeRequest);
            ConstantConfig constantConfig = constantConfDaoMapper.getByProNumAndKey(getTempQrcodeRequest.getProNum(),Constant.WE_CHAT_PUB_APP);
            // 获取token
            String token = getToken(constantConfig.getAppId(), constantConfig.getAppSecret());
            // 获取二维码失效时间
            ConstantConfig configQrDefaultSecond = constantConfDaoMapper.getByKey(Constant.QR_DEFAULT_SECOND);
            logger.info("客户入住二维码失效时间:" + configQrDefaultSecond.getConfigValue()+"秒");
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
        sb.append(getTempQrcodeRequest.getCstCode());
        String param = sb.toString();
        return param;
    }

    private String getCstIntoQrcodeParam(GetTempQrcodeRequest getTempQrcodeRequest)  throws ParseException {
        //生成二维码时，如果验证码超过了有效期，重新生成房屋信息验证码，之前生成的二维码将失效，
        //如果没有超过有效期，则验证码保持原样，有效期重新设置为10分钟
        // areaId|buildingId|houseNo|huRole|huOpenMode|checkCode|usrSeqId|usrType|custId
        StringBuffer sb = new StringBuffer();
        sb.append(ScanQrEnum.S01.getCode());
        sb.append(getTempQrcodeRequest.getCstIntoId());
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

    @Override
    public void saveCstMenu(HgjCst hgjCst) {
        // 查询所有菜单
        List<MenuMini> menuList = menuMiniDaoMapper.getList(new MenuMini());
        // 删除菜单权限
        menuMiniDaoMapper.delete(hgjCst.getCode());
        logger.info("删除菜单权限成功cstCode:" + hgjCst.getCode());
        List<Integer> webMenuIds = hgjCst.getWebMenuIds();
        List<CstMenu> roleMenuList = new ArrayList<>();
        if(webMenuIds != null){
            for(int i = 0; i<webMenuIds.size(); i++){
                CstMenu webRoleMenu = new CstMenu();
                Integer webMenuId = webMenuIds.get(i);
                webRoleMenu.setId(GenerateUniqueIdUtil.getGuid());
                //webRoleMenu.setProjectNum(Constant.PROJECT_NUM);
                webRoleMenu.setCstCode(hgjCst.getCode());
                webRoleMenu.setMenuId(webMenuId);
                webRoleMenu.setCreateBy("");
                webRoleMenu.setCreateTime(new Date());
                webRoleMenu.setUpdateBy("");
                webRoleMenu.setUpdateTime(new Date());
                webRoleMenu.setDeleteFlag(0);
                // 如果不是父节点就保存
                List<MenuMini> menus = menuList.stream().filter(menu -> menu.getId().equals(webMenuId)).collect(Collectors.toList());
                MenuMini menu = menus.get(0);
                if(menu.getParentId() != 0){
                    roleMenuList.add(webRoleMenu);
                }
            }
        }
        // 新增菜单权限
        if(!roleMenuList.isEmpty()){
            menuMiniDaoMapper.insertList(roleMenuList);
            logger.info("新增权限成功:"+ JSONObject.toJSONString(roleMenuList));
        }
        //--------------------------------------------------------------------------------------------------------------
        // 删除账单权限
//        cstPayPerDaoMapper.delete(hgjCst.getCode());
//        logger.info("删除账单权限成功cstCode:" + hgjCst.getCode());
//        List<Integer> weComMenuIds = hgjCst.getWeComMenuIds();
//        List<CstPayPer> cstPayPerList = new ArrayList<>();
//        if(weComMenuIds != null){
//            for(int i = 0; i<weComMenuIds.size(); i++){
//                CstPayPer webRoleMenu = new CstPayPer();
//                Integer webMenuId = weComMenuIds.get(i);
//                webRoleMenu.setId(GenerateUniqueIdUtil.getGuid());
//                //webRoleMenu.setProjectNum(Constant.PROJECT_NUM);
//                webRoleMenu.setCstCode(hgjCst.getCode());
//                webRoleMenu.setFunctionId(webMenuId);
//                webRoleMenu.setCreateBy("");
//                webRoleMenu.setCreateTime(new Date());
//                webRoleMenu.setUpdateBy("");
//                webRoleMenu.setUpdateTime(new Date());
//                webRoleMenu.setDeleteFlag(0);
//                cstPayPerList.add(webRoleMenu);
//
//            }
//        }
//        // 新增账单权限
//        if(!cstPayPerList.isEmpty()){
//            cstPayPerDaoMapper.insertList(cstPayPerList);
//            logger.info("新增账单权限成功:"+ JSONObject.toJSONString(cstPayPerList));
//        }
    }
}
