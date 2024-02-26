package com.ej.hgj.sy.dao.workord;

import com.ej.hgj.entity.workord.WorkPos;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface WorkPosDaoMapper {

    WorkPos getWorkPos(String proNum, String resCode);

}
