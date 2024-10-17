package com.ej.hgj.controller.tag;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.identity.IdentityDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.dao.user.UserRoleDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.identity.Identity;
import com.ej.hgj.entity.tag.*;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.tag.TagCstService;
import com.ej.hgj.sy.dao.house.HgjSyHouseDaoMapper;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    @Autowired
    private UserRoleDaoMapper userRoleDaoMapper;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private CardDaoMapper cardDaoMapper;

    @Autowired
    private IdentityDaoMapper identityDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Tag tag){
        String userId = TokenUtils.getUserId(request);
        List<UserRole> userRoles = userRoleDaoMapper.getByUserAndRole(userId);
        if(userRoles.isEmpty()){
            // 非管理角色
            tag.setType(1);
        }
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
        tag.setType(1);
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
        // 检查标签名是否重复
        List<Tag> tagList = tagDaoMapper.getByProNumAndName(tag.getProNum(), tag.getName());
        if((tag.getId() == null && !tagList.isEmpty()) || (tag.getId() != null && tagList.size() > 1)){
            if(!tagList.isEmpty()){
                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
                ajaxResult.setMessage("标签名称重复!");
                return ajaxResult;
            }
        }
        if(tag.getId() != null){
            tagDaoMapper.update(tag);
        }else{
            tag.setId(TimestampGenerator.generateSerialNumber());
            tag.setType(1);
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
     * 客户树结构查询,按标签，项目
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/selectCstTree",method = RequestMethod.GET)
    public AjaxResult selectCstTree(@RequestParam("tagId") String tagId, @RequestParam("proNum") String proNum){
        AjaxResult ajaxResult = new AjaxResult();
        // 查询项目
        //List<ProConfig> proList = proConfDaoMapper.getList(new ProConfig());
        ProConfig pc = new ProConfig();
        pc.setProjectNum(proNum);
        List<ProConfig> proList = proConfDaoMapper.getList(pc);
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
                //List<HgjHouse> houseList = hgjSyHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<HgjHouse> houseList = hgjHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<ThreeChildren> threeChildrenList = new ArrayList<>();
                for(HgjHouse house : houseList){
                    ThreeChildren threeChildren = new ThreeChildren();
                    threeChildren.setId(house.getCstCode());
                    threeChildren.setLabel(house.getCstName() + "(" + house.getResName() + ")");
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

    /**
     * 客户树结构查询,按卡ID
     * @param cardId
     * @return
     */
    @RequestMapping(value = "/selectCstTreeByCardId",method = RequestMethod.GET)
    public AjaxResult selectCstTreeByCardId(@RequestParam("cardId") Integer cardId){
        AjaxResult ajaxResult = new AjaxResult();
        Card card = cardDaoMapper.getById(cardId);
        // 查询项目
        //List<ProConfig> proList = proConfDaoMapper.getList(new ProConfig());
        ProConfig pc = new ProConfig();
        pc.setProjectNum(card.getProNum());
        List<ProConfig> proList = proConfDaoMapper.getList(pc);
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
                //List<HgjHouse> houseList = hgjSyHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<HgjHouse> houseList = hgjHouseDaoMapper.queryRoomNum(bud.getBudId());
                List<ThreeChildren> threeChildrenList = new ArrayList<>();
                for(HgjHouse house : houseList){
                    ThreeChildren threeChildren = new ThreeChildren();
                    threeChildren.setId(house.getCstCode());
                    threeChildren.setLabel(house.getCstName() + "(" + house.getResName() + ")");
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

//        // 获取已被选中的客户树
//        TagCst tagCstPram = new TagCst();
//        tagCstPram.setTagId(tagId);
//        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCstPram);
//        // list转数组满足前端需求
//        List<String> cstCodes = tagCstList.stream().map(tagCst -> tagCst.getCstCode()).collect(Collectors.toList());
//        String[] tagExpandedKeys = cstCodes.toArray(new String[cstCodes.size()]);
//        String[] tagCheckedKeys = cstCodes.toArray(new String[cstCodes.size()]);
//        // 展开菜单数组
//        map.put("tagExpandedKeys",tagExpandedKeys);
//        // 选中的菜单数组
//        map.put("tagCheckedKeys",tagCheckedKeys);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }


    /**
     * 个人树结构查询
     * @return
     */
    @RequestMapping(value = "/selectCstTreePerson",method = RequestMethod.POST)
    public AjaxResult selectCstTreePerson(@RequestBody TagCst tc){
        //public AjaxResult selectCstTreePerson(@RequestParam("tagId") String tagId, @RequestParam("proNum") String proNum, @RequestParam(required=false, value = "intoRole") Integer[] intoRole){
        String proNum = tc.getProNum();
        String tagId = tc.getTagId();
        Integer[] intoRole = tc.getIntoRole();
        List<Integer> intoRoleList = new ArrayList<>();
        if(intoRole != null){
            intoRoleList = Arrays.asList(intoRole);
        }
        AjaxResult ajaxResult = new AjaxResult();
        ProConfig proConfig = proConfDaoMapper.getByProjectNum(proNum);
        List<OneTreeData> oneTreeDataList = new ArrayList<>();
        OneTreeData oneTreeData = new OneTreeData();
        oneTreeData.setId(proConfig.getProjectNum());
        oneTreeData.setLabel(proConfig.getProjectName());
        // 获取二级-对应项目下已入住个人
        CstInto cstIntoPram = new CstInto();
        cstIntoPram.setProjectNum(proNum);
        cstIntoPram.setIntoRoleList(intoRoleList);
        List<CstInto> cstIntoList = cstIntoDaoMapper.getListByProNumAndIntoRole(cstIntoPram);
        List<TwoChildren> twoChildrenList = new ArrayList<>();
        // 查询所有身份
        List<Identity> identityList = identityDaoMapper.getList(new Identity());
        for(CstInto cstInto : cstIntoList){
            TwoChildren twoChildren = new TwoChildren();
            twoChildren.setId(cstInto.getWxOpenId());
            List<Identity> identitiesFilter = identityList.stream().filter(identity -> identity.getCode() == cstInto.getIntoRole()).collect(Collectors.toList());
            twoChildren.setLabel(cstInto.getUserName() + "(" + cstInto.getCstName() + ")" + " || " + identitiesFilter.get(0).getName());
            twoChildrenList.add(twoChildren);
        }
        oneTreeData.setChildren(twoChildrenList);
        oneTreeDataList.add(oneTreeData);

        HashMap map = new HashMap();
        // web所有菜单
        map.put("personTreeData",oneTreeDataList);

        // 获取已被选中的个人
        TagCst tagCstPram = new TagCst();
        tagCstPram.setTagId(tagId);
        List<TagCst> tagCstList = tagCstDaoMapper.getListPerson(tagCstPram);
        // list转数组满足前端需求
        List<String> wxOpenIds = tagCstList.stream().map(tagCst -> tagCst.getWxOpenId()).collect(Collectors.toList());
        String[] tagExpandedKeys = wxOpenIds.toArray(new String[wxOpenIds.size()]);
        String[] tagCheckedKeys = wxOpenIds.toArray(new String[wxOpenIds.size()]);
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

    @RequestMapping(value = "/selectCstList",method = RequestMethod.GET)
    public AjaxResult selectCstList(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        List<HgjCst> list = new ArrayList<>();
        HashMap map = new HashMap();
        Tag tag = tagDaoMapper.getById(id);
        if(tag.getRange() == 1){
            list = tagCstDaoMapper.getCstByTagId(id);
        }else if(tag.getRange() == 2){
            list = tagCstDaoMapper.getCstIntoByTagId(id);
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

}
