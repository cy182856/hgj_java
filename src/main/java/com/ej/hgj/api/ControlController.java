package com.ej.hgj.api;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.config.ProNeighConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.opendoor.*;
import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.config.ProNeighConfig;
import com.ej.hgj.entity.file.FileMessage;
import com.ej.hgj.entity.opendoor.*;
import com.ej.hgj.service.api.ControlService;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.file.FileSendClient;
import com.ej.hgj.vo.QrCodeLogReqVo;
import com.ej.hgj.vo.QrCodeReqVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 门禁对接接口
 */
@CrossOrigin
@RestController
@RequestMapping("/control")
public class ControlController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.path.remote}")
    private String uploadPathRemote;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private ProNeighConfDaoMapper proNeighConfDaoMapper;

    @Autowired
    private OpenDoorCodeDaoMapper openDoorCodeDaoMapper;

    @Autowired
    private ControlService controlService;

    @Autowired
    private OpenDoorQuickCodeDaoMapper openDoorQuickCodeDaoMapper;

    @Autowired
    private OpenDoorCodeServiceDaoMapper openDoorCodeServiceDaoMapper;

    @Autowired
    private ExternalPersonInfoDaoMapper externalPersonInfoDaoMapper;

    /**
     * 单元楼层客户信息查询
     * @param neighNo
     * @return
     */
    @RequestMapping(value = "/cstInfo/list",method = RequestMethod.GET)
    public AjaxResultApi cstInfoList(@RequestParam("neighNo") String neighNo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        if(StringUtils.isBlank(neighNo)){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("参数错误");
            return ajaxResult;
        }
        try {
            // 根据小区号查询对应项目号
            ProNeighConfig proNeighConfig = proNeighConfDaoMapper.getByNeighNo(neighNo);
            HashMap map = new HashMap();
            // 根据项目号查询所有单元
            List<HgjHouseUnit> unitList = hgjHouseDaoMapper.queryUnit(proNeighConfig.getProjectNum());
            // 根据项目号查询所有楼层
            List<HgjHouseFloor> floorList = hgjHouseDaoMapper.queryFloor(proNeighConfig.getProjectNum());
            // 根据项目号查询所有房间
            List<HgjHouseRoomInfo> houseList = hgjHouseDaoMapper.queryRoomNumAll(proNeighConfig.getProjectNum());
            map.put("unitList", unitList);
            map.put("floorList", floorList);
            map.put("houseList", houseList);
            ajaxResult.setResCode(1);
            ajaxResult.setResMsg("成功");
            ajaxResult.setData(map);
            logger.info("-----单元楼层客户信息查询成功----");
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }

    /**
     * 快速通行码验证
     * @param quickCode
     * @return
     */
    @RequestMapping(value = "/quickCode/check",method = RequestMethod.GET)
    public AjaxResultApi quickCodeCheck(@RequestParam("quickCode") String quickCode){
        logger.info("quickCodeCheck请求参数|quickCode:" + quickCode);
        AjaxResultApi ajaxResult = new AjaxResultApi();
        if(StringUtils.isBlank(quickCode)){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("请求参数错误");
            return ajaxResult;
        }
        HashMap map = new HashMap();
        try {
            QuickCodeInfo quickCodeInfo = openDoorQuickCodeDaoMapper.getByQuickCodeApi(quickCode, DateUtils.strYmd(new Date()));
            if(quickCodeInfo != null){
                map.put("quickCodeInfo",quickCodeInfo);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                ajaxResult.setData(map);
                logger.info("------快速通行码验证成功------");
            }else {
                ajaxResult.setResCode(0);
                ajaxResult.setResMsg("快速通行码无效");
                logger.info("------快速通行码无效----------");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
            logger.info("------快速通行码验证失败----------");
        }
        return ajaxResult;
    }

    /**
     * 生成二维码
     * @param qrCodeReqVo
     * @return
     */
    @RequestMapping(value = "/qrCode/create",method = RequestMethod.POST)
    public AjaxResultApi qrCodeCreate(@RequestBody QrCodeReqVo qrCodeReqVo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        String neighNo = qrCodeReqVo.getNeighNo();
        Integer type = qrCodeReqVo.getType();
        String cardNo = qrCodeReqVo.getCardNo();
        String qrCode = qrCodeReqVo.getQrCode();
        String expDate = qrCodeReqVo.getExpDate();
        Long startTime = qrCodeReqVo.getStartTime();
        Long endTime = qrCodeReqVo.getEndTime();
        Long phone = qrCodeReqVo.getPhone();
        String cstCode = qrCodeReqVo.getCstCode();
        String cstName = qrCodeReqVo.getCstName();
        Integer unitNum = qrCodeReqVo.getUnitNum();
        Integer floor = qrCodeReqVo.getFloor();
        String room = qrCodeReqVo.getRoom();
        String houseId = qrCodeReqVo.getHouseId();
        String serviceName = qrCodeReqVo.getServiceName();
        String facePic = qrCodeReqVo.getFacePic();
        String personPhone = qrCodeReqVo.getPersonPhone();
        logger.info("qrCodeCreate请求参数|neighNo：" + neighNo + "|type:" + type + "|cardNo:" + cardNo + "|qrCode:" + qrCode + "|expDate:" + expDate +
                "|startTime:" + startTime + "|endTime:" + endTime + "|phone:" + phone + "|cstCode:" + cstCode + "|cstName:" + cstName +
                "|unitNum:" + unitNum + "|floor:" + floor + "|room:" + room + "|houseId:" + houseId + "|serviceName:" + serviceName + "|personPhone:" + personPhone);
        if(type == null || StringUtils.isBlank(serviceName) || StringUtils.isBlank(cardNo) || StringUtils.isBlank(qrCode)||
                startTime == null || endTime == null || phone == null || StringUtils.isBlank(facePic)){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("请求参数错误");
            return ajaxResult;
        }
        // 远程文件夹地址
        String folderPathRemote = uploadPathRemote + "/facepic/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 远程文件地址
        String filePathRemote = folderPathRemote + "/" + cardNo + ".txt";
        try {
            // 客服直接创建的二维码
            if(type == 3){
                if(StringUtils.isBlank(neighNo) || StringUtils.isBlank(expDate) ||
                        StringUtils.isBlank(houseId) || StringUtils.isBlank(cstCode) ||
                        StringUtils.isBlank(cstName) || unitNum == null || floor == null||
                        StringUtils.isBlank(room)){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("请求参数错误");
                    return ajaxResult;
                }
                OpenDoorCodeService openDoorCodeServicePram = new OpenDoorCodeService();
                openDoorCodeServicePram.setCardNo(cardNo);
                List<OpenDoorCodeService> list = openDoorCodeServiceDaoMapper.getList(openDoorCodeServicePram);
                if(!list.isEmpty()){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("卡号重复");
                    return ajaxResult;
                }
                ProNeighConfig byNeighNo = proNeighConfDaoMapper.getByNeighNo(neighNo);
                OpenDoorCodeService openDoorCodeService = new OpenDoorCodeService();
                openDoorCodeService.setId(TimestampGenerator.generateSerialNumber());
                openDoorCodeService.setProNum(byNeighNo.getProjectNum());
                openDoorCodeService.setProName(byNeighNo.getProjectName());
                openDoorCodeService.setType(3);
                openDoorCodeService.setServiceName(serviceName);
                openDoorCodeService.setExpDate(expDate);
                openDoorCodeService.setStartTime(startTime);
                openDoorCodeService.setEndTime(endTime);
                openDoorCodeService.setCardNo(cardNo);
                openDoorCodeService.setQrCodeContent(qrCode);
                openDoorCodeService.setNeighNo(neighNo);
                openDoorCodeService.setUnitNum(unitNum.toString());
                openDoorCodeService.setFloors(floor.toString());
                openDoorCodeService.setCstCode(cstCode);
                openDoorCodeService.setCstName(cstName);
                openDoorCodeService.setPhone(phone.toString());
                openDoorCodeService.setResCode(room);
                // 截取房间号
                String[] resCodeSplit = room.split("-");
                String addressNumber = unitNum+resCodeSplit[2];
                openDoorCodeService.setAddressNum(addressNumber);
                openDoorCodeService.setHouseId(houseId);
                openDoorCodeService.setFacePicPath(filePathRemote);
                openDoorCodeService.setCreateTime(new Date());
                openDoorCodeService.setUpdateTime(new Date());
                openDoorCodeService.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                openDoorCodeServiceDaoMapper.save(openDoorCodeService);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服创建二维码成功-------------");
                // 发送文件
                sendRemoteFile(cardNo, facePic, folderPathRemote);
                logger.info("-------------------文件发送成功------------------");
            }

            // 客服通过快速码创建的二维码
            if(type == 4){
                Integer quickCode = qrCodeReqVo.getQuickCode();
                if(quickCode == null){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("快速通行码为空");
                    return ajaxResult;
                }
//                OpenDoorQuickCode openDoorQuickCodePram = new OpenDoorQuickCode();
//                openDoorQuickCodePram.setCardNo(cardNo);
//                List<OpenDoorQuickCode> list = openDoorQuickCodeDaoMapper.getList(openDoorQuickCodePram);
//                if(!list.isEmpty()){
//                    ajaxResult.setResCode(0);
//                    ajaxResult.setResMsg("卡号重复");
//                    return ajaxResult;
//                }
                // 根据快速码更新数据
                OpenDoorQuickCode byQuickCode = openDoorQuickCodeDaoMapper.getByQuickCode(quickCode.toString());
                byQuickCode.setQuickCode(byQuickCode.getQuickCode());
                byQuickCode.setStartTime(startTime);
                byQuickCode.setEndTime(endTime);
                byQuickCode.setCardNo(cardNo);
                byQuickCode.setQrCodeContent(qrCode);
                byQuickCode.setPhone(phone.toString());
                byQuickCode.setFacePicPath(filePathRemote);
                byQuickCode.setServiceName(serviceName);
                byQuickCode.setIsExpire(0);
                byQuickCode.setUpdateTime(new Date());
                openDoorQuickCodeDaoMapper.updateByQuickCode(byQuickCode);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服通过快速码创建二维码成功-------------");
                // 发送文件
                sendRemoteFile(cardNo, facePic, folderPathRemote);
                logger.info("-------------------文件发送成功------------------");
            }
            // 客服创建的通码
            if(type == 5){
                if(StringUtils.isBlank(neighNo) || StringUtils.isBlank(expDate) || StringUtils.isBlank(personPhone)){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("请求参数错误");
                    return ajaxResult;
                }
                OpenDoorCodeService openDoorCodeServicePram = new OpenDoorCodeService();
                openDoorCodeServicePram.setCardNo(cardNo);
                List<OpenDoorCodeService> list = openDoorCodeServiceDaoMapper.getList(openDoorCodeServicePram);
                if(!list.isEmpty()){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("卡号重复");
                    return ajaxResult;
                }
                ProNeighConfig byNeighNo = proNeighConfDaoMapper.getByNeighNo(neighNo);
                OpenDoorCodeService openDoorCodeService = new OpenDoorCodeService();
                openDoorCodeService.setId(TimestampGenerator.generateSerialNumber());
                openDoorCodeService.setProNum(byNeighNo.getProjectNum());
                openDoorCodeService.setProName(byNeighNo.getProjectName());
                openDoorCodeService.setType(5);
                openDoorCodeService.setServiceName(serviceName);
                openDoorCodeService.setExpDate(expDate);
                openDoorCodeService.setStartTime(startTime);
                openDoorCodeService.setEndTime(endTime);
                openDoorCodeService.setCardNo(cardNo);
                openDoorCodeService.setQrCodeContent(qrCode);
                openDoorCodeService.setNeighNo(neighNo);
                openDoorCodeService.setPhone(phone.toString());
                openDoorCodeService.setFacePicPath(filePathRemote);
                openDoorCodeService.setPersonPhone(personPhone);
                openDoorCodeService.setCreateTime(new Date());
                openDoorCodeService.setUpdateTime(new Date());
                openDoorCodeService.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                openDoorCodeServiceDaoMapper.save(openDoorCodeService);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服创建通码成功-------------");
                // 发送文件
                sendRemoteFile(cardNo, facePic, folderPathRemote);
                logger.info("-------------------文件发送成功------------------");
            }
            // 客服直接创建的二维码,不需要快速码验证，临时二维码
            if(type == 6){
                if(StringUtils.isBlank(neighNo) || StringUtils.isBlank(expDate) ||
                        StringUtils.isBlank(houseId) || StringUtils.isBlank(cstCode) ||
                        StringUtils.isBlank(cstName) || unitNum == null || floor == null||
                        StringUtils.isBlank(room)){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("请求参数错误");
                    return ajaxResult;
                }
                OpenDoorCodeService openDoorCodeServicePram = new OpenDoorCodeService();
                openDoorCodeServicePram.setCardNo(cardNo);
                ProNeighConfig byNeighNo = proNeighConfDaoMapper.getByNeighNo(neighNo);
                OpenDoorCodeService openDoorCodeService = new OpenDoorCodeService();
                openDoorCodeService.setId(TimestampGenerator.generateSerialNumber());
                openDoorCodeService.setProNum(byNeighNo.getProjectNum());
                openDoorCodeService.setProName(byNeighNo.getProjectName());
                openDoorCodeService.setType(6);
                openDoorCodeService.setServiceName(serviceName);
                openDoorCodeService.setExpDate(expDate);
                openDoorCodeService.setStartTime(startTime);
                openDoorCodeService.setEndTime(endTime);
                openDoorCodeService.setCardNo(cardNo);
                openDoorCodeService.setQrCodeContent(qrCode);
                openDoorCodeService.setNeighNo(neighNo);
                openDoorCodeService.setUnitNum(unitNum.toString());
                openDoorCodeService.setFloors(floor.toString());
                openDoorCodeService.setCstCode(cstCode);
                openDoorCodeService.setCstName(cstName);
                openDoorCodeService.setPhone(phone.toString());
                openDoorCodeService.setResCode(room);
                // 截取房间号
                String[] resCodeSplit = room.split("-");
                String addressNumber = unitNum+resCodeSplit[2];
                openDoorCodeService.setAddressNum(addressNumber);
                openDoorCodeService.setHouseId(houseId);
                openDoorCodeService.setFacePicPath(filePathRemote);
                openDoorCodeService.setCreateTime(new Date());
                openDoorCodeService.setUpdateTime(new Date());
                openDoorCodeService.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                openDoorCodeServiceDaoMapper.save(openDoorCodeService);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服创建临时二维码成功-------------");
                // 发送文件
                sendRemoteFile(cardNo, facePic, folderPathRemote);
                logger.info("-------------------文件发送成功------------------");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }

    /**
     * 开门记录
     * @param qrCodeLogResVo
     * @return
     */
    @RequestMapping(value = "/openDoor/log",method = RequestMethod.POST)
    public AjaxResultApi save(@RequestBody QrCodeLogReqVo qrCodeLogResVo){
        return controlService.saveOpenDoorLog(qrCodeLogResVo);
    }


    /**
     * 保存外部人员信息
     * @param externalPersonInfo
     * @return
     */
    @RequestMapping(value = "/save/personInfo",method = RequestMethod.POST)
    public AjaxResultApi savePersonInfo(@RequestBody ExternalPersonInfo externalPersonInfo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        String userName = externalPersonInfo.getUserName();
        String phone = externalPersonInfo.getPhone();
        String idCard = externalPersonInfo.getIdCard();
        String belComp = externalPersonInfo.getBelComp();
        String facePic = externalPersonInfo.getFacePic();
        logger.info("savePersonInfo请求参数|userName：" + userName + "|phone:" + phone + "|idCard:" + idCard + "|belComp:" + belComp);
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(phone) || StringUtils.isBlank(idCard)||
                StringUtils.isBlank(belComp) || StringUtils.isBlank(facePic)){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("请求参数错误");
            return ajaxResult;
        }
        ExternalPersonInfo personInfo = new ExternalPersonInfo();
        personInfo.setPhone(phone);
        List<ExternalPersonInfo> list = externalPersonInfoDaoMapper.getList(personInfo);
        if(list != null && list.size() > 0){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("手机号重复");
            return ajaxResult;
        }
        try {
            ExternalPersonInfo epi = new ExternalPersonInfo();
            String id = TimestampGenerator.generateSerialNumber();
            epi.setId(id);
            epi.setUserName(userName);
            epi.setPhone(phone);
            epi.setIdCard(idCard);
            epi.setBelComp(belComp);
            // 远程文件夹地址
            String folderPathRemote = uploadPathRemote + "/facepic/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
            // 远程文件地址
            String filePathRemote = folderPathRemote + "/" + id + ".txt";
            epi.setFacePicPath(filePathRemote);
            epi.setCreateTime(new Date());
            epi.setUpdateTime(new Date());
            epi.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            externalPersonInfoDaoMapper.save(epi);
            ajaxResult.setResCode(1);
            ajaxResult.setResMsg("成功");
            logger.info("-------------------外部人员信息保存成功-------------");
            // 发送文件
            sendRemoteFile(id, facePic, folderPathRemote);
            logger.info("-------------------文件发送成功------------------");
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }

    /**
     * 查询外部人员信息
     * @param externalPersonInfo
     * @return
     */
    @RequestMapping(value = "/query/personInfo",method = RequestMethod.GET)
    public AjaxResultApi queryPersonInfo(ExternalPersonInfo externalPersonInfo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        HashMap map = new HashMap();
        try {
            List<ExternalPersonInfo> list = externalPersonInfoDaoMapper.getList(externalPersonInfo);
            for(ExternalPersonInfo o : list){
                if(StringUtils.isNotBlank(o.getFacePicPath())){
                    // 获取图片路径
//                    String imgPath = o.getFacePicPath();
//                    String base64Img = "";
//                    try {
//                        // 创建BufferedReader对象，从本地文件中读取
//                        BufferedReader reader = new BufferedReader(new FileReader(imgPath));
//                        // 逐行读取文件内容
//                        String line = "";
//                        while ((line = reader.readLine()) != null) {
//                            base64Img += line;
//                        }
//                        // 关闭文件
//                        reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    // 获取图片路径
                    String imgPath = o.getFacePicPath();
                    // 拼接远程文件地址
                    String fileUrl = Constant.REMOTE_FILE_URL + "/" + imgPath;
                    String fileContent = FileSendClient.downloadFileContent(fileUrl);
                    if(StringUtils.isNotBlank(fileContent)) {
                        o.setFacePic(fileContent);
                    }
                }
            }
            map.put("list", list);
            ajaxResult.setResCode(1);
            ajaxResult.setResMsg("成功");
            ajaxResult.setData(map);
            logger.info("-----查询外部人员信息成功----");
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }

    public String saveFacePic(String fileName, String content) {
        String path = "";
        //目录不存在则直接创建
        File filePath = new File(uploadPath + "/facepic");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        //创建年月日文件夹
        File ymdFile = new File(uploadPath + "/facepic/" + new SimpleDateFormat("yyyyMMdd").format(new Date()));
        //目录不存在则直接创建
        if (!ymdFile.exists()) {
            ymdFile.mkdirs();
        }
        //在年月日文件夹下面创建txt文本存储图片base64码
        File txtFile = new File(ymdFile.getPath() + "/" + fileName + ".txt");
        if (!txtFile.exists()) {
            try {
                txtFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = txtFile.getPath();
        FileWriter writer = null;
        try {
            writer = new FileWriter(txtFile);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public void sendRemoteFile(String cardNo, String facePic, String folderPathRemote){
        // 发送文件
        try {
            // 本地文件地址
            String facePicPath = saveFacePic(cardNo, facePic);
            // 读取文件
            byte[] fileBytes = Files.readAllBytes(Paths.get(facePicPath));
            // 创建文件消息对象
            FileMessage fileMessage = new FileMessage(folderPathRemote, cardNo + ".txt", fileBytes);
            FileSendClient.sendFile(fileMessage);
        } catch (Exception e) {
            logger.info("Error in Send: " + e.getMessage());
        }
    }
}
