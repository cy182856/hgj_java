package com.ej.hgj.controller.master;

import com.alibaba.fastjson.JSON;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.master.FileSecLevel;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.master.FileSecLevelService;
import com.ej.hgj.service.user.UserService;
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
@RequestMapping("/file/sec/level")
public class FileSecLevelController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FileSecLevelService fileSecLevelService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           FileSecLevel fileSecLevel, HttpServletRequest request){
        String token = request.getHeader("X-Token");
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<FileSecLevel> list = fileSecLevelService.getList(fileSecLevel);
        logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<FileSecLevel> pageInfo = new PageInfo<>(list);
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
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(FileSecLevel fileSecLevel){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<FileSecLevel> list = fileSecLevelService.getList(fileSecLevel);
        logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        ajaxResult.setData(map);
        logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(HttpServletRequest request, @RequestBody FileSecLevel fileSecLevel){
        User user = userService.getById(TokenUtils.getUserId(request));
        fileSecLevel.setUpdateBy(user.getId());
        fileSecLevel.setUpdateTime(new Date());
        AjaxResult ajaxResult = new AjaxResult();
        if(fileSecLevel.getId() != null){
            fileSecLevelService.update(fileSecLevel);
        }else{
            fileSecLevel.setId(System.currentTimeMillis()+"");
            fileSecLevel.setCreateBy(user.getId());
            fileSecLevel.setCreateTime(new Date());
            fileSecLevel.setDeleteFlag(0);
            fileSecLevelService.save(fileSecLevel);
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        fileSecLevelService.delete(id);
        ajaxResult.setCode(20000);
        ajaxResult.setMessage("成功");
        return ajaxResult;
    }
}
