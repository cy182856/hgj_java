package com.ej.hgj.service.tag;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.utils.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagCstServiceImpl implements TagCstService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private TagDaoMapper tagDaoMapper;

    @Override
    public void saveTagCst(Tag tag) {
        String tagId = tag.getId();
        Tag t = tagDaoMapper.getById(tagId);
        List<String> cstCodes = tag.getCstCodes();
        List<String> wxOpenIds = tag.getWxOpenIds();
        tagCstDaoMapper.delete(tagId);
        logger.info("删除客户标签成功tagId:" + tagId);
        List<TagCst> tagCstList = new ArrayList<>();
        // 选择客户
        if(cstCodes != null){
            // 对已选择的客户编号去重复
            Set<String> setCstCodes = new HashSet<>(cstCodes);
            List<String> cstCodeList = new ArrayList<>(setCstCodes);
            List<HgjCst> cstList = hgjCstDaoMapper.getList(new HgjCst());
            for(int i = 0; i<cstCodeList.size(); i++){
                TagCst tagCst = new TagCst();
                String cstCode = cstCodeList.get(i);
                tagCst.setId(TimestampGenerator.generateSerialNumber());
                tagCst.setTagId(tagId);
                tagCst.setCstCode(cstCode);
                tagCst.setRange(t.getRange());
                tagCst.setCreateBy("");
                tagCst.setCreateTime(new Date());
                tagCst.setUpdateBy("");
                tagCst.setUpdateTime(new Date());
                tagCst.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据客户编号查询客户表，如果查不到就不保存
                List<HgjCst> cstListFilter = cstList.stream().filter(hgjCst -> hgjCst.getCode().equals(cstCode)).collect(Collectors.toList());
                if(!cstListFilter.isEmpty()){
                    tagCstList.add(tagCst);
                }
            }
        }

        // 选择个人
        if(wxOpenIds != null){
            for(int i = 0; i<wxOpenIds.size(); i++){
                TagCst tagCst = new TagCst();
                String wxOpenId = wxOpenIds.get(i);
                tagCst.setId(TimestampGenerator.generateSerialNumber());
                tagCst.setTagId(tagId);
                tagCst.setWxOpenId(wxOpenId);
                tagCst.setRange(t.getRange());
                tagCst.setCreateBy("");
                tagCst.setCreateTime(new Date());
                tagCst.setUpdateBy("");
                tagCst.setUpdateTime(new Date());
                tagCst.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                tagCstList.add(tagCst);
            }
        }

        // 新增客户标签
        if(!tagCstList.isEmpty()){
            tagCstDaoMapper.insertList(tagCstList);
            logger.info("新增客户标签成功:"+ JSONObject.toJSONString(tagCstList));
        }
    }

}
