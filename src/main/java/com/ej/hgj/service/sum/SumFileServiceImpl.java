package com.ej.hgj.service.sum;

import com.ej.hgj.dao.sum.SumFileDaoMapper;
import com.ej.hgj.entity.sum.SumFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SumFileServiceImpl implements SumFileService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SumFileDaoMapper sumFileDaoMapper;


    public List<SumFile> getList(SumFile sumFile){
        return sumFileDaoMapper.getList(sumFile);
    }

    @Override
    public void save(SumFile sumFile) {
        sumFileDaoMapper.save(sumFile);
    }

    @Override
    public void update(SumFile sumFile) {
        sumFileDaoMapper.update(sumFile);
    }

    @Override
    public void delete(String id) {
        sumFileDaoMapper.delete(id);
    }

    @Override
    public SumFile findById(String id) {
        return sumFileDaoMapper.findById(id);
    }

}
