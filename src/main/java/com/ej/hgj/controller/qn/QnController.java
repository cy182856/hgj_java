package com.ej.hgj.controller.qn;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.identity.IdentityDaoMapper;
import com.ej.hgj.dao.qn.QnDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubMenuDaoMapper;
import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.entity.identity.Identity;
import com.ej.hgj.entity.qn.Qn;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.wechat.WechatPubMenu;
import com.ej.hgj.service.qn.QnService;
import com.ej.hgj.sy.dao.house.SyHouseDaoMapper;
import com.ej.hgj.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/qn")
public class QnController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private QnService qnService;

    @Autowired
    private QnDaoMapper qnDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private WechatPubMenuDaoMapper wechatPubMenuDaoMapper;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private SyHouseDaoMapper syHouseDaoMapper;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private IdentityDaoMapper identityDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           Qn qn){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<Qn> list = qnDaoMapper.getList(qn);
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<Qn> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Qn> entityPageInfo = new PageInfo<>();
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

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public AjaxResult save(HttpServletRequest request, @RequestBody Qn qn){
        AjaxResult ajaxResult = new AjaxResult();
        qnService.save(qn,TokenUtils.getUserId(request));
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        qnDaoMapper.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 小程序发布
     * @param id
     * @return
     */
    @RequestMapping(value = "/miniIsShow",method = RequestMethod.GET)
    public AjaxResult miniIsShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        // 校验发布的问卷不能超过10个,根据项目号
        Qn qn = qnDaoMapper.getById(id);
        Qn qnPram = new Qn();
        qnPram.setProNum(qn.getProNum());
        qnPram.setMiniIsShow(1);
        List<Qn> list = qnDaoMapper.getList(qnPram);
        ConstantConfig config = constantConfDaoMapper.getByKey(Constant.QN_SHOW_SIZE);
        Integer size = Integer.valueOf(config.getConfigValue());
        if(!list.isEmpty() && list.size() >= size){
            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
            ajaxResult.setMessage("已发布问卷不能超过"+size+"个！");
        }else {
            qnDaoMapper.miniIsShow(id);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        }
        return ajaxResult;
    }

    @RequestMapping(value = "/notMiniIsShow",method = RequestMethod.GET)
    public AjaxResult notMiniIsShow(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        qnDaoMapper.notMiniIsShow(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 公众号发布
     * @param id
     * @return
     */
//    @RequestMapping(value = "/pubMenuIsShow",method = RequestMethod.GET)
//    public AjaxResult pubMenuIsShow(HttpServletRequest request, @RequestParam(required=false, value = "id") String id){
//        AjaxResult ajaxResult = new AjaxResult();
//        Qn qn = qnDaoMapper.getById(id);
//        // 校验发布的问卷不能超过5个,根据项目号
//        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.WECHAT_PUB_MENU_PARENT_ID);
//        WechatPubMenu wechatPubMenu = new WechatPubMenu();
//        wechatPubMenu.setParentId(Integer.valueOf(constantConfig.getConfigValue()));
//        wechatPubMenu.setProNum(qn.getProNum());
//        List<WechatPubMenu> list = wechatPubMenuDaoMapper.getList(wechatPubMenu);
//        if(!list.isEmpty() && list.size() >= 5){
//            ajaxResult.setCode(Constant.FAIL_RESULT_CODE);
//            ajaxResult.setMessage("已发布问卷不能超过5个！");
//        }else {
//            qnService.pubMenuIsShow(id, TokenUtils.getUserId(request));
//            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//            ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        }
//        return ajaxResult;
//    }

//    @RequestMapping(value = "/notPubMenuIsShow",method = RequestMethod.GET)
//    public AjaxResult notPubMenuIsShow(HttpServletRequest request,@RequestParam(required=false, value = "id") String id){
//        AjaxResult ajaxResult = new AjaxResult();
//        qnService.notPubMenuIsShow(id, TokenUtils.getUserId(request));
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        return ajaxResult;
//    }

    // 下载文件
    @PostMapping("/file/download")
    public void download(HttpServletResponse response, String id) throws IOException, ParseException {
        // 写入文件
        Qn qn = qnDaoMapper.getById(id);
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.JINSHUJU_API_KEY);
        inputFile(qn.getProNum(), qn.getFormToken(), constantConfig.getAppId(), constantConfig.getAppSecret());

        // 读取文件并下载
        OutputStream os = null;
        InputStream is = null;
        try {
            InputStream in = new FileInputStream(new File(uploadPath + "/qn/qn.cvs"));
            // 取得输出流
            os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment");
            //复制
            IOUtils.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.info("下载文件失败，" + e.getMessage());
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

    // 将导出的文件写入磁盘
    public void inputFile(String proNum, String formToken, String apiKey, String apiSecret) throws IOException, ParseException {
        String url = "https://jinshuju.net/api/v1/forms/" + formToken + "/entries";
        String resultFirst = run(apiKey, apiSecret, url);
        JSONObject jsonDataFirst = JSONObject.parseObject(resultFirst);
        String dataFirst = jsonDataFirst.getString("data");
        List<Map<String,Object>> mapListAll = new ArrayList<>();
        // 每页条数
        int pageSize = 50;
        // 总条数
        int total = Integer.valueOf(jsonDataFirst.getString("total"));
        // 总页数
        int totalPages = (total + pageSize - 1) / pageSize;
        // 分页参数，本次请求分页ID，可用于请求下一页数据
        String next = null;
        for(int i = 1; i <= totalPages; i++){
            if(i == 1){
                next = jsonDataFirst.getString("next");
                mapListAll = JSONArray.parseObject(dataFirst,List.class);
            }else {
                if(StringUtils.isNotBlank(next)){
                    url = "https://jinshuju.net/api/v1/forms/" + formToken + "/entries?next="+next;
                }
                String resultData = run(apiKey, apiSecret, url);
                JSONObject jsonData = JSONObject.parseObject(resultData);
                String data = jsonData.getString("data");
                next = jsonData.getString("next");
                List<Map<String,Object>> mapList = JSONArray.parseObject(data, List.class);
                mapListAll.addAll(mapList);
            }
        }
        // 查询所有身份
        List<Identity> identityList = identityDaoMapper.getList(new Identity());
        // 根据项目号查询所有已注册客户
        //List<CstInto> cstIntoList = cstIntoDaoMapper.getByProNumList(proNum); //根据公众号unionId查询openId
        List<CstInto> cstIntoList = cstIntoDaoMapper.getListByProNum(proNum);// 查询入住表openId
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getDefault()); // 设置你想要的时区
        for (Map<String,Object> map : mapListAll){
            //String openId = map.get("x_field_weixin_openid")+""; // 系统字段公众号openId
            String openId = map.get("wx_open_id")+"";// 隐藏字段，小程序openId
            // 日期格式转换
            String isoDateString = map.get("updated_at").toString();
            Date date = isoFormat.parse(isoDateString);
            String formattedDate = outputFormat.format(date);
            // 更新时间格式化
            map.put("updated_at",formattedDate);
            // 项目
            map.put("project_name","");
            // 填表人
            map.put("user_name","");
            // 手机号
            map.put("phone","");
            // 客户名称
            map.put("cst_name","");
            // 房间号
            map.put("room_num","");
            // 身份
            map.put("identity","");
            if(StringUtils.isNotBlank(openId)){
                List<CstInto> cstIntoListFilter = cstIntoList.stream().filter(cstInto -> openId.equals(cstInto.getWxOpenId())).collect(Collectors.toList());
                if(!cstIntoListFilter.isEmpty()){
                    CstInto cstInto = cstIntoListFilter.get(0);
                    map.put("project_name",cstInto.getProjectName());
                    map.put("user_name",cstInto.getUserName());
                    map.put("phone",cstInto.getPhone());
                    map.put("cst_name",cstInto.getCstName());
                    Integer intoRole = cstInto.getIntoRole();
                    // 租户员工、租客、同住人查询所要入住的房间
                    List<String> houseList = new ArrayList<>();
                    if(intoRole == 1 || intoRole == 3 || intoRole == 4){
                        List<HgjHouse> hgjHouseList = hgjHouseDaoMapper.getByCstIntoId(cstInto.getId());
                        if(!hgjHouseList.isEmpty()){
                            for(HgjHouse hgjHouse : hgjHouseList){
                                houseList.add(hgjHouse.getResName());
                            }
                        }
                    }else {
                        // 租户、产权人查询该客户所有房间
                        HgjHouse hgjHouse = new HgjHouse();
                        hgjHouse.setCstCode(cstInto.getCstCode());
                        List<HgjHouse> list = syHouseDaoMapper.getListByCstCode(hgjHouse);
                        if(!list.isEmpty()){
                            for(HgjHouse house : list){
                                houseList.add(house.getResName());
                            }
                        }
                    }
                    if(!houseList.isEmpty()){
                        String houses = houseList.stream().collect(Collectors.joining("|"));
                        map.put("room_num", houses);
                    }

                    List<Identity> identitiesFilter = identityList.stream().filter(identity -> identity.getCode() == intoRole).collect(Collectors.toList());
                    map.put("identity",identitiesFilter.get(0).getName());

                }
            }
        }
        // 获取表单结构
        String urlForm = "https://jinshuju.net/api/v1/forms/" + formToken;
        String resultForm = run(apiKey, apiSecret, urlForm);
        JSONObject jsonDataForm = JSONObject.parseObject(resultForm);
        String fields = jsonDataForm.getString("fields");
        List<Map<String,Object>> fieldList = JSONArray.parseObject(fields, List.class);
        // 从表单结构获取标题
        Map<String,Object> mapTitle = new LinkedHashMap<>();
        //mapTitle.put("serial_number","序号");
        mapTitle.put("project_name","项目");
        mapTitle.put("user_name","填表人");
        mapTitle.put("cst_name","客户名称");
        mapTitle.put("phone","手机号");
        mapTitle.put("room_num","房间号");
        mapTitle.put("identity","身份");

        for(Map<String,Object> mapField : fieldList){
            mapField.forEach((key,value) ->{
                String field = mapField.get(key)+"";
                JSONObject jsonField = JSONObject.parseObject(field);
                String label = jsonField.getString("label");
                // 隐藏字段微信号 wx_open_id 不放入标题
                String api_code_alias = jsonField.getString("api_code_alias");
                if(!"wx_open_id".equals(api_code_alias)){
                    mapTitle.put(key, label);
                }
            });
        }
        mapTitle.put("updated_at","更新时间");
        List<Map<String,Object>> mapFilterAll = new ArrayList<>();
        for (Map<String,Object> mapList : mapListAll){
            Map<String,Object> mapFilter = new LinkedHashMap<>();
            mapTitle.forEach((key1,value1) ->{
                mapList.forEach((key2,value2) ->{
                    if(key1.equals(key2)){
                        mapFilter.put(key2,value2);
                    }
                });
                // 表结构中的key在填写数据中未找到，填充一个空key
                if(!mapFilter.containsKey(key1)){
                    mapFilter.put(key1,"");
                }
            });
            mapFilterAll.add(mapFilter);
        }
        mapFilterAll.add(0,mapTitle);

        // 去掉隐藏字段wx_open_id
        for (Map<String,Object> map : mapListAll){
            map.remove("wx_open_id");
        }

        //目录不存在则直接创建
        File filePath = new File(uploadPath + "/qn");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        // 创建一个CSV写入器
        CSVWriter csvWriter = new CSVWriter(new FileWriter(uploadPath + "/qn" + "/qn.cvs"));
        for (Map<String, Object> map : mapFilterAll) {
            String[] csvData = new String[map.size()]; // 创建一个与map大小相等的数组
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                csvData[i] = entry.getValue()+""; // 将map中的值存储到数组中
                i++;
            }
            csvWriter.writeNext(csvData); // 将CSV数据写入文件
        }
        // 刷新并关闭CSV写入器
        csvWriter.flush(); // 确保所有数据都被写入文件
        csvWriter.close(); // 关闭写入器，‌释放资源
    }

    public String run(String apiKey, String apiSecret, String url) throws IOException {
        String authHeaderPayload = getAuthHeaderPayload(apiKey, apiSecret);

        BufferedReader httpResponseReader = null;
        try {
            URL apiEndpointUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) apiEndpointUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Authorization", authHeaderPayload);

            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String lineRead;
            while ((lineRead = httpResponseReader.readLine()) != null) {
                //System.out.println(lineRead);
                return lineRead;
            }

        } finally {
            if (httpResponseReader != null) {
                try {
                    httpResponseReader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    private String getAuthHeaderPayload(String apiKey, String apiSecret) {
        String credentials = apiKey + ":" + apiSecret;
        String base64EncodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + base64EncodedCredentials;
    }

    public static void main(String[] args) throws IOException {
        //String formToken = "KXAYWl";
        String formToken = "Uf4H2e";
        String url = "https://jinshuju.net/api/v1/forms/" + formToken + "/entries";
        //String apiKey = "ntCYZjR_I1d2RdOD2738kQ";
        String apiKey = "7-yTLX0Hiu5blrUthwaa-w";
        //String apiSecret = "P-sRUiU3TNSTnsBJvSBbIw";
        String apiSecret = "LStRoFvO2ImyZN45IK0onQ";
        QnController qnController = new QnController();

        String resultFirst = qnController.run(apiKey, apiSecret,url);
        JSONObject jsonDataFirst = JSONObject.parseObject(resultFirst);
        String dataFirst = jsonDataFirst.getString("data");
        List<Map<String,Object>> mapListAll = null;
        // 每页条数
        int pageSize = 50;
        // 总条数
        int total = Integer.valueOf(jsonDataFirst.getString("total"));
        // 总页数
        int totalPages = (total + pageSize - 1) / pageSize;
        // 分页参数，本次请求分页ID，可用于请求下一页数据
        String next = null;
        for(int i = 1; i <= totalPages; i++){
            if(i == 1){
                next = jsonDataFirst.getString("next");
                mapListAll = JSONArray.parseObject(dataFirst,List.class);
            }else {
                if(StringUtils.isNotBlank(next)){
                    url = "https://jinshuju.net/api/v1/forms/" + formToken + "/entries?next="+next;
                }
                String resultData = qnController.run(apiKey, apiSecret, url);
                JSONObject jsonData = JSONObject.parseObject(resultData);
                String data = jsonData.getString("data");
                next = jsonData.getString("next");
                List<Map<String,Object>> mapList = JSONArray.parseObject(data, List.class);
                mapListAll.addAll(mapList);
            }
        }

        for (Map<String,Object> map : mapListAll){
            map.remove("info_region");
            map.remove("info_os");
            map.remove("info_remote_ip");
            map.remove("token");
            map.remove("info_platform");
            map.remove("referral_link");
            map.remove("x_field_1");
            map.remove("color_mark");
            map.remove("x_field_weixin_nickname");
            map.remove("x_field_weixin_unionid");
            map.remove("x_field_weixin_headimgurl");
            map.remove("creator_name");
            map.remove("referred_from");
            map.remove("referred_from_associated_serial_number");
            map.remove("referral_users_count");
            map.remove("referral_poster_url");
            map.remove("info_filling_duration");
            map.remove("info_browser");
        }

        // 获取表单结构
        String urlForm = "https://jinshuju.net/api/v1/forms/" + formToken;
        String resultForm = qnController.run(apiKey, apiSecret, urlForm);
        JSONObject jsonDataForm = JSONObject.parseObject(resultForm);
        String fields = jsonDataForm.getString("fields");
        List<Map<String,Object>> fieldList = JSONArray.parseObject(fields, List.class);
        // 复制第一条数据更新为标题
        Map<String,Object> mapListFirst = mapListAll.get(0);
        Map<String,Object> mapTitle = new LinkedHashMap<>();
        mapListFirst.forEach((key,value) ->{
            mapTitle.put(key,value);
        });
        mapTitle.forEach((key,value) ->{
            for(Map<String,Object> map : fieldList){
                if("serial_number".equals(key)){
                    mapTitle.put(key,"序号");
                }
                if("created_at".equals(key)){
                    mapTitle.put(key,"创建时间");
                }
                if("updated_at".equals(key)){
                    mapTitle.put(key,"更新时间");
                }
                if("x_field_weixin_openid".equals(key)){
                    mapTitle.put(key,"wxOpenId");
                }
                if(StringUtils.isNotBlank(map.get(key)+"")){
                    String field = map.get(key)+"";
                    if(StringUtils.isNotBlank(field) && !"null".equals(field)){
                        JSONObject jsonField = JSONObject.parseObject(field);
                        String label = jsonField.getString("label");
                        mapTitle.put(key,label);
                    }

                }
            }
        });
        mapListAll.add(0,mapTitle);
        // 创建一个CSV写入器
        CSVWriter csvWriter = new CSVWriter(new FileWriter("d:/var/upload/qn/output.cvs"));
        for (Map<String, Object> map : mapListAll) {
            String[] csvData = new String[map.size()]; // 创建一个与map大小相等的数组
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                csvData[i] = entry.getValue()+""; // 将map中的值存储到数组中
                i++;
            }
            csvWriter.writeNext(csvData); // 将CSV数据写入文件
        }

        for (Map<String,Object> map : mapListAll){
            System.out.println(map);
        }
        // 刷新并关闭CSV写入器
        csvWriter.flush(); // 确保所有数据都被写入文件
        csvWriter.close(); // 关闭写入器，‌释放资源

    }
}
