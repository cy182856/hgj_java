package com.ej.hgj.controller.gonggao;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.gonggao.GonggaoDaoMapper;
import com.ej.hgj.dao.gonggao.GonggaoTypeDaoMapper;
import com.ej.hgj.dao.message.MsgTemplateDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubUserDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.gonggao.Gonggao;
import com.ej.hgj.entity.message.MsgTemplate;
import com.ej.hgj.entity.wechat.*;
import com.ej.hgj.utils.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/gonggao")
public class GonggaoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private GonggaoTypeDaoMapper gonggaoTypeDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private ProConfDaoMapper proConfDaoMapper;

    @Autowired
    private GonggaoDaoMapper gonggaoDaoMapper;

    @Autowired
    private WechatPubDaoMapper wechatPubDaoMapper;

    @Autowired
    private WechatPubUserDaoMapper wechatPubUserDaoMapper;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private MsgTemplateDaoMapper msgTemplateDaoMapper;

    @RequestMapping(value = "/updateRelease",method = RequestMethod.GET)
    public AjaxResult updateRelease(){
        AjaxResult ajaxResult = new AjaxResult();
        List<ProConfig> list = proConfDaoMapper.getList(new ProConfig());
        for(ProConfig proConfig : list){
            // 更新已发布列表
            syncWxPubContentArticle(proConfig.getProjectNum());
            // 更新草稿箱列表
            syncWxPubContentMedia(proConfig.getProjectNum());
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    public void syncWxPubContentArticle(String proNum){
        ConstantConfig weChatPubApp =  constantConfDaoMapper.getByProNumAndKey(proNum, Constant.WE_CHAT_PUB_APP);
        // 获取公众号token
        String wxPubToken = getWxPubToken(weChatPubApp.getAppId(),weChatPubApp.getAppSecret());
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/batchget?access_token=" + wxPubToken;
        Map<String, String> params = new HashMap<String, String>();
        params.put("offset", "0");
        params.put("count", "20");
        params.put("no_content", "0");
        String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(params);
        JSONObject jsonObjectPost = httpRequest(url, "POST", jsonStr);
        if(jsonObjectPost != null){
            String item = JSONObject.toJSONString(jsonObjectPost.get("item"));
            String total = JSONObject.toJSONString(jsonObjectPost.get("total_count"));
            if(Integer.valueOf(total)>0) {
                //item为数组json类型，这时需要转换成JSONArray
                JSONArray jsonArray = JSONObject.parseArray(item);
                int size = jsonArray.size();
                // 查询数据库已有公告列表
                Gonggao gg = new Gonggao();
                gg.setProNum(proNum);
                List<Gonggao> alreadylist = gonggaoDaoMapper.getList(gg);
                List<Gonggao> gonggaoList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String content = jsonObject.getString("content");
                    String articleId = jsonObject.getString("article_id");
                    String updateTime = jsonObject.getString("update_time");
                    // 根据articleId查询数据库，没有就同步
                    List<Gonggao> gonggaos = alreadylist.stream().filter(gonggao -> articleId.equals(gonggao.getArticleId())).collect(Collectors.toList());
                    //content为文章模块
                    JSONObject jsonObjectContent = JSON.parseObject(content);
                    //取出文章列表信息并转成json
                    String news = jsonObjectContent.getString("news_item");
                    JSONArray jsonArrayNews = JSONObject.parseArray(news);
                    if(gonggaos.isEmpty()){
                        for (int j = 0; j < jsonArrayNews.size(); j++) {
                            JSONObject jsonObjectNews = jsonArrayNews.getJSONObject(j);
                            Gonggao gonggao = new Gonggao();
                            gonggao.setId(TimestampGenerator.generateSerialNumber());
                            gonggao.setProNum(proNum);
                            gonggao.setArticleId(articleId);
                            gonggao.setIsDeleted(jsonObjectNews.getString("is_deleted"));
                            gonggao.setAuthor(jsonObjectNews.getString("author"));
                            gonggao.setTitle(jsonObjectNews.getString("title"));
                            gonggao.setUrl(jsonObjectNews.getString("url"));
                            gonggao.setThumbUrl(jsonObjectNews.getString("thumb_url"));
                            // 默认1,不显示  0-显示
                            gonggao.setIsShow(1);
                            // 来源：1-公众号 2-编辑器
                            gonggao.setSource(1);
                            gonggao.setCreateTime(new Date());
                            gonggao.setUpdateTime(new Date(Long.valueOf(updateTime) * 1000));
                            gonggao.setCreateBy("");
                            gonggao.setCreateBy("");
                            gonggao.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                            gonggaoList.add(gonggao);
                        }
                    }
                }
                if(!gonggaoList.isEmpty()){
                    gonggaoDaoMapper.insertList(gonggaoList);
                }
            }
        }
    }

    public void syncWxPubContentMedia(String proNum){
        ConstantConfig weChatPubApp =  constantConfDaoMapper.getByProNumAndKey(proNum, Constant.WE_CHAT_PUB_APP);
        // 获取公众号token
        String wxPubToken = getWxPubToken(weChatPubApp.getAppId(),weChatPubApp.getAppSecret());
        String url = "https://api.weixin.qq.com/cgi-bin/draft/batchget?access_token=" + wxPubToken;
        Map<String, String> params = new HashMap<String, String>();
        params.put("offset", "0");
        params.put("count", "20");
        params.put("no_content", "0");
        String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(params);
        JSONObject jsonObjectPost = httpRequest(url, "POST", jsonStr);
        if(jsonObjectPost != null){
            String item = JSONObject.toJSONString(jsonObjectPost.get("item"));
            String total = JSONObject.toJSONString(jsonObjectPost.get("total_count"));
            if(Integer.valueOf(total)>0) {
                //item为数组json类型，这时需要转换成JSONArray
                JSONArray jsonArray = JSONObject.parseArray(item);
                int size = jsonArray.size();
                // 查询数据库已有公告列表
                Gonggao gg = new Gonggao();
                gg.setProNum(proNum);
                List<Gonggao> alreadylist = gonggaoDaoMapper.getList(gg);
                List<Gonggao> gonggaoList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String content = jsonObject.getString("content");
                    String mediaId = jsonObject.getString("media_id");
                    String updateTime = jsonObject.getString("update_time");
                    // 根据articleId查询数据库，没有就同步
                    List<Gonggao> gonggaos = alreadylist.stream().filter(gonggao -> mediaId.equals(gonggao.getMediaId())).collect(Collectors.toList());
                    //content为文章模块
                    JSONObject jsonObjectContent = JSON.parseObject(content);
                    //取出文章列表信息并转成json
                    String news = jsonObjectContent.getString("news_item");
                    JSONArray jsonArrayNews = JSONObject.parseArray(news);
                    if(gonggaos.isEmpty()){
                        for (int j = 0; j < jsonArrayNews.size(); j++) {
                            JSONObject jsonObjectNews = jsonArrayNews.getJSONObject(j);
                            Gonggao gonggao = new Gonggao();
                            gonggao.setId(TimestampGenerator.generateSerialNumber());
                            gonggao.setProNum(proNum);
                            gonggao.setMediaId(mediaId);
                            gonggao.setAuthor(jsonObjectNews.getString("author"));
                            gonggao.setTitle(jsonObjectNews.getString("title"));
                            gonggao.setUrl(jsonObjectNews.getString("url"));
                            gonggao.setThumbUrl(jsonObjectNews.getString("thumb_url"));
                            // 默认1,不显示  0-显示
                            gonggao.setIsShow(1);
                            // 来源：1-公众号 2-编辑器
                            gonggao.setSource(1);
                            gonggao.setCreateTime(new Date());
                            gonggao.setUpdateTime(new Date(Long.valueOf(updateTime) * 1000));
                            gonggao.setCreateBy("");
                            gonggao.setCreateBy("");
                            gonggao.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                            gonggaoList.add(gonggao);
                        }
                    }
                }
                if(!gonggaoList.isEmpty()){
                    gonggaoDaoMapper.insertList(gonggaoList);
                }
            }
        }
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Gonggao gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Gonggao> list = gonggaoDaoMapper.getList(gonggao);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Gonggao> pageInfo = new PageInfo<>(list);
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

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(Gonggao gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<Gonggao> list = gonggaoDaoMapper.getList(gonggao);
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(@RequestBody Gonggao gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        if(gonggao.getId() != null){
            gonggaoDaoMapper.update(gonggao);
        }else{
            gonggao.setId(TimestampGenerator.generateSerialNumber());
            gonggao.setUpdateTime(new Date());
            gonggao.setCreateTime(new Date());
            gonggao.setDeleteFlag(0);
            gonggaoDaoMapper.save(gonggao);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        gonggaoDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/isShow",method = RequestMethod.GET)
    public AjaxResult isShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        Gonggao gonggao = gonggaoDaoMapper.getById(id);
        if(StringUtils.isBlank(gonggao.getType())){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("请先选择分类！");
            return ajaxResult;
        }
        gonggaoDaoMapper.isShow(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/notIsShow",method = RequestMethod.GET)
    public AjaxResult notIsShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        gonggaoDaoMapper.notIsShow(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/sendMsg",method = RequestMethod.GET)
    public AjaxResult sendMsg(@RequestParam(required=false, value = "id") String id) throws Exception {
        AjaxResult ajaxResult = new AjaxResult();
        Gonggao gonggao = gonggaoDaoMapper.getById(id);
        if(gonggao.getIsShow() == 1){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("公告未发布，不能发送消息！");
            return ajaxResult;
        }
        String proNum = gonggao.getProNum();
        // 根据项目号查询公众号
        WechatPub wechatPub = wechatPubDaoMapper.getByProNum(proNum);
        String accessToken = JSONUtil.parseObj(HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wechatPub.getAppId()+"&secret=" + wechatPub.getAppSecret())).get("access_token").toString();
        sendTempMsg(proNum,accessToken,wechatPub.getProName());
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    public void sendTempMsg(String proNum, String accessToken, String proName) throws Exception {
        // 查询小程序配置
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP_EJ_ZHSQ);
        Miniprogram miniprogram = new Miniprogram(constantConfig.getAppId(), "pages/main/main");
        // 查询消息模板
        MsgTemplate msgTemplate = msgTemplateDaoMapper.getByProNumAndKey(proNum, Constant.GONGGAO_TEMPLATE);
        if(msgTemplate != null) {
            // 根据项目号查询已入住客户
            CstInto cstInto = new CstInto();
            cstInto.setProjectNum(proNum);
            cstInto.setIntoStatus(Constant.INTO_STATUS_Y);
            List<CstInto> cstIntoList = cstIntoDaoMapper.getList(cstInto);
            // 根据入住用户unionId获取公众号openId
            if (!cstIntoList.isEmpty()) {
                List<String> unionIdList = new ArrayList<>();
                for (CstInto cst : cstIntoList) {
                    if (StringUtils.isNotBlank(cst.getUnionId()))
                        unionIdList.add(cst.getUnionId());
                }
                // 根据项目号，unionids集合查询公众号用户
                if (!unionIdList.isEmpty()) {
                    WechatPubUser wechatPubUser = new WechatPubUser();
                    wechatPubUser.setProNum(proNum);
                    wechatPubUser.setUnionIdList(unionIdList);
                    List<WechatPubUser> wechatPubUserList = wechatPubUserDaoMapper.getList(wechatPubUser);
                    if (!wechatPubUserList.isEmpty()) {
                        for (WechatPubUser wp : wechatPubUserList) {
                            TempleMessage modelMessage = new TempleMessage();
                            modelMessage.setTouser(wp.getOpenid());
                            modelMessage.setTemplate_id(msgTemplate.getTemplateId());
                            modelMessage.setData("dataParam");
                            modelMessage.setMiniprogram(miniprogram);
                            String jsonMenu = JsonUtils.writeEntiry2JSON(modelMessage);
                            String data = msgTemplate.getTemplateData();
                            data = data.replace("param1", proName).replace("param2", DateUtils.strYmdHms()).replace("param3", "新公告通知");
                            jsonMenu = jsonMenu.replace("\"dataParam\"", data);
                            newSendModel(jsonMenu, accessToken);
                        }
                    }
                }
            }
        }
    }


    public static int newSendModel(String jsonMenu, String accessToken) {
        int result = -1;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", accessToken);
        jsonMenu = jsonMenu.replace("date", "Date").replace("money", "Money");
        //}
        JSONObject jsonObject = HttpClientUtil.sendPost(url, jsonMenu);
        if (null != jsonObject) {
            int errorCode = ((Integer) jsonObject.get("errcode"));
            if (0 != errorCode) {
                result = errorCode;
            } else {
                result = 0;
            }
        }

        return result;
    }

    /**
     * 获取公众号token
     */
    public String getWxPubToken(String appid, String secret) {
        String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" + "&appid=" + appid + "&secret=" + secret;
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(access_token_url));
        String access_token = jsonObject.getString("access_token");
        logger.info("------------------获取token:"+access_token+"--------------------");
        return access_token;
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            //SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            System.out.println("HTTP请求返回：" + buffer.toString());
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
        } catch (Exception e) {
        }
        return jsonObject;
    }

    /**
     * 编辑器保存
     * @param gonggao
     * @return
     */
    @RequestMapping(value = "/saveContent",method = RequestMethod.POST)
    public AjaxResult saveContent(@RequestBody Gonggao gonggao){
        AjaxResult ajaxResult = new AjaxResult();
        String gonggaoId = gonggao.getId();
        if(StringUtils.isBlank(gonggaoId)){
            String id = TimestampGenerator.generateSerialNumber();
            gonggao.setId(id);
            String filePath = saveGongGaoContent(id, gonggao.getContent());
            gonggao.setFilePath(filePath);
            // 来源：1-公众号 2-编辑器
            gonggao.setSource(2);
            // 默认1,不显示  0-显示
            gonggao.setIsShow(1);
            gonggao.setUpdateTime(new Date());
            gonggao.setCreateTime(new Date());
            gonggao.setDeleteFlag(0);
            gonggaoDaoMapper.save(gonggao);
        }else {
            String filePath = saveGongGaoContent(gonggaoId, gonggao.getContent());
            gonggao.setFilePath(filePath);
            gonggao.setUpdateTime(new Date());
            gonggaoDaoMapper.update(gonggao);
        }

        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    public String saveGongGaoContent(String no, String content) {
        String path = "";
        //目录不存在则直接创建
        File filePath = new File(uploadPath + "/gonggao");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        //创建年月日文件夹
        //File ymdFile = new File(uploadPath + "/gonggao" + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()));
        //目录不存在则直接创建
//        if (!ymdFile.exists()) {
//            ymdFile.mkdirs();
//        }
        //在年月日文件夹下面创建txt文本存储图片base64码
        //File txtFile = new File(ymdFile.getPath() + "/" + no + ".html");
        File txtFile = new File(uploadPath + "/gonggao" + "/" + no + ".html");
        if (!txtFile.exists()) {
            try {
                txtFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = txtFile.getPath();
        FileWriter writer = null;
        try {
            writer = new FileWriter(txtFile);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    @PostMapping(value = "/view")
    public void view(HttpServletResponse response, String fileId) throws ServletException, IOException {
        Gonggao gonggao = gonggaoDaoMapper.getById(fileId);
//        try {
//            response.setContentType("application/vnd.ms-excel");
//            //1.输入流，通过输入流读取上传的文件
//            FileInputStream fileInputStream = new FileInputStream(new File(sumFile.getFileUrl()));
//            //2.输出流，通过输出流将文件写回了浏览器，在浏览器展示
//            ServletOutputStream outputStream = response.getOutputStream();
//            //4.将文件读取进bytes数组，通过输出流写回浏览器
////            int len = 0;
////            byte[] bytes = new byte[1024];
////            while ((len = fileInputStream.read(bytes)) != -1) {
////                outputStream.write(bytes, 0, len);
////            }
//            IOUtils.copy(fileInputStream,outputStream);
//            outputStream.flush();
//            //5.关闭资源
//            outputStream.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        OutputStream os = null;
        InputStream is = null;
        try {
            InputStream in = new FileInputStream(new File(gonggao.getFilePath()));
            // 取得输出流
            os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment");
            //复制
            IOUtils.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.info("文件读取失败，" + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            }
        }
    }


    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public AjaxResult select(@RequestParam(required=false, value = "id") String id) throws IOException {
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        Gonggao gonggao = gonggaoDaoMapper.getById(id);
        Path path = Paths.get(gonggao.getFilePath());
        List<String> lines = Files.readAllLines(path);
        String content = lines.toString();
        content = content.replace("[","");
        content = content.replace("]","");
        gonggao.setContent(content);
        map.put("gonggao",gonggao);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    public static void main(String[] args) throws IOException {
            FTPClient ftpClient = new FTPClient();
            // 指定FTP服务器的IP地址和端口
            String server = "192.168.79.129";
            int port = 21; // FTP的默认端口是21，如果FTP服务器使用的是非标准端口，则需要更改此值

            // 连接到FTP服务器
            ftpClient.connect(server, port);

            // 登录
            ftpClient.login("sam", "Aa111111");

            // 进入被动模式
            ftpClient.enterLocalPassiveMode();

            // 设置为二进制文件类型
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            // 上传文件的路径
            String remoteFilePath = "/home/sam/hgj_docker";
            String localFilePath = "D:\\var\\upload\\file\\gonggao\\20240717\\20240717135826g46ctf.html";

            // 使用InputStream来上传文件
            try (FileInputStream input = new FileInputStream(localFilePath)) {
                ftpClient.storeFile(remoteFilePath, input);
            }

            // 断开连接
            ftpClient.logout();

            System.out.println("File uploaded successfully.");


    }

}
