package com.ej.hgj.controller.role;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.service.role.RoleService;
import com.ej.hgj.service.user.UserRoleService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("/role")
public class RoleController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Role role){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Role> list = roleService.getList(role);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Role> pageInfo = new PageInfo<>(list);
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

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(Role role){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Role> list = roleService.getList(role);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Role role){
        AjaxResult ajaxResult = new AjaxResult();
        if(role.getId() != null){
            roleService.update(role);
        }else{
            role.setId(TimestampGenerator.generateSerialNumber());
            //role.setProjectNum(Constant.PROJECT_NUM);
            role.setUpdateTime(new Date());
            role.setCreateTime(new Date());
            role.setDeleteFlag(0);
            roleService.save(role);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/saveRoleMenu",method = RequestMethod.POST)
    public AjaxResult saveRoleMenu(@RequestBody Role role){
        AjaxResult ajaxResult = new AjaxResult();
        roleMenuService.saveRoleMenu(role);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        UserRole userRole = new UserRole();
        userRole.setRoleId(id);
        List<UserRole> userRoleList = userRoleService.getList(userRole);
        if(!userRoleList.isEmpty()){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("该角色有关联用户，不能删除！");
        }else {
            roleService.delete(id);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        }
        return ajaxResult;
    }
}
