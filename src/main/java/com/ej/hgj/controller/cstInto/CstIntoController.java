package com.ej.hgj.controller.cstInto;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.cstInto.CstIntoService;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.service.role.RoleService;
import com.ej.hgj.service.user.UserRoleService;
import com.ej.hgj.sy.dao.house.SyHouseDaoMapper;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/cstInto")
public class CstIntoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleService roleService;

    @Autowired
    private CstIntoService cstIntoService;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private CstIntoHouseDaoMapper cstIntoHouseDaoMapper;

    @Autowired
    private SyHouseDaoMapper syHouseDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           CstInto cstInto){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isNotBlank(cstInto.getEndTime())){
            cstInto.setEndTime(cstInto.getEndTime() + " 23:59:59");
        }
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<CstInto> list = cstIntoDaoMapper.getList(cstInto);
        for(CstInto cst : list){
            // 入住状态
            if(cst.getIntoRole() == 0 || cst.getIntoRole() == 2){
                if(cst.getIntoStatus() == Constant.INTO_STATUS_N){
                    cst.setIntoStatusName("未入住");
                }else if(cst.getIntoStatus() == Constant.INTO_STATUS_Y){
                    cst.setIntoStatusName("已入住");
                } else if(cst.getIntoStatus() == Constant.INTO_STATUS_U){
                    cst.setIntoStatusName("已解绑");
                }
            }else if(cst.getIntoRole() == 1 || cst.getIntoRole() == 3){
                if(cst.getHouseIntoStatus() == Constant.INTO_STATUS_N){
                    cst.setIntoStatusName("未入住");
                }else if(cst.getHouseIntoStatus() == Constant.INTO_STATUS_Y){
                    cst.setIntoStatusName("已入住");
                } else if(cst.getHouseIntoStatus() == Constant.INTO_STATUS_U){
                    cst.setIntoStatusName("已解绑");
                }else if(cst.getHouseIntoStatus() == Constant.INTO_STATUS_A){
                    cst.setIntoStatusName("待审核");
                }
            }

            // 入住身份
            if(cst.getIntoRole() == Constant.INTO_ROLE_CST){
                cst.setIntoRoleName("客户");
            } else if(cst.getIntoRole() == Constant.INTO_ROLE_ENTRUST){
                cst.setIntoRoleName("员工");
            } else if(cst.getIntoRole() == Constant.INTO_ROLE_PROPERTY_OWNER){
                cst.setIntoRoleName("产权人");
            } else if(cst.getIntoRole() == Constant.INTO_ROLE_HOUSEHOLD){
                cst.setIntoRoleName("住户");
            }

            // 客户、产权人查询所有房间号
            List<String> houseList = new ArrayList<>();
            HgjHouse hgjHouse = new HgjHouse();
            hgjHouse.setCstCode(cst.getCstCode());
            List<HgjHouse> hgjHouseList = syHouseDaoMapper.getListByCstCode(hgjHouse);
            if(!hgjHouseList.isEmpty()){
                for(HgjHouse house : hgjHouseList){
                    houseList.add(house.getResName());
                }
            }

            cst.setHouseList(houseList);
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<CstInto> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<User> entityPageInfo = new PageInfo<>();
            entityPageInfo.setList(new ArrayList<>());
            entityPageInfo.setTotal(pageInfo.getTotal());
            entityPageInfo.setPageNum(page);
            entityPageInfo.setPageSize(limit);
            map.put("pageInfo",entityPageInfo);
        }else {
            map.put("pageInfo",pageInfo);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    /**
     * 解除绑定
     * @param id
     * @return
     */
//    @RequestMapping(value = "/delete",method = RequestMethod.GET)
//    public AjaxResult delete(@RequestParam(required=false, value = "id") String id,
//                             @RequestParam(required=false, value = "cstIntoHouseId") String cstIntoHouseId){
//        AjaxResult ajaxResult = new AjaxResult();
//        // 判断是否是客户、业主
//        CstInto cstInto = cstIntoDaoMapper.getById(id);
//        if(cstInto.getIntoRole() == Constant.INTO_ROLE_CST || cstInto.getIntoRole() == Constant.INTO_ROLE_PROPERTY_OWNER){
//
//            CstInto cst = new CstInto();
//            cst.setIntoRole(Constant.INTO_ROLE_CST);
//            cst.setCstCode(cstInto.getCstCode());
//            // 查询业主
//            List<CstInto> cstIntoList = cstIntoDaoMapper.getList(cst);
//            // 查询租户
//            cst.setIntoRole(Constant.INTO_ROLE_ENTRUST);
//            cst.setIntoStatus(Constant.INTO_STATUS_Y);
//            List<CstInto> cstIntoTenantList = cstIntoDaoMapper.getList(cst);
//            // 如果只剩一个业主，需要先解除租户才能解除业主
//            if(!cstIntoList.isEmpty() && cstIntoList.size() == 1 && !cstIntoTenantList.isEmpty()){
//                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
//                ajaxResult.setMessage("请先解除租户在解除业主");
//                return ajaxResult;
//            }
//        }
//        cstIntoDaoMapper.delete(id);
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id,
                             @RequestParam(required=false, value = "cstIntoHouseId") String cstIntoHouseId){
        AjaxResult ajaxResult = new AjaxResult();
        return cstIntoService.unbind(ajaxResult,id,cstIntoHouseId);
    }

    /**
     * 设为客户或业主
     * @param id
     * @return
     */
    @RequestMapping(value = "/owner",method = RequestMethod.GET)
    public AjaxResult owner(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        cstIntoService.owner(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
