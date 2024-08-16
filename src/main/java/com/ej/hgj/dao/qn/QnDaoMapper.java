package com.ej.hgj.dao.qn;

import com.ej.hgj.entity.gonggao.Gonggao;
import com.ej.hgj.entity.qn.Qn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface QnDaoMapper {

    Qn getById(String id);

    List<Qn> getList(Qn qn);

    void save(Qn qn);

    void update(Qn qn);

    void delete(String id);

    void miniIsShow(String id);

    void notMiniIsShow(String id);

    void pubMenuIsShow(String id);

    void notPubMenuIsShow(String id);


}
