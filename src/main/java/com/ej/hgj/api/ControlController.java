package com.ej.hgj.api;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.config.ProNeighConfDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.opendoor.*;
import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.config.ProNeighConfig;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.opendoor.*;
import com.ej.hgj.service.api.ControlService;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.vo.QrCodeLogResVo;
import com.ej.hgj.vo.QrCodeResVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
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
     * @param qrCodeResVo
     * @return
     */
    @RequestMapping(value = "/qrCode/create",method = RequestMethod.POST)
    public AjaxResultApi qrCodeCreate(@RequestBody QrCodeResVo qrCodeResVo){
        AjaxResultApi ajaxResult = new AjaxResultApi();
        String neighNo = qrCodeResVo.getNeighNo();
        Integer type = qrCodeResVo.getType();
        String cardNo = qrCodeResVo.getCardNo();
        String qrCode = qrCodeResVo.getQrCode();
        String expDate = qrCodeResVo.getExpDate();
        Long startTime = qrCodeResVo.getStartTime();
        Long endTime = qrCodeResVo.getEndTime();
        Long phone = qrCodeResVo.getPhone();
        String cstCode = qrCodeResVo.getCstCode();
        String cstName = qrCodeResVo.getCstName();
        Integer unitNum = qrCodeResVo.getUnitNum();
        Integer floor = qrCodeResVo.getFloor();
        String room = qrCodeResVo.getRoom();
        String houseId = qrCodeResVo.getHouseId();
        String serviceName = qrCodeResVo.getServiceName();
        String facePic = qrCodeResVo.getFacePic();
        logger.info("qrCodeCreate请求参数|neighNo：" + neighNo + "|type:" + type + "|cardNo:" + cardNo + "|qrCode:" + qrCode + "|expDate:" + expDate +
                "|startTime:" + startTime + "|endTime:" + endTime + "|phone:" + phone + "|cstCode:" + cstCode + "|cstName:" + cstName +
                "|unitNum:" + unitNum + "|floor:" + floor + "|room:" + room + "|houseId:" + houseId + "|serviceName:" + serviceName);
        if(type == null || StringUtils.isBlank(serviceName) || StringUtils.isBlank(cardNo) || StringUtils.isBlank(qrCode)||
                startTime == null || endTime == null || phone == null || StringUtils.isBlank(facePic)){
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg("请求参数错误");
            return ajaxResult;
        }

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
                openDoorCodeService.setType(2);
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
                String facePicPath = saveFacePic(cardNo, facePic);
                openDoorCodeService.setFacePicPath(facePicPath);
                openDoorCodeService.setCreateTime(new Date());
                openDoorCodeService.setUpdateTime(new Date());
                openDoorCodeService.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                openDoorCodeServiceDaoMapper.save(openDoorCodeService);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服直接创建二维码成功-------------");
            }

            // 客服通过快速码创建的二维码
            if(type == 4){
                Integer quickCode = qrCodeResVo.getQuickCode();
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
                String facePicPath = saveFacePic(cardNo, facePic);
                byQuickCode.setFacePicPath(facePicPath);
                byQuickCode.setServiceName(serviceName);
                byQuickCode.setIsExpire(0);
                byQuickCode.setUpdateTime(new Date());
                openDoorQuickCodeDaoMapper.updateByQuickCode(byQuickCode);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
                logger.info("-------------------客服通过快速码创建二维码成功-------------");
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
    public AjaxResultApi save(@RequestBody QrCodeLogResVo qrCodeLogResVo){
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
            String facePicPath = saveFacePic(id, facePic);
            epi.setFacePicPath(facePicPath);
            epi.setCreateTime(new Date());
            epi.setUpdateTime(new Date());
            epi.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            externalPersonInfoDaoMapper.save(epi);
            ajaxResult.setResCode(1);
            ajaxResult.setResMsg("成功");
            logger.info("-------------------外部人员信息保存成功-------------");
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
                    String imgPath = o.getFacePicPath();
                    String base64Img = "";
                    try {
                        // 创建BufferedReader对象，从本地文件中读取
                        BufferedReader reader = new BufferedReader(new FileReader(imgPath));
                        // 逐行读取文件内容
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            base64Img += line;
                        }
                        // 关闭文件
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    o.setFacePic(base64Img);
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
        File ymdFile = new File(uploadPath + "/facepic" + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()));
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
}
