package com.ej.hgj.api;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.config.ProNeighConfDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorQuickCodeDaoMapper;
import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.config.ProNeighConfig;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.entity.opendoor.OpenDoorLog;
import com.ej.hgj.entity.opendoor.OpenDoorQuickCode;
import com.ej.hgj.service.api.ControlService;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.vo.QrCodeLogResVo;
import com.ej.hgj.vo.QrCodeResVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            }else {
                ajaxResult.setResCode(0);
                ajaxResult.setResMsg("快速通行码无效");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setResCode(0);
            ajaxResult.setResMsg(e.toString());
        }
        return ajaxResult;
    }


    /**
     * 生成二维码
     * @param qrCodeResVo
     * @return
     */
    @RequestMapping(value = "/qrCode/create",method = RequestMethod.POST)
    public AjaxResultApi save(@RequestBody QrCodeResVo qrCodeResVo){
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
        if(type == null || StringUtils.isBlank(cardNo) || StringUtils.isBlank(qrCode)||
                startTime == null || endTime == null || phone == null ){
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
                OpenDoorCode openDoorCodePram = new OpenDoorCode();
                openDoorCodePram.setCardNo(cardNo);
                List<OpenDoorCode> list = openDoorCodeDaoMapper.getList(openDoorCodePram);
                if(!list.isEmpty()){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("卡号重复");
                    return ajaxResult;
                }
                ProNeighConfig byNeighNo = proNeighConfDaoMapper.getByNeighNo(neighNo);
                OpenDoorCode openDoorCode = new OpenDoorCode();
                openDoorCode.setId(TimestampGenerator.generateSerialNumber());
                openDoorCode.setProNum(byNeighNo.getProjectNum());
                openDoorCode.setProName(byNeighNo.getProjectName());
                openDoorCode.setType(2);
                openDoorCode.setExpDate(expDate);
                openDoorCode.setStartTime(startTime);
                openDoorCode.setEndTime(endTime);
                openDoorCode.setCardNo(cardNo);
                openDoorCode.setQrCodeContent(qrCode);
                openDoorCode.setNeighNo(neighNo);
                openDoorCode.setUnitNum(unitNum.toString());
                openDoorCode.setFloors(floor.toString());
                openDoorCode.setCstCode(cstCode);
                openDoorCode.setCstName(cstName);
                openDoorCode.setPhone(phone.toString());
                openDoorCode.setResCode(room);
                // 截取房间号
                String[] resCodeSplit = room.split("-");
                String addressNumber = unitNum+resCodeSplit[2];
                openDoorCode.setAddressNum(addressNumber);
                openDoorCode.setHouseId(houseId);
                openDoorCode.setCreateTime(new Date());
                openDoorCode.setUpdateTime(new Date());
                openDoorCode.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                openDoorCodeDaoMapper.save(openDoorCode);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
            }

            // 客服通过快速码创建的二维码
            if(type == 4){
                Integer quickCode = qrCodeResVo.getQuickCode();
                if(quickCode == null){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("快速通行码为空");
                    return ajaxResult;
                }
                String facePic = qrCodeResVo.getFacePic();
                if(StringUtils.isBlank(facePic)){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("人脸图像为空");
                    return ajaxResult;
                }
                OpenDoorQuickCode openDoorQuickCodePram = new OpenDoorQuickCode();
                openDoorQuickCodePram.setCardNo(cardNo);
                List<OpenDoorQuickCode> list = openDoorQuickCodeDaoMapper.getList(openDoorQuickCodePram);
                if(!list.isEmpty()){
                    ajaxResult.setResCode(0);
                    ajaxResult.setResMsg("卡号重复");
                    return ajaxResult;
                }
                // 根据快速码更新数据
                OpenDoorQuickCode byQuickCode = openDoorQuickCodeDaoMapper.getByQuickCode(quickCode.toString());
                byQuickCode.setQuickCode(byQuickCode.getQuickCode());
                byQuickCode.setStartTime(startTime);
                byQuickCode.setEndTime(endTime);
                byQuickCode.setCardNo(cardNo);
                byQuickCode.setQrCodeContent(qrCode);
                byQuickCode.setPhone(phone.toString());
                String facePicPath = saveFacePic(cardNo, qrCode);
                byQuickCode.setFacePicPath(facePicPath);
                byQuickCode.setIsExpire(0);
                byQuickCode.setUpdateTime(new Date());
                openDoorQuickCodeDaoMapper.updateByQuickCode(byQuickCode);
                ajaxResult.setResCode(1);
                ajaxResult.setResMsg("成功");
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

    public String saveFacePic(String cardNo, String content) {
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
        File txtFile = new File(ymdFile.getPath() + "/" + cardNo + ".txt");
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
