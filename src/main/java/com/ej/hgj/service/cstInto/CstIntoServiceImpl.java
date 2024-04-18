package com.ej.hgj.service.cstInto;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.CstPayPer;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.enums.QrSceneEnum;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.wechat.WechatPubNumUtils;
import com.ej.hgj.vo.IntoVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CstIntoServiceImpl implements CstIntoService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private CstIntoHouseDaoMapper cstIntoHouseDaoMapper;

    @Override
    public void owner(String id) {
        // 查询入住信息
        CstInto cstInto = cstIntoDaoMapper.getById(id);
        Integer intoRole = cstInto.getIntoRole();
        // 角色是委托人处理
        if(intoRole == Constant.INTO_ROLE_ENTRUST){
            updateIntoRole(id,Constant.INTO_ROLE_CST);
        }else {
            updateIntoRole(id,Constant.INTO_ROLE_PROPERTY_OWNER);
        }
    }

    public void updateIntoRole(String id,Integer intoRole){
        // 更新入住角色为
        CstInto cst = new CstInto();
        cst.setId(id);
        cst.setIntoRole(intoRole);
        cst.setUpdateTime(new Date());
        cstIntoDaoMapper.update(cst);
        // 删除已入住房间
        CstIntoHouse cstIntoHouse = new CstIntoHouse();
        cstIntoHouse.setCstIntoId(id);
        cstIntoHouse.setDeleteFlag(Constant.DELETE_FLAG_YES);
        cstIntoHouse.setUpdateTime(new Date());
        cstIntoHouseDaoMapper.updateByCstIntoId(cstIntoHouse);
    }

//    @Override
//    public void owner(String id) {
//        // 查询该租户已绑定的房屋列表
//        CstInto cstInto = cstIntoDaoMapper.getById(id);
//        // 删除该租户已绑定的房屋
//        cstIntoDaoMapper.deleteByCstCodeAndWxOpenId(cstInto.getCstCode(),cstInto.getWxOpenId());
//        // 设为业主
//        CstInto cInto = new CstInto();
//        cInto.setId(TimestampGenerator.generateSerialNumber());
//        cInto.setWxOpenId(cstInto.getWxOpenId());
//        cInto.setUserName(cstInto.getUserName());
//        cInto.setCstCode(cstInto.getCstCode());
//        cInto.setIntoRole(Constant.INTO_ROLE_CST);
//        cInto.setIntoStatus(Constant.INTO_STATUS_Y);
//        cInto.setCreateTime(new Date());
//        cInto.setUpdateTime(new Date());
//        cInto.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//        cstIntoDaoMapper.save(cInto);
//    }

    @Override
    public String saveCstIntoInfo(IntoVo intoVo) {
        // 根据入住类型预生成入住信息
        String intoType = intoVo.getIntoType();
        String[] resIds = intoVo.getResId();
        CstInto cstInto = new CstInto();
        String cstIntoId = TimestampGenerator.generateSerialNumber();
        cstInto.setId(cstIntoId);
        cstInto.setProjectNum(intoVo.getOrgId());
        cstInto.setCstCode(intoVo.getCstCode());
        cstInto.setIntoRole(Integer.valueOf(intoType));
        cstInto.setIntoStatus(Constant.INTO_STATUS_N);
        cstInto.setCreateTime(new Date());
        cstInto.setUpdateTime(new Date());
        cstInto.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        cstIntoDaoMapper.save(cstInto);
        // 如果是委托人或者住户，需要保存房间号
        if("1".equals(intoType) || "3".equals(intoType)){
            List<CstIntoHouse> cstIntoHouseList = new ArrayList<>();
            for(int i = 0; i<resIds.length; i++){
                CstIntoHouse cstIntoHouse = new CstIntoHouse();
                cstIntoHouse.setId(TimestampGenerator.generateSerialNumber());
                cstIntoHouse.setCstIntoId(cstIntoId);
                cstIntoHouse.setHouseId(resIds[i]);
                cstIntoHouse.setIntoStatus(Constant.INTO_STATUS_N);
                cstIntoHouse.setCreateTime(new Date());
                cstIntoHouse.setUpdateTime(new Date());
                cstIntoHouse.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                cstIntoHouseList.add(cstIntoHouse);
            }
            cstIntoHouseDaoMapper.insertList(cstIntoHouseList);
        }

        return cstIntoId;
    }

    @Override
    public AjaxResult unbind(AjaxResult ajaxResult,String id, String cstIntoHouseId) {
        // 判断是否是客户、业主
        CstInto cstInto = cstIntoDaoMapper.getById(id);
        if(cstInto.getIntoRole() == Constant.INTO_ROLE_CST || cstInto.getIntoRole() == Constant.INTO_ROLE_PROPERTY_OWNER){
            // 查询已入住该客户的委托人或者住户
            List<CstInto> cstIntoList = cstIntoDaoMapper.getByCstCode(cstInto.getCstCode());
            // 客户、业主解绑需要先解除委托人、住户
            if(!cstIntoList.isEmpty()){
                ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
                ajaxResult.setMessage("请先解除委托人或者住户");
                return ajaxResult;
            }else {
                CstInto cst = new CstInto();
                cst.setId(id);
                cst.setIntoStatus(Constant.INTO_STATUS_U);
                cst.setUpdateTime(new Date());
                cstIntoDaoMapper.update(cst);
            }
        }else {
            // 委托人、住户
            CstIntoHouse cstIntoHouse = new CstIntoHouse();
            cstIntoHouse.setId(cstIntoHouseId);
            cstIntoHouse.setIntoStatus(Constant.INTO_STATUS_U);
            cstIntoHouse.setUpdateTime(new Date());
            cstIntoHouseDaoMapper.updateById(cstIntoHouse);

            // 如果住户、委托人绑定房间被全部解除，入住表也解除
            CstInto cs = cstIntoDaoMapper.getById(id);
            List<CstIntoHouse> cstIntoHouseList = cstIntoHouseDaoMapper.getByCstIntoIdAndIntoStatus(id);
            if(cstIntoHouseList.isEmpty() && cs != null && (cs.getIntoRole() == Constant.INTO_ROLE_ENTRUST || cs.getIntoRole() == Constant.INTO_ROLE_HOUSEHOLD)){
                CstInto cst = new CstInto();
                cst.setId(id);
                cstInto.setIntoStatus(Constant.INTO_STATUS_U);
                cstInto.setUpdateTime(new Date());
                cstIntoDaoMapper.update(cstInto);
            }
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }
}
