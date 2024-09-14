package com.ej.hgj.api;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.config.ProNeighConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorCodeDaoMapper;
import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.api.QuickCodeInfo;
import com.ej.hgj.entity.config.ProNeighConfig;
import com.ej.hgj.entity.opendoor.OpenDoorCode;
import com.ej.hgj.utils.TimestampGenerator;
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
        // 根据小区号查询对应项目号
        ProNeighConfig proNeighConfig = proNeighConfDaoMapper.getByNeighNo(neighNo);
        HashMap map = new HashMap();
        // 根据项目号查询所有单元
        List<HgjHouseUnit> unitList = hgjHouseDaoMapper.queryUnit(proNeighConfig.getProjectNum());
        // 根据项目号查询所有楼层
        List<HgjHouseFloor> floorList = hgjHouseDaoMapper.queryFloor(proNeighConfig.getProjectNum());
        // 根据项目号查询所有房间
        List<HgjHouseRoomInfo> houseList = hgjHouseDaoMapper.queryRoomNumAll(proNeighConfig.getProjectNum());
        map.put("unitList",unitList);
        map.put("floorList",floorList);
        map.put("houseList",houseList);
        ajaxResult.setResCode(1);
        ajaxResult.setResMsg("成功");
        ajaxResult.setData(map);
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
        QuickCodeInfo quickCodeInfo = openDoorCodeDaoMapper.getByQuickCode(quickCode);
        map.put("quickCodeInfo",quickCodeInfo);
        ajaxResult.setResCode(1);
        ajaxResult.setResMsg("成功");
        ajaxResult.setData(map);
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
        ProNeighConfig byNeighNo = proNeighConfDaoMapper.getByNeighNo(neighNo);
        OpenDoorCode openDoorCode = new OpenDoorCode();
        openDoorCode.setId(TimestampGenerator.generateSerialNumber());
        openDoorCode.setProNum(byNeighNo.getProjectNum());
        openDoorCode.setProName(byNeighNo.getProjectName());
        openDoorCode.setType(type);
        openDoorCode.setExpDate(qrCodeResVo.getExpDate());
        openDoorCode.setStartTime(qrCodeResVo.getStartTime());
        openDoorCode.setEndTime(qrCodeResVo.getEndTime());
        openDoorCode.setCardNo(qrCodeResVo.getCardNo());
        openDoorCode.setQrCodeContent(qrCodeResVo.getQrCode());
        openDoorCode.setNeighNo(qrCodeResVo.getNeighNo());
        openDoorCode.setUnitNum(qrCodeResVo.getUnitNum()+"");
        openDoorCode.setFloors(qrCodeResVo.getFloor()+"");
        if(type == 4){
            openDoorCode.setRandNum(qrCodeResVo.getQuickCode() + "");
            String facePicPath = saveFacePic(qrCodeResVo.getCardNo(),qrCodeResVo.getFacePic());
            openDoorCode.setFacePicPath(facePicPath);
        }
        openDoorCode.setCstCode(qrCodeResVo.getCstCode());
        openDoorCode.setCstName(qrCodeResVo.getCstName());
        openDoorCode.setPhone(qrCodeResVo.getPhone()+"");
        openDoorCode.setResCode(qrCodeResVo.getRoom()+"");

        openDoorCode.setIsExpire(1);
        openDoorCode.setCreateTime(new Date());
        openDoorCode.setUpdateTime(new Date());
        openDoorCode.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        openDoorCodeDaoMapper.save(openDoorCode);
        ajaxResult.setResCode(1);
        ajaxResult.setResMsg("成功");
        return ajaxResult;
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
