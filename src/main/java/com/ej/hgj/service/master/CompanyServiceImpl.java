package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.CompanyDaoMapper;
import com.ej.hgj.entity.master.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CompanyDaoMapper companyDaoMapper;


    public List<Company> getList(Company company){
        return companyDaoMapper.getList(company);
    }

    @Override
    public void save(Company company) {
        companyDaoMapper.save(company);
    }

    @Override
    public void update(Company company) {
        companyDaoMapper.update(company);
    }

    @Override
    public void delete(String id) {
        companyDaoMapper.delete(id);
    }

}
