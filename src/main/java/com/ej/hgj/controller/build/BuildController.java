package com.ej.hgj.controller.build;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.build.BuildDaoMapper;
import com.ej.hgj.entity.build.Build;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.role.RoleMenuService;
import com.ej.hgj.service.role.RoleService;
import com.ej.hgj.service.user.UserRoleService;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
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
@RequestMapping("/build")
public class BuildController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BuildDaoMapper buildDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(@RequestParam(required=false, value = "projectNum") String projectNum,
                             @RequestParam(required=false, value = "mobile") String mobile){
        AjaxResult ajaxResult = new AjaxResult();
        List<Build> list = new ArrayList<>();
        HashMap map = new HashMap();
        Build b = new Build();
        b.setOrgId(projectNum);
        if(StringUtils.isNotBlank(mobile)){
            b.setMobile(mobile);
            list = buildDaoMapper.getListAll(b);
        }else {
            list = buildDaoMapper.getList(b);
        }
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

}
