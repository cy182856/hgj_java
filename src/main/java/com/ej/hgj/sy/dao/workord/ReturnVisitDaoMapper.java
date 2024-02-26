package com.ej.hgj.sy.dao.workord;

import com.ej.hgj.entity.workord.ReturnVisit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ReturnVisitDaoMapper {

    List<ReturnVisit> getList(String woId);

}
