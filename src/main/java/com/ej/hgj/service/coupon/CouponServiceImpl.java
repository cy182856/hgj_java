package com.ej.hgj.service.coupon;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.CouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.dao.tag.TagDaoMapper;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
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

@Transactional
@Service
public class CouponServiceImpl implements CouponService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponGrantDaoMapper couponGrantDaoMapper;

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private CouponGrantBatchDaoMapper couponGrantBatchDaoMapper;

    @Autowired
    private TagDaoMapper tagDaoMapper;

    @Override
    public void couponGrant(Coupon coupon) {
        String id = coupon.getId();
        String tagId = coupon.getTagId();
        String startTIme = coupon.getStartTime();
        String endTime = coupon.getEndTime();
        // 保存批次表
        CouponGrantBatch couponGrantBatch = new CouponGrantBatch();
        String batchId = TimestampGenerator.generateSerialNumber();
        couponGrantBatch.setId(batchId);
        couponGrantBatch.setCouponId(id);
        couponGrantBatch.setTagId(tagId);
        couponGrantBatch.setTypeCode(coupon.getTypeCode());
        couponGrantBatch.setCouNum(coupon.getCouNum());
        couponGrantBatch.setStartTime(startTIme);
        couponGrantBatch.setEndTime(endTime);
        couponGrantBatch.setCreateTime(new Date());
        couponGrantBatch.setCreateBy("");
        couponGrantBatch.setUpdateTime(new Date());
        couponGrantBatch.setUpdateBy("");
        couponGrantBatch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        couponGrantBatchDaoMapper.save(couponGrantBatch);

        // 保存分发表-批次详情
        Tag tag = tagDaoMapper.getById(tagId);
        List<TagCst> tagCstList = new ArrayList<>();
        if(tag.getRange() == 1){
            TagCst tagCst = new TagCst();
            tagCst.setTagId(tagId);
            tagCstList = tagCstDaoMapper.getList(tagCst);
        }
        if(tag.getRange() == 2){
            TagCst tagCst = new TagCst();
            tagCst.setTagId(tagId);
            tagCstList = tagCstDaoMapper.getCstIntoList(tagCst);
        }
        if (!tagCstList.isEmpty()) {
            List<CouponGrant> couponGrantList = new ArrayList<>();
            for (TagCst cst : tagCstList) {
                CouponGrant sg = new CouponGrant();
                sg.setId(TimestampGenerator.generateSerialNumber());
                sg.setBatchId(batchId);
                sg.setCouponId(id);
                sg.setTagId(tagId);
                sg.setTypeCode(coupon.getTypeCode());
                sg.setCouNum(coupon.getCouNum());
                sg.setExpNum(coupon.getCouNum());
                sg.setStartTime(startTIme);
                sg.setEndTime(endTime);
                if(tag.getRange() == 1){
                    sg.setCstCode(cst.getCstCode());
                    sg.setWxOpenId("");
                    sg.setRange(1);
                }
                if(tag.getRange() == 2){
                    sg.setWxOpenId(cst.getWxOpenId());
                    sg.setCstCode("");
                    sg.setRange(2);
                }
                sg.setCreateTime(new Date());
                sg.setCreateBy("");
                sg.setUpdateTime(new Date());
                sg.setUpdateBy("");
                sg.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                couponGrantList.add(sg);
            }
            couponGrantDaoMapper.insertList(couponGrantList);
        }

    }

    @Override
    public void deleteBatch(String id) {
        // 删除批次
        couponGrantBatchDaoMapper.delete(id);
        // 删除发放详细
        couponGrantDaoMapper.deleteByBatchId(id);
    }
}
