package com.ej.hgj.service.wechat;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.common.BusinessException;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.house.CstIntoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.house.CstInto;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.wechat.*;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.enums.TempMonsterBasicRespCode;
import com.ej.hgj.enums.wechat.EventTypeEnum;
import com.ej.hgj.enums.wechat.PubMsgTypeEnum;
import com.ej.hgj.request.RespTextMessage;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.wechat.PubCommonUtil;
import com.ej.hgj.utils.JsonUtils;
import com.ej.hgj.utils.wechat.MessageUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class WechatServiceImpl implements WechatService{

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private CstIntoMapper cstIntoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Override
    public String handleMessage(HttpServletRequest request) throws IOException, DocumentException, JAXBException {
        String respMessage = "";
        //String requestXml = MessageUtil.parse2Xml(request);
        String requestXml = parse2Xml(request);
        logger.info("------------------微信服务器接收到的消息:------------------"+requestXml);
        Document document = DocumentHelper.parseText(requestXml);
        String msgType = document.getRootElement().element("MsgType").getText();
        if(StringUtils.equals(PubMsgTypeEnum.EVENT.getCode(), msgType)) {
            // 事件类型
            respMessage = handleMessage(requestXml);
        } else if (StringUtils.equals(PubMsgTypeEnum.TEXT.getCode(), msgType)){
            // 文本类型
            respMessage = handleMessage(requestXml);
        } else {
            logger.info("暂不处理改类型消息, msgType="+msgType);
        }
        return respMessage;
    }

    // 处理事件
    public String handleMessage(String requestXml) throws BusinessException, JAXBException {
        logger.info("----------------处理事件 requestXml:------------"+requestXml);
        // xml转对象
        JAXBContext context = JAXBContext.newInstance(XmlToObject.class);
        Unmarshaller unmarshal = context.createUnmarshaller();
        StringReader sr = new StringReader(requestXml);
        Object xmlObject = unmarshal.unmarshal(sr);
        XmlToObject xmlToObject = (XmlToObject) xmlObject;
        String event = xmlToObject.getEvent();
        // 二维码扫描带过来的客户编号
        String eventKey = xmlToObject.getEventKey().replaceAll("qrscene_", "");
        // 获取客户编号
        String cstCode = eventKey;
        String resp = "";
        if(StringUtils.equals(event, EventTypeEnum.SUBSCRIBE.getType())) {//处理关注事件
            resp = handleSubscribeEvent(xmlToObject, cstCode);
        }else if(StringUtils.equals(event, EventTypeEnum.SCAN.getType())) {//扫码事件
            resp = handleScanEvent(xmlToObject, false);
        }else if(StringUtils.equals(event, EventTypeEnum.UNSUBSCRIBE.getType())) {//取消关注事件
            return handleUnsubscribe(xmlToObject);
        }else if(StringUtils.equals(event, EventTypeEnum.CLICK.getType())) {//模板消息发送后结果通知事件
            logger.info(EventTypeEnum.CLICK.getTypeDesc());
            return handleClickEvent(xmlToObject);//处理菜单点击事件
        }else if(StringUtils.equals(event, EventTypeEnum.TEMPLATESENDJOBFINISH.getType())) {//模板消息发送后结果通知事件
            logger.info(EventTypeEnum.TEMPLATESENDJOBFINISH.getTypeDesc());
        }
        return resp;
    }

    /**
     * 处理公众号绑定事件
     *
     * @param xmlToObject
     * @return
     */
    private String handleSubscribeEvent(XmlToObject xmlToObject, String cstCode) {
        String eventKey = xmlToObject.getEventKey();
        String wxOpenId = xmlToObject.getFromUserName();
        if(!StringUtils.isBlank(eventKey)) {
            logger.info("-------------------处理关注带参数事件--------------------");
            return handleScanEvent(xmlToObject, true);
        }
        sendSubTemp(cstCode, wxOpenId);
        return respSubs(xmlToObject);
    }

    /**
     * 处理扫码生成二维码事件
     *
     * @param xmlToObject
     * @return
     */
    private String handleScanEvent(XmlToObject xmlToObject, boolean isSubscribe) {
        logger.info("处理扫码事件", EventTypeEnum.SCAN.getType());
        String wxOpenId = xmlToObject.getFromUserName();
        String pubOrgId = xmlToObject.getToUserName();
        String eventKey = xmlToObject.getEventKey().replaceAll("qrscene_", "");
        // 获取扫码对象编号 目前包括: 客户编号||房屋编号
        String objectId = eventKey;
        logger.info("扫码事件二维码传递参数:"+eventKey);
        sendScanTemp(objectId, pubOrgId, wxOpenId, eventKey);
        if(!isSubscribe) {
            return "";
        }
        return respSubs(xmlToObject);
    }

    private void sendSubTemp(final String cstCode, final String wxOpenId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info( "开启线程执行异步推送模板消息");
                try {
                    Thread.sleep(2000);
                    sendClaimMsg(cstCode, wxOpenId);
                } catch (InterruptedException e) {
                    logger.info( e.getMessage(), "推送关注模板消息失败");
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    private String handleUnsubscribe(XmlToObject xmlToObject) {
        logger.info(EventTypeEnum.UNSUBSCRIBE.getTypeDesc());
        return "";
    }

    private String handleClickEvent(XmlToObject xmlToObject) {
        logger.info("处理菜单点击事件", EventTypeEnum.CLICK.getTypeDesc());
        String pubOrgId = xmlToObject.getToUserName();
        String eventKey = xmlToObject.getEventKey();
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.WE_CHAT_PUB_ORG_ID);
        if(StringUtils.equals(pubOrgId, constantConfig.getConfigValue())) {
            logger.info("处理上海朴由公众号点击事件");
            if(StringUtils.equals(eventKey, "C1")) {
                RespTextMessage respTextMessage = new RespTextMessage();
                respTextMessage.setToUserName(xmlToObject.getFromUserName());
                respTextMessage.setFromUserName(xmlToObject.getToUserName());
                respTextMessage.setMsgType(Constant.RESP_MESSAGE_TYPE_TEXT);
                respTextMessage.setCreateTime(new Date().getTime());
                respTextMessage.setContent(Constant.PY_CLICK_TEXT);
                return MessageUtil.messageToXml(respTextMessage);
            }

        }
        logger.info("其余点击事件暂不处理");
        return "";
    }

    private String respSubs(XmlToObject xmlToObject) {
        RespTextMessage respTextMessage = new RespTextMessage();
        respTextMessage.setToUserName(xmlToObject.getFromUserName());
        respTextMessage.setFromUserName(xmlToObject.getToUserName());
        respTextMessage.setMsgType(Constant.RESP_MESSAGE_TYPE_TEXT);
        respTextMessage.setCreateTime(new Date().getTime());
        respTextMessage.setContent("");
        return MessageUtil.messageToXml(respTextMessage);
    }

    private void sendScanTemp(final String objectId, final String pubOrgId, final String wxOpenId, final String eventKey) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("开启线程执行异步推送扫码模板消息");
                try {
                    Thread.sleep(1000);
                    String scanService = eventKey.substring(0, 3);
                    //String transParam =  eventKey.substring(3);
                    if(StringUtils.equals(scanService, ScanQrEnum.S01.getCode())) {
                        sendClaimConfirmMsg(objectId, wxOpenId);
                    }else if(StringUtils.equals(scanService, ScanQrEnum.S02.getCode())) {
                        logger.info( "扫标的二维码");
                        sendPropObjMsg(objectId, wxOpenId);
                    }else if(StringUtils.equals(scanService, ScanQrEnum.S03.getCode())){
                        sendClaimConfirmMsg(objectId, wxOpenId);
                    }else {
                        sendClaimConfirmMsg(objectId, wxOpenId);
                    }
                } catch (InterruptedException e) {
                    logger.info(e.getMessage(), "推送关注模板消息失败");
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    public void sendClaimMsg(String cstCode, String wxOpenId) {
        logger.info("发送关注业主认领通知", cstCode, wxOpenId);
        String cstName = "";
        String isCHeck = "N";
        // 通过客户编号查询客户信息
        HgjCst hgjCst = new HgjCst();
        hgjCst.setCode(cstCode);
        List<HgjCst> list = hgjCstDaoMapper.getList(hgjCst);
        if(!list.isEmpty()){
            HgjCst hgjCst1 = list.get(0);
            cstName = hgjCst1.getCstName();
        }
        CstInto cstInto = new CstInto();
        cstInto.setCstCode(cstCode);
        List<CstInto> list1 = cstIntoMapper.getList(cstInto);
        if(!list1.isEmpty()){
            isCHeck = "Y";
        }
        try {

            TempleModel templeModel = new TempleModel();
            //templeModel.setFirst(new ModelData("尊敬的XXX业主，欢迎关注XXX公众号"));
            templeModel.setKeyword1(new ModelData("房屋绑定"));
            templeModel.setKeyword2(new ModelData(DateUtils.transfer2LongDateGbkDateTime(DateUtils.getCurrentDateTime())));
            templeModel.setKeyword3(new ModelData("请点击完成绑定"));
            //templeModel.setKeyword4(new ModelData("至2020年10月10号"));
            //templeModel.setRemark(new ModelData("点击详情完成房屋认领"));

            String sign = sign(cstCode, wxOpenId);
            String miniprogramUrl = Constant.BIND_PAGE_OWNER + "?cstCode=" + cstCode + "&cstName=" + cstName + "&isCHeck=" + isCHeck + "&sign=" + sign;
            logger.info("miniprogramUrl=", miniprogramUrl);
            ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP);
            Miniprogram miniprogram = new Miniprogram(constantConfig.getAppId(), miniprogramUrl);
            ModelMessage modelMessage = new ModelMessage(wxOpenId, Constant.TEMP_LATE_ID, templeModel, miniprogram);
            int resp = sendModel(modelMessage, "", Constant.TEMP_LATE_ID);
            if (resp != 0) {
                logger.info("发送业主认领通知模板消息失败");
                throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
            }
        } catch (Exception e) {
            logger.info("发送业主认领通知模板消息失败");
            throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
        }
    }

    private String sign(String cstCode, String wxopenid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cstCode", cstCode);
        map.put("wxopenid", wxopenid);
        String temp = PubCommonUtil.createLinkString(map) + Constant.MD5SALT;
        return DigestUtils.md5Hex(temp);
    }

    public static int sendModel(ModelMessage modelMsg, String accessToken, String templateId) throws Exception {
        int result = -1;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", accessToken);
        String jsonMenu = JsonUtils.writeEntiry2JSON(modelMsg);
        if (modelMsg.getTemplate_id().equals(templateId)) {
            jsonMenu = jsonMenu.replace("date", "Date").replace("money", "Money");
        }
        JSONObject jsonObject = HttpClientUtil.sendPost(url,jsonMenu);
        if (null != jsonObject) {
            int errorCode = ((Integer) jsonObject.get("errcode"));
            if (0 != errorCode) {
                result = errorCode;
            } else {
                result = 0;
            }
        }

        return result;
    }

    public static int newSendModel(String jsonMenu, String accessToken) {
        int result = -1;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", accessToken);
        //String jsonMenu = JsonUtils.writeEntiry2JSON(modelMsg);
        //if (modelMsg.getTemplate_id().equals(templateId)) {
        jsonMenu = jsonMenu.replace("date", "Date").replace("money", "Money");
        //}
        JSONObject jsonObject = HttpClientUtil.sendPost(url,jsonMenu);
        if (null != jsonObject) {
            int errorCode = ((Integer) jsonObject.get("errcode"));
            if (0 != errorCode) {
                result = errorCode;
            } else {
                result = 0;
            }
    }

    return result;
}

    /**
     * 当前在用的，公众号发送房屋绑定消息
     * @param objectId
     * @param wxOpenId
     * @throws BusinessException
     */
    public void sendClaimConfirmMsg(String objectId, String wxOpenId) throws BusinessException {
        String cstCode = "";
        String cstName = "";
        String houseId = "";
        String houseName = "";
        //String isCHeck = "N";
        String proNum = "";
        String miniprogramUrl = "";
        // objectId去掉前三位
        objectId = objectId.substring(3);
        // 判断objectId是客户编号还是房屋编号
        HgjHouse hh = hgjHouseDaoMapper.findById(objectId);
        if(hh != null){
            houseId = objectId;
            HgjCst hgjCst = hgjCstDaoMapper.getCstNameByResId(houseId);
            houseName = hh.getResName();
            cstCode = hgjCst.getCode();
            cstName = hgjCst.getCstName();
            proNum = hgjCst.getOrgId();
        }else {
            HgjCst hc = hgjCstDaoMapper.findByCstCode(objectId);
            if(hc != null){
                cstCode = objectId;
                // 通过客户编号查询客户信息
                HgjCst hgjCst = new HgjCst();
                hgjCst.setCode(cstCode);
                cstName = hc.getCstName();
                proNum = hc.getOrgId();
            }
        }

//        CstInto cstInto = new CstInto();
//        cstInto.setCstCode(cstCode);
//        List<CstInto> list1 = cstIntoMapper.getList(cstInto);
//        if(!list1.isEmpty()){
//            isCHeck = "Y";
//        }

        try {
            //custId = transParam.substring(3, 13);
            //transParam = transParam.substring(13);
            // 获取token
            //String accessToken = JSONUtil.parseObj(HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxf32478b0185ce964&secret=a69e45d063ba9be3176e4ff4dbd64dc5")).get("access_token").toString();
            ConstantConfig weChatPubApp = constantConfDaoMapper.getByProNumAndKey(proNum,Constant.WE_CHAT_PUB_APP);
            String accessToken = JSONUtil.parseObj(HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+weChatPubApp.getAppId()+"&secret=" + weChatPubApp.getAppSecret())).get("access_token").toString();

            //需要加签处理
            //String sign = sign(cstCode, wxOpenId);
            //String miniprogramUrl = Constant.BIND_PAGE + "?cstCode=" + cstCode + "&cstName=" + cstName + "&proNum=" + proNum + "&isCHeck=" + isCHeck + "&sign=" + sign;
            if(StringUtils.isNotBlank(houseId)){
                miniprogramUrl = Constant.BIND_PAGE_TENANT + "?cstCode=" + cstCode + "&cstName=" + cstName  + "&houseId=" + houseId + "&houseName=" + houseName + "&proNum=" + proNum;
            }else {
                miniprogramUrl = Constant.BIND_PAGE_OWNER + "?cstCode=" + cstCode + "&cstName=" + cstName + "&proNum=" + proNum;
            }
            ConstantConfig miniProgramApp = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP);
            Miniprogram miniprogram = new Miniprogram(miniProgramApp.getAppId(), miniprogramUrl);
            ConstantConfig constantConfig = constantConfDaoMapper.getByProNumAndKey(proNum,Constant.TEMP_LATE_ID);
            String tempLateId = constantConfig.getConfigValue();
            // 根据项目号区分发送到哪个公众号
            int resp = 0;
            // 10000-租户服务平台公众号
            if("10000".equals(proNum)){
                TempleModel templeModel = new TempleModel();
                //templeModel.setFirst(new ModelData("尊敬的XXX业主，欢迎关注XXX公众号"));
                templeModel.setKeyword1(new ModelData("房屋绑定"));
                templeModel.setKeyword2(new ModelData(DateUtils.transfer2LongDateGbkDateTime(DateUtils.getCurrentDateTime())));
                templeModel.setKeyword3(new ModelData("请点击完成绑定"));
                //templeModel.setKeyword4(new ModelData("至2020年10月10号"));
                //templeModel.setRemark(new ModelData("点击详情完成房屋认领"));
                ModelMessage modelMessage = new ModelMessage(wxOpenId, tempLateId, templeModel, miniprogram);
                String jsonMenu = JsonUtils.writeEntiry2JSON(modelMessage);
                resp = newSendModel(jsonMenu, accessToken);
            }else if("10001".equals(proNum)){
                TempleModelXh templeModel = new TempleModelXh();
                templeModel.setConst2(new ModelData("房屋绑定"));
                //templeModel.setTime4(new ModelData(DateUtils.transfer2LongDateGbkDateTime(DateUtils.getCurrentDateTime())));
                // 时间格式-2023年12月5日 09:38
                String date = DateUtils.wechatPubFormat(new Date());
                templeModel.setTime4(new ModelData(date));
                templeModel.setThing3(new ModelData("请点击完成绑定"));
                ModelMessageXh modelMessage = new ModelMessageXh(wxOpenId, tempLateId, templeModel, miniprogram);
                String jsonMenu = JsonUtils.writeEntiry2JSON(modelMessage);
                resp = newSendModel(jsonMenu, accessToken);

            }

            //String miniprogramUrl = Constant.BIND_PAGE + "?cstCode=" + cstCode + "&wxopenid=" + wxOpenId + "&sign=" + sign;
            //ModelMessage modelMessage = new ModelMessage(wxOpenId, tempLateId.getConfigValue(), templeModel, miniprogram);
            // 小程序发布前跳转链接无法识别消息发送失败  miniprogram先注释掉
            // ModelMessage modelMessage = new ModelMessage(wxOpenId, tempLateId.getConfigValue(), templeModel, new Miniprogram());
            //int resp = sendModel(modelMessage, accessToken, tempLateId.getConfigValue());
            if (resp != 0) {
                throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
            }
        } catch (Exception e) {
            throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
        }
    }
    public void sendPropObjMsg(String cstCode, String wxOpenId) throws BusinessException {
        //String transParam = eventKey.substring(13);
        try {
            TempleModel templeModel = new TempleModel();
            templeModel.setKeyword1(new ModelData("匿名问题反馈"));
            templeModel.setKeyword2(new ModelData(DateUtils.transfer2LongDateGbkDateTime(DateUtils.getCurrentDateTime())));
            templeModel.setKeyword3(new ModelData("请点击完成您想投诉、建议、表扬以及报修的事件"));
            String miniprogramUrl = Constant.ADVICE_PAGE + "?cstCode=" + cstCode + "&wxOpenId=" + wxOpenId;
            ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP);
            Miniprogram miniprogram = new Miniprogram(constantConfig.getAppId(), miniprogramUrl);
            ModelMessage modelMessage = new ModelMessage(wxOpenId, Constant.TEMP_LATE_ID, templeModel, miniprogram);
            //int resp = sendModel(modelMessage, "", Constant.TEMP_LATE_ID);
            int resp = -1;
            if (resp != 0) {
                throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
            }
        } catch (Exception e) {
            throw new BusinessException(TempMonsterBasicRespCode.WECHAT_TEMPLATE_MSG_FAIL);
        }
    }


    public String parse2Xml(HttpServletRequest request) throws IOException{
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try{
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                inputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws JAXBException {
        String xml = "<xml>\n" +
                "    <ToUserName>\n" +
                "        <![CDATA[gh_d3bdf6adb0cb]]>\n" +
                "    </ToUserName>\n" +
                "    <FromUserName>\n" +
                "        <![CDATA[oAUUuwazrP0-R3eBnpV_ZpDFYteA]]>\n" +
                "    </FromUserName>\n" +
                "    <CreateTime>1692326985</CreateTime>\n" +
                "    <MsgType>\n" +
                "        <![CDATA[event]]>\n" +
                "    </MsgType>\n" +
                "    <Event>\n" +
                "        <![CDATA[SCAN]]>\n" +
                "    </Event>\n" +
                "    <EventKey>\n" +
                "        <![CDATA[S01GYS1708210026]]>\n" +
                "    </EventKey>\n" +
                "    <Ticket>\n" +
                "        <![CDATA[gQFr7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAydW1ScWhqMTVjSzExLXV1dXhBY2sAAgRG3N5kAwRYAgAA]]>\n" +
                "    </Ticket>\n" +
                "</xml>";

        JAXBContext context = JAXBContext.newInstance(XmlToObject.class);
        Unmarshaller unmarshal = context.createUnmarshaller();
        StringReader sr = new StringReader(xml);
        Object xmlObject = unmarshal.unmarshal(sr);
        XmlToObject xmlToObject = (XmlToObject) xmlObject;
        System.out.println(xmlToObject.getEvent());
    }
}
