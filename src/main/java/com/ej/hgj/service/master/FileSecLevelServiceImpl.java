package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.FileSecLevelDaoMapper;
import com.ej.hgj.entity.master.FileSecLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileSecLevelServiceImpl implements FileSecLevelService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FileSecLevelDaoMapper fileSecLevelDaoMapper;


    public List<FileSecLevel> getList(FileSecLevel fileSecLevel){
        return fileSecLevelDaoMapper.getList(fileSecLevel);
    }

    @Override
    public void save(FileSecLevel fileSecLevel) {
        fileSecLevelDaoMapper.save(fileSecLevel);
    }

    @Override
    public void update(FileSecLevel fileSecLevel) {
        fileSecLevelDaoMapper.update(fileSecLevel);
    }

    @Override
    public void delete(String id) {
        fileSecLevelDaoMapper.delete(id);
    }

}
