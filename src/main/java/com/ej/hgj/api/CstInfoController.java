package com.ej.hgj.api;

import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.dao.config.ProNeighConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.entity.api.HgjHouseFloor;
import com.ej.hgj.entity.api.HgjHouseRoomInfo;
import com.ej.hgj.entity.api.HgjHouseUnit;
import com.ej.hgj.entity.config.ProNeighConfig;
import com.ej.hgj.entity.house.HgjHouse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/cstInfo")
public class CstInfoController {

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private ProNeighConfDaoMapper proNeighConfDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
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
}
