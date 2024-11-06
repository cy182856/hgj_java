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
import java.util.stream.Collectors;

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
    public void couponGrant(CouponGrantBatch couponGrantBatchReq) {
        String id = couponGrantBatchReq.getId();
        String tagId = couponGrantBatchReq.getTagId();
        String startTIme = couponGrantBatchReq.getStartTime();
        //String endTime = coupon.getEndTime();
        // 保存批次表
        CouponGrantBatch couponGrantBatch = new CouponGrantBatch();
        String batchId = TimestampGenerator.generateSerialNumber();
        couponGrantBatch.setId(batchId);
        couponGrantBatch.setCouponId(id);
        couponGrantBatch.setTagId(tagId);
        couponGrantBatch.setTypeCode(couponGrantBatchReq.getTypeCode());
        couponGrantBatch.setCouNum(couponGrantBatchReq.getCouNum());
        couponGrantBatch.setStartTime(startTIme);
        //couponGrantBatch.setEndTime(endTime);
        couponGrantBatch.setCreateTime(new Date());
        couponGrantBatch.setCreateBy("");
        couponGrantBatch.setUpdateTime(new Date());
        couponGrantBatch.setUpdateBy("");
        couponGrantBatch.setDeleteFlag(Constant.DELETE_FLAG_NOT);

        // 根据券有效日期查询券发放明细
        List<CouponGrant> listByExpDate = couponGrantDaoMapper.getListByExpDate(startTIme);

        // 发放明细
        List<CouponGrant> couponGrantList = new ArrayList<>();

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
            for (TagCst cst : tagCstList) {
                CouponGrant sg = new CouponGrant();
                sg.setId(TimestampGenerator.generateSerialNumber());
                sg.setBatchId(batchId);
                sg.setCouponId(id);
                sg.setTagId(tagId);
                sg.setTypeCode(couponGrantBatchReq.getTypeCode());
                sg.setCouNum(couponGrantBatchReq.getCouNum());
                sg.setApplyNum(0);
                sg.setStartTime(startTIme);
                //sg.setEndTime(endTime);
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

                // 如果有效月份未发放过券，才可以发放
                List<CouponGrant> listByExpDateFilter = listByExpDate.stream().filter(couponGrant -> couponGrant.getCstCode().equals(cst.getCstCode())).collect(Collectors.toList());
                if(listByExpDateFilter.isEmpty()) {
                    couponGrantList.add(sg);
                }
            }
        }
        if(!couponGrantList.isEmpty()) {
            // 保存发放明细
            couponGrantDaoMapper.insertList(couponGrantList);
            // 保存批次
            couponGrantBatchDaoMapper.save(couponGrantBatch);
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
