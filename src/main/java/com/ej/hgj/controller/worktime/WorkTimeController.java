package com.ej.hgj.controller.worktime;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.WorkTimeConfDaoMapper;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.config.WorkTimeConfig;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/workTime")
public class WorkTimeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WorkTimeConfDaoMapper workTimeConfDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           WorkTimeConfig workTimeConfig){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<WorkTimeConfig> list = workTimeConfDaoMapper.getList(new WorkTimeConfig());
        PageInfo<WorkTimeConfig> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<WorkTimeConfig> entityPageInfo = new PageInfo<>();
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
        return ajaxResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public AjaxResult update(HttpServletRequest request, @RequestBody WorkTimeConfig workTimeConfig){
        String userId = TokenUtils.getUserId(request);
        workTimeConfig.setUpdateTime(new Date());
        workTimeConfig.setUpdateBy(userId);
        workTimeConfDaoMapper.update(workTimeConfig);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
