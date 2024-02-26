package com.ej.hgj.controller.login;

import com.auth0.jwt.JWT;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.user.UserDaoMapper;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.service.user.UserService;
import com.ej.hgj.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserDaoMapper userDaoMapper;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public AjaxResult login(@RequestBody Map<String,String> data, HttpServletRequest request, HttpServletResponse response){
        AjaxResult ajaxResult = new AjaxResult();
        String userName = data.get("username");
        String password = data.get("password");
        //校验用户名和密码
        User user = userService.queryUser(userName, password);
        if (user == null) {
            ajaxResult.setCode(400);
            ajaxResult.setMessage("用户名或密码错误");
        }else {
            //生成token
            String token = TokenUtils.getToken(user.getStaffId(), user.getPassword());
            //将用户存入session,用userid作为键
            request.getSession().setAttribute(user.getStaffId(),user);
            HashMap tokenMap = new HashMap();
            tokenMap.put("token",token);
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage("登陆成功");
            ajaxResult.setData(tokenMap);
        }
        return ajaxResult;
    }

//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public AjaxResult serviceLogin(@RequestBody Map<String,String> data, HttpServletRequest request, HttpServletResponse response){
//        AjaxResult ajaxResult = new AjaxResult();
//        String userName = data.get("username");
//        String password = data.get("password");
//        //校验用户名和密码
//        List<User> userList = userDaoMapper.queryUserList(userName, password);
//        if (userList.isEmpty()) {
//            ajaxResult.setCode(400);
//            ajaxResult.setMessage("用户名或密码错误");
//        }else {
//            //生成token
//            String token = TokenUtils.getToken(userList.get(0).getStaffId(), userList.get(0).getPassword());
//            //将用户存入session,用userid作为键
//            request.getSession().setAttribute(userList.get(0).getStaffId(),userList.get(0));
//            HashMap tokenMap = new HashMap();
//            tokenMap.put("token",token);
//            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//            ajaxResult.setMessage("登陆成功");
//            ajaxResult.setData(tokenMap);
//        }
//        return ajaxResult;
//    }

    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
    public AjaxResult getInfo(@RequestParam("token") String token){
        AjaxResult ajaxResult = new AjaxResult();
        //解密获取
        String userId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
        //根据userid查询数据库
        User user = userService.getById(userId);
        if(user == null){
            ajaxResult.setCode(400);
            ajaxResult.setMessage("未获取到用户信息,请重新登陆");
        }else{
            HashMap hashMap = new HashMap();
            hashMap.put("avatar", "any");
            hashMap.put("introduction", "介绍");
            hashMap.put("roles", new String[]{"any"});
            hashMap.put("name", user.getUserName());
            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
            ajaxResult.setMessage("已登录");
            ajaxResult.setData(hashMap);
        }
        return ajaxResult;
    }


//    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
//    public AjaxResult getInfo(@RequestParam("token") String token){
//        AjaxResult ajaxResult = new AjaxResult();
//        //解密获取
//        String userId = JWT.decode(token).getAudience().get(0); //得到token中的userid载荷
//        //根据userid查询数据库
//        List<User> userList = userDaoMapper.queryUserByStaffId(userId);
//        if(userList.isEmpty()){
//            ajaxResult.setCode(400);
//            ajaxResult.setMessage("未获取到用户信息,请重新登陆");
//        }else{
//            HashMap hashMap = new HashMap();
//            hashMap.put("avatar", "any");
//            hashMap.put("introduction", "介绍");
//            hashMap.put("roles", new String[]{"any"});
//            hashMap.put("name", userList.get(0).getUserName());
//            ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
//            ajaxResult.setMessage("已登录");
//            ajaxResult.setData(hashMap);
//        }
//        return ajaxResult;
//    }
}
