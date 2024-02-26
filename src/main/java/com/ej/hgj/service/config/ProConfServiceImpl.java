package com.ej.hgj.service.config;

import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.entity.config.ProConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProConfServiceImpl implements ProConfService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProConfDaoMapper proConfDaoMapper;

    @Override
    public ProConfig getById(String id){
        return proConfDaoMapper.getById(id);
    }

    public List<ProConfig> getList(ProConfig proConfig){
        return proConfDaoMapper.getList(proConfig);
    }

    @Override
    public void save(ProConfig role) {
        proConfDaoMapper.save(role);
    }

    @Override
    public void update(ProConfig role) {
        proConfDaoMapper.update(role);
    }

    @Override
    public void delete(String id) {
        proConfDaoMapper.delete(id);
    }

}
