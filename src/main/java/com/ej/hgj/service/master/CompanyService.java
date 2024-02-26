package com.ej.hgj.service.master;

import com.ej.hgj.entity.master.Company;
import java.util.List;

public interface CompanyService {

    List<Company> getList(Company company);

    void save(Company company);

    void update(Company company);

    void delete(String id);


}
