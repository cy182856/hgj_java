package com.ej.hgj.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.dao.user.UserRoleDaoMapper;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.user.UserRole;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.RandomNumberGenerator;
import com.ej.hgj.utils.TokenUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class LoginController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private static final Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(100)       // 最大缓存数量
            .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
            .build();

    @Autowired
    private UserService userService;

    @Autowired
    private UserDaoMapper userDaoMapper;

    @Autowired
    private UserRoleDaoMapper userRoleDaoMapper;

//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public AjaxResult login(@RequestBody Map<String,String> data, HttpServletRequest request, HttpServletResponse response){
//        AjaxResult ajaxResult = new AjaxResult();
//        String userName = data.get("username");
//        String password = data.get("password");
//        //校验用户名和密码
//        User user = userService.queryUser(userName, password);
//        if (user == null) {
//            ajaxResult.setCode(400);
//            ajaxResult.setMessage("用户名或密码错误");
//        }else {
//            //生成token
//            String token = TokenUtils.getToken(user.getStaffId(), user.getPassword());
//            //将用户存入session,用userid作为键
//            request.getSession().setAttribute(user.getStaffId(),user);
//            HashMap tokenMap = new HashMap();
//            tokenMap.put("token",token);
//            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//            ajaxResult.setMessage("登陆成功");
//            ajaxResult.setData(tokenMap);
//        }
//        return ajaxResult;
//    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public AjaxResult serviceLogin(@RequestBody Map<String,String> data, HttpServletRequest request){
        AjaxResult ajaxResult = new AjaxResult();
        String userName = data.get("username");
        String password = data.get("password");
        if(StringUtils.isBlank(userName)){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("用户名不能为空");
            return ajaxResult;
        }
        // 校验用户是否存在
        List<User> queryUserByMobile = userDaoMapper.queryUserByMobile(userName);
        if(queryUserByMobile.isEmpty()){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("无访问权限");
            return ajaxResult;
        }
        // 开发者密码登录
        List<UserRole> userRoleByMobile = userRoleDaoMapper.getUserRoleByMobile(userName);
        if(!userRoleByMobile.isEmpty()) {
            //校验用户名和密码
            List<User> userList = userDaoMapper.queryUserList(userName, password);
            if (userList.isEmpty()) {
                ajaxResult.setCode(400);
                ajaxResult.setMessage("密码错误");
                return ajaxResult;
            }else {
                //生成token
                String token = TokenUtils.getToken(userList.get(0).getUserId(), userList.get(0).getPassword());
                //将用户存入session,用userid作为键
                request.getSession().setAttribute(userList.get(0).getUserId(),userList.get(0));
                HashMap tokenMap = new HashMap();
                tokenMap.put("token",token);
                ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
                ajaxResult.setMessage("登陆成功");
                ajaxResult.setData(tokenMap);
            }
        // 手机号、钉钉验证码登录
        }else {
            // 获取缓存数据，通过key获取值
            String value = cache.getIfPresent(userName);
            if (!password.equals(value)) {
                ajaxResult.setCode(400);
                ajaxResult.setMessage("验证码错误");
                return ajaxResult;
            }else {
                //生成token
                String token = TokenUtils.getToken(queryUserByMobile.get(0).getUserId(), queryUserByMobile.get(0).getPassword());
                //将用户存入session,用userid作为键
                request.getSession().setAttribute(queryUserByMobile.get(0).getUserId(),queryUserByMobile.get(0));
                HashMap tokenMap = new HashMap();
                tokenMap.put("token",token);
                ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
                ajaxResult.setMessage("登陆成功");
                ajaxResult.setData(tokenMap);
            }
        }
        logger.info("---------" + userName + "--------登录系统------");
        return ajaxResult;
    }

//    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
//    public AjaxResult getInfo(@RequestParam("token") String token){
//        AjaxResult ajaxResult = new AjaxResult();
//        //解密获取
//        String userId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
//        //根据userid查询数据库
//        User user = userService.getById(userId);
//        if(user == null){
//            ajaxResult.setCode(400);
//            ajaxResult.setMessage("未获取到用户信息,请重新登陆");
//        }else{
//            HashMap hashMap = new HashMap();
//            hashMap.put("avatar", "any");
//            hashMap.put("introduction", "介绍");
//            hashMap.put("roles", new String[]{"any"});
//            hashMap.put("name", user.getUserName());
//            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//            ajaxResult.setMessage("已登录");
//            ajaxResult.setData(hashMap);
//        }
//        return ajaxResult;
//    }

    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
    public AjaxResult getInfo(@RequestParam("token") String token){
        AjaxResult ajaxResult = new AjaxResult();
        //解密获取
        String userId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
        //根据userid查询数据库
        List<User> userList = userDaoMapper.queryUserByUserId(userId);
        if(userList.isEmpty()){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("未获取到用户信息,请重新登陆");
        }else{
            HashMap hashMap = new HashMap();
            hashMap.put("avatar", "any");
            hashMap.put("introduction", "介绍");
            hashMap.put("roles", new String[]{"any"});
            hashMap.put("name", userList.get(0).getUserName());
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage("已登录");
            ajaxResult.setData(hashMap);
        }
        return ajaxResult;
    }

    /**
     * 发送钉钉验证码
     * @param userName
     * @return
     */
    @RequestMapping(value = "/sendCode",method = RequestMethod.GET)
    public AjaxResult sendCode(@RequestParam(required=false, value = "userName") String userName,HttpServletResponse response){
        AjaxResult ajaxResult = new AjaxResult();
        if(StringUtils.isBlank(userName)){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("手机号不能为空");
            return ajaxResult;
        }
        // 校验用户是否存在
        List<User> queryUserByMobile = userDaoMapper.queryUserByMobile(userName);
        if(queryUserByMobile.isEmpty()){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("无访问权限");
            return ajaxResult;
        }
        // 获取缓存数据，通过key获取值
        String value = cache.getIfPresent(userName);
        if(StringUtils.isBlank(value)){
            // 6位随机数
            Set<Integer> randomNum = RandomNumberGenerator.generateRandomNumbers(100000, 999999, 1);
            String randNum = "";
            for(Integer integer : randomNum){
                randNum = integer.toString();
            }
            // 发送钉钉验证码
            JSONObject jsonObject = sendNotify(userName, randNum);
            String errCode = jsonObject.get("errcode").toString();
            String errMsg = jsonObject.get("errmsg").toString();
            if(!"0".equals(errCode)){
                ajaxResult.setCode(400);
                ajaxResult.setMessage(errMsg);
                return ajaxResult;
            }
            // 写入缓存
            cache.put(userName,randNum);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage("发送成功");
        logger.info("---------" + userName + "--------发送验证码成功------");
        return ajaxResult;
    }

    /**
     * 钉钉发送工作通知
     */
    public JSONObject sendNotify(String mobile, String randNum){

//        String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;
//        String msg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"验证码：" + randNum + " 时间：" + DateUtils.strYmdHms() + "\"}}";
//        logger.info("----生成验证码-----" + randNum + "--------------msg--------" + msg);
//        String userId = getByMobile(accessToken, mobile);
//        String jsonData = "{  \"agent_id\": " + "1900735881" + ",  \"userid_list\": " +
//                userId + ",  \"to_all_user\": " + false + ",  \"msg\": " + msg + "}";
//        JSONObject resultJson = HttpClientUtil.sendPostDing(url, jsonData, accessToken);
        try {
            String accessToken = getAccessToken();
            String userId = getByMobile(accessToken, mobile);
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(1900735881L);
            req.setUseridList(userId);
            String msg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"【智慧管家】您的验证码为：" + randNum + "，有效期10分钟。\"}}";
            req.setMsg(msg);
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, accessToken);
            JSONObject resultJson = JSONObject.parseObject(rsp.getBody());
            logger.info("----生成验证码：" + randNum + "--msg：" + msg + "--返回数据：" + resultJson);
            return resultJson;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取token
     * @return
     */
    public String getAccessToken(){
        String access_token_url = "https://oapi.dingtalk.com/gettoken?appkey="+"dingidwnjiltivszhxmz"+"&appsecret="+"Q_dho9NiJ9meriZCstSuSbG7tFk9l8JgK9kAl2NNl8qXv_r2tj6qNxSmZXH-wWwz";
        JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.doGet(access_token_url));
        String access_token = jsonObject.getString("access_token");
        logger.info("------------------获取token:"+access_token+"--------------------");
        return access_token;
    }

    /**
     * 通过手机号获取userId
     * @return
     */
    public String getByMobile( String accessToken, String mobile){
        String url = "https://oapi.dingtalk.com/topapi/v2/user/getbymobile?access_token=" + accessToken;
        String jsonData = "{  \"mobile\": " + mobile + "}";
        JSONObject resultJson = HttpClientUtil.sendPost(url, jsonData);
        String result = resultJson.get("result").toString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        String userId= jsonObject.getString("userid");
        logger.info("------------------获取userId:"+userId+"--------------------");
        return userId;
    }

    public static void main(String[] args) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(1900735881L);
            req.setUseridList("0223222418838382");
            String msg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"【智慧管家】您的验证码为：123456，有效期10分钟。\"}}";
            req.setMsg(msg);
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, "5461fd29bb7339ad813017c9c81535a7");
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
