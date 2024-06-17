package com.ej.hgj.service.coupon;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.CouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
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

    @Override
    public void couponGrant(Coupon coupon) {
        String id = coupon.getId();
        String tagId = coupon.getTagId();
        String startTIme = coupon.getStartTime();
        String endTime = coupon.getEndTime();
        // 保存批次表
        CouponGrantBatch stopCouponGrantBatch = new CouponGrantBatch();
        String batchId = TimestampGenerator.generateSerialNumber();
        stopCouponGrantBatch.setId(batchId);
        stopCouponGrantBatch.setCouponId(id);
        stopCouponGrantBatch.setTagId(tagId);
        stopCouponGrantBatch.setStartTime(startTIme);
        stopCouponGrantBatch.setEndTime(endTime);
        stopCouponGrantBatch.setCreateTime(new Date());
        stopCouponGrantBatch.setCreateBy("");
        stopCouponGrantBatch.setUpdateTime(new Date());
        stopCouponGrantBatch.setUpdateBy("");
        stopCouponGrantBatch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        couponGrantBatchDaoMapper.save(stopCouponGrantBatch);

        // 保存分发表-批次详情
        TagCst tagCst = new TagCst();
        tagCst.setTagId(tagId);
        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCst);
        if(!tagCstList.isEmpty()) {
            List<CouponGrant> couponGrantList = new ArrayList<>();
            for (TagCst cst : tagCstList) {
                CouponGrant sg = new CouponGrant();
                sg.setId(TimestampGenerator.generateSerialNumber());
                sg.setBatchId(batchId);
                sg.setCouponId(id);
                sg.setTagId(tagId);
                sg.setStartTime(startTIme);
                sg.setEndTime(endTime);
                sg.setCstCode(cst.getCstCode());
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
