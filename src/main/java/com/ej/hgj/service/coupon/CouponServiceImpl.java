package com.ej.hgj.service.coupon;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.coupon.StopCouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.StopCouponGrantDaoMapper;
import com.ej.hgj.dao.role.RoleDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.coupon.StopCoupon;
import com.ej.hgj.entity.coupon.StopCouponGrant;
import com.ej.hgj.entity.coupon.StopCouponGrantBatch;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.service.role.RoleService;
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
    private StopCouponGrantDaoMapper stopCouponGrantDaoMapper;

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private StopCouponGrantBatchDaoMapper stopCouponGrantBatchDaoMapper;

    @Override
    public void couponGrant(StopCoupon stopCoupon) {
        String id = stopCoupon.getId();
        String tagId = stopCoupon.getTagId();
        String startTIme = stopCoupon.getStartTime();
        String endTime = stopCoupon.getEndTime();
        // 保存批次表
        StopCouponGrantBatch stopCouponGrantBatch = new StopCouponGrantBatch();
        stopCouponGrantBatch.setId(TimestampGenerator.generateSerialNumber());
        stopCouponGrantBatch.setStopCouponId(id);
        stopCouponGrantBatch.setTagId(tagId);
        stopCouponGrantBatch.setStartTime(startTIme);
        stopCouponGrantBatch.setEndTime(endTime);
        stopCouponGrantBatch.setCreateTime(new Date());
        stopCouponGrantBatch.setCreateBy("");
        stopCouponGrantBatch.setUpdateTime(new Date());
        stopCouponGrantBatch.setUpdateBy("");
        stopCouponGrantBatch.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        stopCouponGrantBatchDaoMapper.save(stopCouponGrantBatch);

        // 保存分发表
        TagCst tagCst = new TagCst();
        tagCst.setTagId(tagId);
        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCst);
        if(!tagCstList.isEmpty()) {
            List<StopCouponGrant> stopCouponGrantList = new ArrayList<>();
            for (TagCst cst : tagCstList) {
                StopCouponGrant sg = new StopCouponGrant();
                sg.setId(TimestampGenerator.generateSerialNumber());
                sg.setStopCouponId(id);
                sg.setTagId(tagId);
                sg.setStartTime(startTIme);
                sg.setEndTime(endTime);
                sg.setCstCode(cst.getCstCode());
                sg.setCreateTime(new Date());
                sg.setCreateBy("");
                sg.setUpdateTime(new Date());
                sg.setUpdateBy("");
                sg.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                stopCouponGrantList.add(sg);
            }
            stopCouponGrantDaoMapper.insertList(stopCouponGrantList);
        }
    }
}
