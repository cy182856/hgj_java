package com.ej.hgj.controller.feedback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.feedback.FeedbackDaoMapper;
import com.ej.hgj.dao.gonggao.GonggaoDaoMapper;
import com.ej.hgj.dao.gonggao.GonggaoTypeDaoMapper;
import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.feedback.FeedBack;
import com.ej.hgj.entity.gonggao.Gonggao;
import com.ej.hgj.entity.opendoor.OpenDoorCodeService;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.MyX509TrustManager;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.file.FileSendClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FeedbackDaoMapper feedbackDaoMapper;


    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           FeedBack feedBack){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<FeedBack> list = feedbackDaoMapper.getByList(feedBack);
        for(FeedBack f : list){
            if(StringUtils.isNotBlank(f.getImage())){
                // 获取图片路径
                String imgPath = f.getImage();
                // 拼接远程文件地址
                String fileUrl = Constant.REMOTE_FILE_URL + "/" + imgPath;
                String fileContent = FileSendClient.downloadFileContent(fileUrl);
                if(StringUtils.isNotBlank(fileContent)) {
                    String[] fileList = fileContent.split(",");
                    for(int i = 0; i<fileList.length; i++){
                        fileList[i] = "data:image/jpeg;base64," + fileList[i];
                    }
                    f.setFileList(fileList);
                }
//                String base64Img = "";
//                try {
//                    // 创建BufferedReader对象，从本地文件中读取
//                    BufferedReader reader = new BufferedReader(new FileReader(imgPath));
//                    // 逐行读取文件内容
//                    String line = "";
//                    while ((line = reader.readLine()) != null) {
//                        base64Img += line;
//                    }
//                    // 关闭文件
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //String[] fileList = base64Img.split(",");
//                for(int i = 0; i<fileList.length; i++){
//                    fileList[i] = "data:image/jpeg;base64," + fileList[i];
//                }
//                f.setFileList(fileList);
                //base64Img = "data:image/jpeg;base64," + base64Img;
                //f.setImage(base64Img);
            }
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<FeedBack> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Gonggao> entityPageInfo = new PageInfo<>();
            entityPageInfo.setList(new ArrayList<>());
            entityPageInfo.setTotal(pageInfo.getTotal());
            entityPageInfo.setPageNum(page);
            entityPageInfo.setPageSize(limit);
            map.put("pageInfo",entityPageInfo);
        }else {
            map.put("pageInfo",pageInfo);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

}
