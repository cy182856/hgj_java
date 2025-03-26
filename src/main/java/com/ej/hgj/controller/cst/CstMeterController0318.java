//
//
//package com.ej.hgj.controller.cst;
//
//import com.ej.hgj.constant.AjaxResult;
//import com.ej.hgj.constant.Constant;
//import com.ej.hgj.dao.config.ProConfDaoMapper;
//import com.ej.hgj.dao.cst.CstMeterDaoMapper;
//import com.ej.hgj.dao.house.HgjHouseDaoMapper;
//import com.ej.hgj.entity.config.ProConfig;
//import com.ej.hgj.entity.cst.CstMeter;
//import com.ej.hgj.entity.house.HgjHouse;
//import com.ej.hgj.entity.role.Role;
//import com.ej.hgj.entity.tag.OneTreeData;
//import com.ej.hgj.entity.tag.TagCst;
//import com.ej.hgj.entity.tag.ThreeChildren;
//import com.ej.hgj.entity.tag.TwoChildren;
//import com.ej.hgj.entity.user.User;
//import com.ej.hgj.entity.user.UserRole;
//import com.ej.hgj.service.role.RoleMenuService;
//import com.ej.hgj.service.role.RoleService;
//import com.ej.hgj.service.user.UserRoleService;
//import com.ej.hgj.utils.TimestampGenerator;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@CrossOrigin
//@RestController
//@RequestMapping("/cst/meter")
//public class CstMeterController0318 {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private UserRoleService userRoleService;
//
//    @Autowired
//    private RoleMenuService roleMenuService;
//
//    @Autowired
//    private CstMeterDaoMapper cstMeterDaoMapper;
//
//    @Autowired
//    private ProConfDaoMapper proConfDaoMapper;
//
//    @Autowired
//    private HgjHouseDaoMapper hgjHouseDaoMapper;
//
//    @RequestMapping(value = "/list",method = RequestMethod.GET)
//    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
//                           @RequestParam(value = "limit",defaultValue = "10") int limit,
//                           CstMeter cstMeter){
//        AjaxResult ajaxResult = new AjaxResult();
//        HashMap map = new HashMap();
//        PageHelper.offsetPage((page-1) * limit,limit);
//        List<CstMeter> list = cstMeterDaoMapper.getList(cstMeter);
//        //logger.info("list:"+ JSON.toJSONString(list));
//        PageInfo<CstMeter> pageInfo = new PageInfo<>(list);
//        //计算总页数
//        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
//        if(page > pageNumTotal){
//            PageInfo<User> entityPageInfo = new PageInfo<>();
//            entityPageInfo.setList(new ArrayList<>());
//            entityPageInfo.setTotal(pageInfo.getTotal());
//            entityPageInfo.setPageNum(page);
//            entityPageInfo.setPageSize(limit);
//            map.put("pageInfo",entityPageInfo);
//        }else {
//            map.put("pageInfo",pageInfo);
//        }
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        ajaxResult.setData(map);
//        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
//        return ajaxResult;
//    }
//
//    @RequestMapping(value = "/select",method = RequestMethod.GET)
//    public AjaxResult select(Role role){
//        AjaxResult ajaxResult = new AjaxResult();
//        HashMap map = new HashMap();
//        List<Role> list = roleService.getList(role);
//        //logger.info("list:"+ JSON.toJSONString(list));
//        map.put("list",list);
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        ajaxResult.setData(map);
//        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
//        return ajaxResult;
//    }
//
//    @RequestMapping(value = "/save",method = RequestMethod.POST)
//    public AjaxResult save(@RequestBody CstMeter cstMeter){
//        AjaxResult ajaxResult = new AjaxResult();
//        if(cstMeter.getId() != null){
//            cstMeterDaoMapper.update(cstMeter);
//        }else{
//            CstMeter cm = cstMeterDaoMapper.getByCstCode(cstMeter.getCstCode());
//            if(cm != null){
//                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
//                ajaxResult.setMessage("数据已存在,不能重复添加");
//                return ajaxResult;
//            }
//            cstMeter.setId(TimestampGenerator.generateSerialNumber());
//            cstMeter.setProNum("10000");
//            cstMeter.setUserId(cstMeter.getUserId());
//            cstMeter.setCstCode(cstMeter.getCstCode());
//            cstMeter.setAccountDate(cstMeter.getAccountDate());
//            cstMeter.setUpdateTime(new Date());
//            cstMeter.setCreateTime(new Date());
//            cstMeter.setDeleteFlag(0);
//            cstMeterDaoMapper.save(cstMeter);
//        }
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }
//
//    @RequestMapping(value = "/saveRoleMenu",method = RequestMethod.POST)
//    public AjaxResult saveRoleMenu(@RequestBody Role role){
//        AjaxResult ajaxResult = new AjaxResult();
//        roleMenuService.saveRoleMenu(role);
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }
//
//    @RequestMapping(value = "/delete",method = RequestMethod.GET)
//    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
//        AjaxResult ajaxResult = new AjaxResult();
//        cstMeterDaoMapper.delete(id);
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }
//
//    /**
//     * 客户树结构查询,渔人码头项目,用户编号
//     * @return
//     */
//    @RequestMapping(value = "/selectCstTree",method = RequestMethod.POST)
//    public AjaxResult selectCstTree(@RequestBody CstMeter cstMeter){
//        AjaxResult ajaxResult = new AjaxResult();
//        // 查询项目
//        //List<ProConfig> proList = proConfDaoMapper.getList(new ProConfig());
//        ProConfig pc = new ProConfig();
//        pc.setProjectNum("10000");
//        List<ProConfig> proList = proConfDaoMapper.getList(pc);
//        List<OneTreeData> oneTreeDataList = new ArrayList<>();
//        for(ProConfig proConfig : proList){
//            OneTreeData oneTreeData = new OneTreeData();
//            oneTreeData.setId(proConfig.getProjectNum());
//            oneTreeData.setLabel(proConfig.getProjectName());
//            // 获取二级-楼栋
//            List<HgjHouse> budList = hgjHouseDaoMapper.queryBuilding(proConfig.getProjectNum());
//            List<TwoChildren> twoChildrenList = new ArrayList<>();
//            for(HgjHouse bud : budList){
//                TwoChildren twoChildren = new TwoChildren();
//                twoChildren.setId(bud.getBudId());
//                twoChildren.setLabel(bud.getBudName());
//                // 获取三级-房号
//                //List<HgjHouse> houseList = hgjSyHouseDaoMapper.queryRoomNum(bud.getBudId());
//                List<HgjHouse> houseList = hgjHouseDaoMapper.queryRoomNum(bud.getBudId());
//                // 根据客户编号去重复
//                houseList = houseList.stream()
//                        .collect(Collectors.collectingAndThen(
//                                Collectors.toMap(
//                                        HgjHouse::getCstCode, // 根据cstCode去重
//                                        Function.identity(), // 使用Electricity对象本身作为值，因为我们只需要去重，但保留完整对象
//                                        (existing, replacement) -> existing // 选择现有的对象，因为我们只想保留一个对象
//                                ),
//                                map -> new ArrayList<>(map.values()) // 将Map的values转换为List
//                        ));
//
////                if(!houseList.isEmpty() && StringUtils.isNotBlank(cstMeter.getCstName())){
////                    houseList = houseList.stream().filter(hgjHouse -> hgjHouse.getCstName().contains(cstMeter.getCstName())).collect(Collectors.toList());
////                }
//                List<ThreeChildren> threeChildrenList = new ArrayList<>();
//                for(HgjHouse house : houseList){
//                    ThreeChildren threeChildren = new ThreeChildren();
//                    threeChildren.setId(house.getCstCode());
//                    //threeChildren.setLabel(house.getCstName() + "(" + house.getResName() + ")");
//                    threeChildren.setLabel(house.getCstName());
//                    threeChildrenList.add(threeChildren);
//                }
//                twoChildren.setChildren(threeChildrenList);
//                twoChildrenList.add(twoChildren);
//            }
//            oneTreeData.setChildren(twoChildrenList);
//            oneTreeDataList.add(oneTreeData);
//        }
//
//        HashMap map = new HashMap();
//        // web所有菜单
//        map.put("cstTreeData",oneTreeDataList);
//
//        // 获取已被选中的客户树
//       // CstMeter cstMeter = new CstMeter();
//       // cstMeter.setUserId(userId);
//       // List<CstMeter> cstMeterList = cstMeterDaoMapper.getList(cstMeter);
//
//        // list转数组满足前端需求
//       // List<String> cstCodes = cstMeterList.stream().map(meter -> meter.getCstCode()).collect(Collectors.toList());
//       // String[] tagExpandedKeys = cstCodes.toArray(new String[cstCodes.size()]);
//       // String[] tagCheckedKeys = cstCodes.toArray(new String[cstCodes.size()]);
//        // 展开菜单数组
//        //map.put("tagExpandedKeys",tagExpandedKeys);
//        // 选中的菜单数组
//        //map.put("tagCheckedKeys",tagCheckedKeys);
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        ajaxResult.setData(map);
//        return ajaxResult;
//    }
//
//}
//
//
//
