package com.ej.hgj.service.tag;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.utils.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TagCstServiceImpl implements TagCstService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Override
    public void saveTagCst(Tag tag) {
        String tagId = tag.getId();
        List<String> cstCodes = tag.getCstCodes();
        tagCstDaoMapper.delete(tagId);
        logger.info("删除客户标签成功tagId:" + tagId);
        List<TagCst> tagCstList = new ArrayList<>();
        if(cstCodes != null){
            for(int i = 0; i<cstCodes.size(); i++){
                TagCst tagCst = new TagCst();
                String cstCode = cstCodes.get(i);
                tagCst.setId(TimestampGenerator.generateSerialNumber());
                tagCst.setTagId(tagId);
                tagCst.setCstCode(cstCode);
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
