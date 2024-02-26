package com.ej.hgj.service.sum;

import com.ej.hgj.entity.sum.SumInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SumInfoService {

    List<SumInfo> getList(SumInfo sumInfo);

    void save(SumInfo sumInfo);

    void update(SumInfo sumInfo);

    void delete(String id);

    SumInfo findById(String id);

    void uploadFile(MultipartFile file, String sumId, String dirNum, String userId);


}
