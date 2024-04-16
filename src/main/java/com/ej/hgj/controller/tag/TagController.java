package com.ej.hgj.controller.tag;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.tag.*;
import com.ej.hgj.service.tag.TagCstService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
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
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/tag")
public class TagController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagDaoMapper tagDaoMapper;

    @Autowired
    private ProConfDaoMapper proConfDaoMapper;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private HgjSyHouseDaoMapper hgjSyHouseDaoMapper;

    @Autowired
    private TagCstService tagCstService;

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Tag tag){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Tag> list = tagDaoMapper.getList(tag);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Tag> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<GonggaoType> entityPageInfo = new PageInfo<>();
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
    public AjaxResult select(Tag tag){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Tag> list = tagDaoMapper.getList(tag);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Tag tag){
        AjaxResult ajaxResult = new AjaxResult();
        if(tag.getId() != null){
            tagDaoMapper.update(tag);
        }else{
            tag.setId(TimestampGenerator.generateSerialNumber());
            tag.setUpdateTime(new Date());
            tag.setCreateTime(new Date());
            tag.setDeleteFlag(0);
            tagDaoMapper.save(tag);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        TagCst tagCst = new TagCst();
        tagCst.setTagId(id);
        List<TagCst> list = tagCstDaoMapper.getList(tagCst);
        if(!list.isEmpty()){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("该标签有关联客户，不能删除！");
        }else {
            tagDaoMapper.delete(id);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        }
        return ajaxResult;
    }

    /**
     * 客户树结构查询
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/selectCstTree",method = RequestMethod.GET)
    public AjaxResult selectUserMenu(@RequestParam("tagId") String tagId){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询项目
        List<ProConfig> proList = proConfDaoMapper.getList(new ProConfig());
        List<OneTreeData> oneTreeDataList = new ArrayList<>();
        for(ProConfig proConfig : proList){
            OneTreeData oneTreeData = new OneTreeData();
            oneTreeData.setId(proConfig.getProjectNum());
            oneTreeData.setLabel(proConfig.getProjectName());
            // 获取二级-楼栋
            List<HgjHouse> budList = hgjHouseDaoMapper.queryBuilding(proConfig.getProjectNum());
            List<TwoChildren> twoChildrenList = new ArrayList<>();
            for(HgjHouse bud : budList){
                TwoChildren twoChildren = new TwoChildren();
                twoChildren.setId(bud.getBudId());
                twoChildren.setLabel(bud.getBudName());
                // 获取三级-房号
                List<HgjHouse> houseList = hgjSyHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<ThreeChildren> threeChildrenList = new ArrayList<>();
                for(HgjHouse house : houseList){
                    ThreeChildren threeChildren = new ThreeChildren();
                    threeChildren.setId(house.getCstCode());
                    threeChildren.setLabel(house.getCstName());
                    threeChildrenList.add(threeChildren);
                }
                twoChildren.setChildren(threeChildrenList);
                twoChildrenList.add(twoChildren);
            }
            oneTreeData.setChildren(twoChildrenList);
            oneTreeDataList.add(oneTreeData);
        }

        HashMap map = new HashMap();
        // web所有菜单
        map.put("cstTreeData",oneTreeDataList);

        // 获取已被选中的客户树
        TagCst tagCstPram = new TagCst();
        tagCstPram.setTagId(tagId);
        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCstPram);
        // list转数组满足前端需求
        List<String> cstCodes = tagCstList.stream().map(tagCst -> tagCst.getCstCode()).collect(Collectors.toList());
        String[] tagExpandedKeys = cstCodes.toArray(new String[cstCodes.size()]);
        String[] tagCheckedKeys = cstCodes.toArray(new String[cstCodes.size()]);
        // 展开菜单数组
        map.put("tagExpandedKeys",tagExpandedKeys);
        // 选中的菜单数组
        map.put("tagCheckedKeys",tagCheckedKeys);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    @RequestMapping(value = "/saveTagCst",method = RequestMethod.POST)
    public AjaxResult saveTagCst(@RequestBody Tag tag){
        AjaxResult ajaxResult = new AjaxResult();
        tagCstService.saveTagCst(tag);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}