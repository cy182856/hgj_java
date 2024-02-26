package com.ej.hgj.dao.corp;

import com.ej.hgj.entity.corp.Corp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CorpDaoMapper {

    Corp getById(String id);

    Corp getByCorpId(String corpId);

    List<Corp> getList(Corp corp);

    void save(Corp corp);

    void update(Corp corp);

    void delete(String id);

}
