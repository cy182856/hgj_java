package com.ej.hgj.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.build.BuildDaoMapper;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.config.ProConfDaoMapper;
import com.ej.hgj.dao.corp.CorpDaoMapper;
import com.ej.hgj.dao.user.UserDutyPhoneDaoMapper;
import com.ej.hgj.entity.build.Build;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.config.ProConfig;
import com.ej.hgj.entity.corp.Corp;
import com.ej.hgj.entity.user.Department;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserDutyPhone;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.*;
import com.ej.hgj.utils.wechat.MyX509TrustManager;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private BuildDaoMapper buildDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private UserDutyPhoneDaoMapper userDutyPhoneDaoMapper;

    @Autowired
    private CorpDaoMapper corpDaoMapper;

    @Autowired
    private ProConfDaoMapper proConfDaoMapper;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           User user){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<User> list = userService.getList(user);
        //查询所有的楼栋绑定记录
        List<Build> buildList = buildDaoMapper.getListAll(new Build());
        // 查询所有值班电话
        List<UserDutyPhone> userDutyPhoneList = userDutyPhoneDaoMapper.getList(new UserDutyPhone());
        for(User user1 : list){
            List<Build> buildListFilter = buildList.stream().filter(build1 -> user1.getMobile().equals(build1.getMobile()) && build1.getOrgId().equals(user1.getProjectNum())).collect(Collectors.toList());
            // 去重复
            buildListFilter = buildListFilter.stream().collect(
                        Collectors.collectingAndThen(Collectors.toCollection(
                                () -> new TreeSet<>(Comparator.comparing(Build::getBudId))), ArrayList::new));
            // 排序
            buildListFilter = buildListFilter.stream().sorted(Comparator.comparing(Build :: getBudNameUnit)).collect(Collectors.toList());
            user1.setBuildList(buildListFilter);

            List<UserDutyPhone> userDutyPhoneListFilter = userDutyPhoneList.stream().filter(dutyPhone -> dutyPhone.getMobile().equals(user1.getMobile())).collect(Collectors.toList());
            if(!userDutyPhoneListFilter.isEmpty()){
                user1.setPhone(userDutyPhoneListFilter.get(0).getPhone());
            }
        }
        //logger.info("list:"+ JSON.toJSONString(list));
        PageInfo<User> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<User> entityPageInfo = new PageInfo<>();
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
        // 多数据源测试
        return ajaxResult;
    }

    @RequestMapping(value = "/deptSelect",method = RequestMethod.GET)
    public AjaxResult deptSelect(){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<User> list = userService.getDeptList(new User());
        //logger.info("list:"+ JSON.toJSONString(list));
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        //logger.info("responseMsg:"+ JSON.toJSONString(ajaxResult));
        return ajaxResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public AjaxResult save(HttpServletRequest request, @RequestBody User user){
        return userService.updateRolePro(new AjaxResult(),user,TokenUtils.getUserId(request));
    }

//    @RequestMapping(value = "/update",method = RequestMethod.POST)
//    public AjaxResult save(HttpServletRequest request, @RequestBody User user){
//        return userService.updateRolePro(new AjaxResult(),user,TokenUtils.getUserId(request));
//    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public AjaxResult delete(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        userService.delete(id);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    public String getAccessToken(String cropId, String addressBookSecret) {
        String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+cropId+"&corpsecret="+addressBookSecret;
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(access_token_url));
        String access_token = jsonObject.getString("access_token");
        logger.info("------------------获取token:"+access_token+"--------------------");
        return access_token;
    }

    //同步通讯录
//    @RequestMapping(value = "/sync",method = RequestMethod.GET)
//    public AjaxResult sync() {
//        logger.info("---------------------------同步通讯录开始--------------------");
//        AjaxResult ajaxResult = new AjaxResult();
//        //记住，先去企业微信后台管理端开启api同步权限
//        //corpid---企业微信corpid
//        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.WE_COM_APP);
//        //corpsecret  ---企业微信通讯录secret
//        ConstantConfig secret = constantConfDaoMapper.getByKey(Constant.WE_COM_ADDRESS_BOOK_SECRET);
//        // 1查询出access_Token的值
//        String access_token = getAccessToken(constantConfig.getAppId(),secret.getConfigValue());
//        // 获取部门列表信息(不填则是查询出所有的部门列表)
//        List<Department> departmentList = getDepartmentList(access_token, "");
//        logger.info("---------------------------获取部门列表--------------------" + JSON.toJSONString(departmentList));
//        // 通讯录新增
//        List<User> usersAll = new ArrayList<>();
//        // 获取已有通讯录列表
//        List<User> alreadyUserList = userService.getList(new User());
//        // 根据部门列表获取部门成员详情
//        List<User> userInfoList = getDepartmentUserDetails(departmentList,access_token);
//        // 跟据企业通讯录的用户ID查找已有通讯录，不存在就新增
//        for(User wxUserInfo : userInfoList){
//            List<User> alreadyUserFilterList = alreadyUserList.stream().filter(alreadyWxUserInfo -> alreadyWxUserInfo.getMobile().equals(wxUserInfo.getMobile())).collect(Collectors.toList());
//            if(alreadyUserFilterList.isEmpty()){
//                usersAll.add(wxUserInfo);
//            }
//        }
//        // 将其保存到数据库中
//        if (!usersAll.isEmpty()) {
//            userService.insertList(usersAll);
//        }
//        logger.info("---------------------------同步通讯录结束--------------------" + JSON.toJSONString(usersAll));
//        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
//        HashMap map = new HashMap();
//        map.put("user",usersAll);
//        ajaxResult.setData(map);
//        return ajaxResult;
//    }

    //企业微信服务商同步授权企业通讯录
    @RequestMapping(value = "/sync",method = RequestMethod.GET)
    public AjaxResult sync() {
        logger.info("---------------------------同步通讯录开始--------------------");
        AjaxResult ajaxResult = new AjaxResult();
        ConstantConfig miniProgramAppEj = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP_EJ);
        ConstantConfig suiteTicket = constantConfDaoMapper.getByKey(Constant.SUITE_TICKET);
        // 通讯录新增
        List<User> usersAll = new ArrayList<>();
        // 查询授权企业corpId
        List<Corp> corpList = corpDaoMapper.getList(new Corp());
        // 获取已有通讯录列表
        List<User> alreadyUserList = userService.getList(new User());
        for(Corp corp : corpList) {
            // 获取第三方凭证token 
            JSONObject jsonObjectSuiteAcToken = getSuiteAccessToken(miniProgramAppEj.getAppId(), miniProgramAppEj.getAppSecret(), suiteTicket.getConfigValue());
            String suiteAccessToken = jsonObjectSuiteAcToken.get("suite_access_token").toString();
            // 获取企业凭证
            JSONObject jsonObjectAccToken = getCorpToken(corp.getCorpId(), corp.getPermanentCode(), suiteAccessToken);
            String access_token = jsonObjectAccToken.getString("access_token").toString();
            // 获取部门列表信息(不填则是查询出所有的部门列表)
            List<Department> departmentList = getDepartmentList(access_token, "");
            logger.info("---------------------------获取部门列表--------------------" + JSON.toJSONString(departmentList));
            // 根据部门列表获取部门成员详情
            List<User> userInfoList = getDepartmentUserDetails(departmentList, access_token);
            // 跟据企业通讯录的用户ID查找已有通讯录，不存在就新增
            for (User wxUserInfo : userInfoList) {
                List<User> alreadyUserFilterList = alreadyUserList.stream().filter(alreadyWxUserInfo -> alreadyWxUserInfo.getMobile().equals(wxUserInfo.getMobile())).collect(Collectors.toList());
                if (alreadyUserFilterList.isEmpty()) {
                    wxUserInfo.setCorpId(corp.getCorpId());
                    usersAll.add(wxUserInfo);
                }
            }
        }
        // 将其保存到数据库中
        if (!usersAll.isEmpty()) {
            // 根据手机号去重复
            usersAll = usersAll.stream().collect(
                    Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(User::getMobile))), ArrayList::new));
            userService.insertList(usersAll);
        }
        logger.info("---------------------------同步通讯录结束--------------------" + JSON.toJSONString(usersAll));
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        HashMap map = new HashMap();
        map.put("user", usersAll);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    /**
     * 获取部门列表
     * @param accessToken
     * @param departmentId
     * @return
     */
    public List<Department> getDepartmentList(String accessToken, String departmentId) {
        // 1.获取请求的url
        String getDepartmentList_url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token="+accessToken+"&id="+departmentId;
        // 2.调用接口，发送请求，获取成员
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(getDepartmentList_url));
        List<Department> departmentList = JSONObject.parseArray(jsonObject.getString("department"),Department.class);
        return  departmentList;
    }

    /**
     * 获取部门成员详情
     * @param depts
     * @param accessToken
     * @param//是否遍历子部门的成员，一般不要遍历，除非你就只获取父级部门或者子部门为空，不然会导致数据重复
     * @return
     */
    public List<User> getDepartmentUserDetails(List<Department> depts, String accessToken) {
        List<User> users = new ArrayList<>();
        for (Department department : depts) {
            // 1.获取请求的url
            String getDepartmentUserDetails_url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token="+accessToken+"&department_id="+department.getId();
            // 2.调用接口，发送请求，获取部门成员
            JSONObject jsonObject =JSONObject.parseObject(HttpClientUtil.doGet(getDepartmentUserDetails_url));
            List<Map<String, Object>> mapListJson = (List)jsonObject.getJSONArray("userlist");
            for (Map<String, Object> map : mapListJson){
                User user = new User();
                String mobile = map.get("mobile") + "";
                if(StringUtils.isNotBlank(mobile)) {
                    user.setId(TimestampGenerator.generateSerialNumber());
                    user.setStaffId(mobile);
                    user.setUserId(map.get("userid") + "");
                    user.setUserName(map.get("name") + "");
                    user.setQrCode(map.get("qr_code") + "");
                    user.setPassword(Constant.INIT_PASSWORD);
                    user.setAlias(map.get("alias") + "");
                    user.setDeptName(department.getName());
                    user.setPosition(map.get("position") + "");
                    user.setMobile(mobile);
                    user.setBizMail(map.get("biz_mail") + "");
                    user.setAvatar(map.get("avatar") + "");
                    user.setThumbAvatar(map.get("thumb_avatar") + "");
                    user.setGender(Integer.valueOf(map.get("gender").toString()));
                    user.setStatus(Integer.valueOf(map.get("status").toString()));
                    user.setCreateTime(new Date());
                    user.setUpdateTime(new Date());
                    user.setCreateBy("");
                    user.setUpdateBy("");
                    user.setDeleteFlag(0);
                    users.add(user);
                }
            }
        }
        return users;
    }

    /**
     * 获取第三方应用凭证
     * @param suiteId 服务商应用ID
     * @return
     */
    public static JSONObject getSuiteAccessToken(String suiteId, String suiteSecret, String suiteTicket) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("suite_id", suiteId);
        params.put("suite_secret", suiteSecret);
        params.put("suite_ticket", suiteTicket);
        String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(params);
        JSONObject jsonObject = httpRequest(Constant.GET_SUITE_TOKEN, "POST", jsonStr);
        if (jsonObject != null && jsonObject.containsKey("suite_access_token")) {
            return jsonObject;
        }
        return null;
    }

    /**
     *获取企业凭证（access_token）
     * @param authCorpid 授权企业代号
     * @param permanentCode 授权企业永久授权码
     * @param suiteAccessToken
     * @return 授权方企业Token
     */
    public static JSONObject getCorpToken(String authCorpid, String permanentCode, String suiteAccessToken) {
        String jsonStr = "{\"auth_corpid\": \""+ authCorpid +"\",\"permanent_code\": \""+ permanentCode +"\"}";
        JSONObject jsonObject = httpRequest(Constant.GET_CORP_TOKEN.replace("SUITE_ACCESS_TOKEN", suiteAccessToken), "POST", jsonStr);
        if (jsonObject != null && jsonObject.containsKey("access_token")) {
            return jsonObject;
        }
        return null;
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

}
