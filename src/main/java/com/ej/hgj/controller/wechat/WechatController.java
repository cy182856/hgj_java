/**
 *
 * 上海云之富金融信息服务有限公司
 * Copyright (c) 2014-2020 YunCF,Inc.All Rights Reserved.
 */
package com.ej.hgj.controller.wechat;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.service.wechat.WechatService;
import com.ej.hgj.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

//@CrossOrigin
//@RestController
//@RequestMapping("/wechatPub")
//public class WechatController {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private WechatService wechatService;
//
//    @RequestMapping(value = "/data/receive", method = RequestMethod.GET)
//    public void receive(ModelMap model, HttpServletResponse rep, String signature, String echostr, String timestamp,
//                        String nonce) {
//        logger.info("服务器绑定启用收到参数", "signature[" + signature + "]", "echostr[" + echostr + "]",
//            "timestamp[" + timestamp + "]", "nonce[" + nonce + "]");
//        if (timestamp == null) timestamp = "";
//        if (nonce == null) nonce = "";
//        if (echostr == null) echostr = "";
//
//        String[] array = { Constant.TOKEN, timestamp, nonce };
//        Arrays.sort(array);
//        String tempStr = array[0] + array[1] + array[2];
//        logger.info("排序后的数据", tempStr);
//
//        String afterEncodeStr = SecurityUtil.encodeBySHA1(tempStr);
//        logger.info("加密后的字符串", afterEncodeStr);
//
//        if (afterEncodeStr.equals(signature)) {
//            try {
//                logger.info("校验成功，成为开发者！");
//                PrintWriter out =  rep.getWriter();
//                out.write(echostr);
//                out.flush();
//                out.close();
//            }catch(IOException e) {
//                logger.info(e.getMessage(), "校验异常！");
//            }
//        } else {
//            logger.info("校验失败！");
//        }
//    }
//
//    @RequestMapping(value="/data/receive", method =RequestMethod.POST)
//    public void receive(ModelMap model,HttpServletRequest request,HttpServletResponse response) throws Exception {
//        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
//        request.setCharacterEncoding("UTF-8");
//        response.setContentType("text/json");
//        response.setCharacterEncoding("UTF-8");
//
//        //防止恶意访问
//        //TODO
//        String respMessage = "";
//        try {
//            //调用核心业务类接收消息、处理消息
//            respMessage = wechatService.handleMessage(request);
//            logger.info("====================返回微信服务器数据：["+respMessage,"]==========================");
//        }catch (Exception e) {
//            logger.info(e.getMessage(), "处理微信服务器消息失败");
//        }
//        // 响应消息
//        PrintWriter out = response.getWriter();
//        out.print(respMessage);
//        out.close();
//    }
//}
