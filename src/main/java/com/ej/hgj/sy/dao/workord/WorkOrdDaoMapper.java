package com.ej.hgj.sy.dao.workord;

import com.ej.hgj.entity.workord.WorkOrd;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WorkOrdDaoMapper {

    WorkOrd getGgBxWoNo(String proNum);

    WorkOrd getKhBxWoNo(String proNum);

    WorkOrd getCsWorkOrd(String woNo);

    List<WorkOrd> getList(List<String> woNoList);

    WorkOrd getCostSum(String woId);

}
