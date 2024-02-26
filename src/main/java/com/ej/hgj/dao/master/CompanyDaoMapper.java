package com.ej.hgj.dao.master;

import com.ej.hgj.entity.master.Company;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Mapper
@Component
public interface CompanyDaoMapper {

    List<Company> getList(Company company);

    void save(Company company);

    void update(Company company);

    void delete(String id);

}
