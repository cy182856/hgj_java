package com.ej.hgj.service.sum;

import com.ej.hgj.dao.sum.SumInfoDaoMapper;
import com.ej.hgj.entity.sum.SumFile;
import com.ej.hgj.entity.sum.SumInfo;
import com.ej.hgj.entity.user.UserLog;
import com.ej.hgj.service.user.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class SumInfoServiceImpl implements SumInfoService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private SumInfoDaoMapper sumInfoDaoMapper;

    @Autowired
    private SumFileService sumFileService;

    @Autowired
    private UserLogService userLogService;

    public List<SumInfo> getList(SumInfo sumInfo){
        return sumInfoDaoMapper.getList(sumInfo);
    }

    @Override
    public void save(SumInfo sumInfo) {
        sumInfoDaoMapper.save(sumInfo);
    }

    @Override
    public void update(SumInfo sumInfo) {
        sumInfoDaoMapper.update(sumInfo);
    }

    @Override
    public void delete(String id) {
        sumInfoDaoMapper.delete(id);
    }

    @Override
    public SumInfo findById(String id) {
        return sumInfoDaoMapper.findById(id);
    }

    @Override
    public void uploadFile(MultipartFile file, String sumId, String dirNum, String userId) {
        if(file != null){
            //目录不存在则直接创建
            File filePath = new File(uploadPath);
            if(!filePath.exists()){
                filePath.mkdirs();
            }
            //创建年月日文件夹
            Calendar date = Calendar.getInstance();
            File ymdFile = new File(uploadPath + File.separator + date.get(Calendar.YEAR) + (date.get(Calendar.MONTH)+1) + date.get(Calendar.DAY_OF_MONTH));
            //目录不存在则直接创建
            if(!ymdFile.exists()){
                ymdFile.mkdirs();
            }
            String uploadPath = ymdFile.getPath();
            //获取文件名
            String fileName = file.getOriginalFilename();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = dateFormat.format(new Date());
            String newFileName = strDate + "_" + fileName;
            String savePath = uploadPath +"/"+ newFileName;
            try {
                file.transferTo(new File(savePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //保存文件
            SumFile sumFile = new SumFile();
            sumFile.setId(System.currentTimeMillis()+"");
            sumFile.setSumId(sumId);
            sumFile.setFileName(newFileName);
            sumFile.setFileUrl(savePath);
            sumFile.setDirNum(dirNum);
            sumFile.setCreateTime(new Date());
            sumFile.setUpdateTime(new Date());
            sumFile.setDeleteFlag(0);
            sumFileService.save(sumFile);

            //更新文件数量
            SumInfo sumInfo = this.findById(sumId);
            Integer fileNum = sumInfo.getFileNum();
            if (fileNum == null){
                fileNum = 0;
            }
            sumInfo.setFileNum(fileNum + 1);
            this.update(sumInfo);

            UserLog userLog = new UserLog();
            userLog.setId(System.currentTimeMillis()+"");
            userLog.setOperateUrl("/sum/info/file/upload");
            userLog.setOperateMenu("汇总列表");
            userLog.setUserId(userId);
            userLog.setCreateTime(new Date());
            userLog.setUpdateTime(new Date());
            userLog.setDeleteFlag(0);
            userLog.setOperateContent("附件上传");
            userLog.setOperateId(sumFile.getId());
            userLogService.save(userLog);
        }
    }
}
