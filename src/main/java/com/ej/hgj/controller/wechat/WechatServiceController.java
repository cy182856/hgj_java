package com.ej.hgj.controller.wechat;


import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.wechat.WechatPubDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubMenuDaoMapper;
import com.ej.hgj.entity.build.Build;
import com.ej.hgj.entity.user.User;
import com.ej.hgj.entity.wechat.WechatPub;
import com.ej.hgj.entity.wechat.WechatPubMenu;
import com.ej.hgj.service.house.HouseService;
import com.ej.hgj.vo.WechatPubVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/wechatPub")
public class WechatServiceController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WechatPubDaoMapper wechatPubDaoMapper;

    @Autowired
    private WechatPubMenuDaoMapper wechatPubMenuDaoMapper;

    @RequestMapping(value = "/addMenu",method = RequestMethod.POST)
    public AjaxResult addMenu(@RequestBody WechatPubVo wechatPubVo) throws Exception {
        AjaxResult ajaxResult = new AjaxResult();
        String message = "";
        String[] ids = wechatPubVo.getId();
        WechatPub wechatPub = new WechatPub();
        if(ids.length > 0){
            for(int i=0;i<ids.length;i++){
                // 根据id查询公众号
                wechatPub.setId(Integer.valueOf(ids[i]));
                List<WechatPub> wechatPubList = wechatPubDaoMapper.getList(wechatPub);
                if (!wechatPubList.isEmpty()) {
                    for (WechatPub wp : wechatPubList) {
                         message = addMenu(wp);
                    }
                }
            }
        }else {
            // 查询所有公众号
            List<WechatPub> wechatPubList = wechatPubDaoMapper.getList(wechatPub);
            if (!wechatPubList.isEmpty()) {
                for (WechatPub wp : wechatPubList) {
                    message = addMenu(wp);
                }
            }
        }
        ajaxResult.setCode(20000);
        ajaxResult.setMessage(message);
        return ajaxResult;
    }

    /**
     * 创建自定义菜单(每天限制1000次)
     * */
    public String addMenu(WechatPub wechatPub) throws Exception {
        String jsonMenu = updateWechatMenu(wechatPub);
        String message = null;
        String access_token = getAccessTokenByApp(wechatPub.getAppId(), wechatPub.getAppSecret());
        String path="https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+access_token;
        try {
            URL url=new URL(path);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(jsonMenu.getBytes("UTF-8"));
            os.close();

            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] bt = new byte[size];
            is.read(bt);
            message=new String(bt,"UTF-8");
            logger.info("[菜单配置],菜单配置结果:{}",message);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 微信菜单
     */
    private String updateWechatMenu(WechatPub wechatPub){

        // 获取该公众号所有菜单
        WechatPubMenu wechatPubMenu = new WechatPubMenu();
        wechatPubMenu.setWechatPubId(wechatPub.getId());
        List<WechatPubMenu> listAll = wechatPubMenuDaoMapper.getList(wechatPubMenu);

        // 获取该公众号主菜单
        List<WechatPubMenu> listMenu = listAll.stream().filter(all -> all.getParentId() == 0).collect(Collectors.toList());

        // 获取该公众号每个主菜单的子菜单
        if(!listMenu.isEmpty()){
            for(WechatPubMenu wp : listMenu){
                List<WechatPubMenu> listMenuSub = listAll.stream().filter(all -> all.getParentId() == wp.getId()).collect(Collectors.toList());
                wp.setSub_button(listMenuSub);
            }
        }

        //1
//        WechatPubMenu menu1 = new WechatPubMenu();
//        menu1.setType("view");
//        menu1.setName("成绩");
//        menu1.setUrl("https://zhgj.shofw.com/wx/queryMutipUsr");
//
//        List<WechatPubMenu> sub_button = new ArrayList<>();
//        WechatPubMenu subMenu = new WechatPubMenu();
//        subMenu.setType("view");
//        subMenu.setName("成绩1");
//        subMenu.setUrl("https://zhgj.shofw.com/wx/queryMutipUsr");
//        sub_button.add(subMenu);
//
//        WechatPubMenu subMenu1 = new WechatPubMenu();
//        subMenu1.setType("view");
//        subMenu1.setName("成绩2");
//        subMenu1.setUrl("https://zhgj.shofw.com/wx/queryMutipUsr");
//        sub_button.add(subMenu1);
//        menu1.setSub_button(sub_button);
//
//        //2
//        WechatPubMenu menu2 = new WechatPubMenu();
//        menu2.setType("view");
//        menu2.setName("错题");
//        menu2.setUrl("https://zhgj.shofw.com/wx/queryMutipUsr");
//
//        //3
//        WechatPubMenu menu3 = new WechatPubMenu();
//        menu3.setType("view");
//        menu3.setName("扫一扫");
//        menu3.setUrl("https://zhgj.shofw.com/wx/queryMutipUsr");
//
//        //ALL
//        List<WechatPubMenu> meunList = new ArrayList<WechatPubMenu>();
//        meunList.add(menu3);
//        meunList.add(menu2);
//        meunList.add(menu1);

        //根节点
        WechatPubMenu menu = new WechatPubMenu();
        // menu.setButton(meunList);
        menu.setButton(listMenu);

        String strMunu = JSONObject.toJSONString(menu);
        return strMunu;
    }


    public String getAccessTokenByApp(String appId,String appsercet) throws Exception{

        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                +appId+ "&secret="+appsercet;

        URL url = new URL(accessTokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.connect();

        //获取返回的字符
        InputStream inputStream = connection.getInputStream();
        int size =inputStream.available();
        byte[] bs =new byte[size];
        inputStream.read(bs);
        String message=new String(bs,"UTF-8");

        //获取access_token
        JSONObject jsonObject = JSONObject.parseObject(message);
        logger.info("获取actoken的结果:" + jsonObject.toJSONString());
        return jsonObject.getString("access_token");
    }


//   public String buildMenuData(){
//        JSONObject object = new JSONObject();
//        JSONArray buttonArray = new JSONArray();
//
//        JSONObject button1 = new JSONObject();
//        // type菜单的相应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
//        button1.put("type","view");
//        // 菜单标题，不超过16个字节，子菜单不超过60个字节
//        button1.put("name","测试菜单");
//        // view、miniprogram类型必须;网页链接，用户点击菜单可打开链接，不超过1024字节。type为miniprogram时，不支持小程序的老版本客户端将打开本url
//        button1.put("url","https://zhgj.shofw.com/wx/queryMutipUsr");
//        buttonArray.set(0,button1);
//
//        JSONObject button2 = new JSONObject();
//        button2.put("type","view");
//        button2.put("name","测试菜单1");
//        button2.put("url","https://zhgj.shofw.com/wx/queryMutipUsr");
//        buttonArray.set(1,button2);
//
//        JSONObject button3 = new JSONObject();
//        button3.put("type","view");
//        button3.put("name","测试菜单2");
//        button3.put("url","https://zhgj.shofw.com/wx/queryMutipUsr");
//        buttonArray.set(2,button3);
//
//        JSONObject button4 = new JSONObject();
//        button4.put("type","view");
//        button4.put("name","测试菜单3");
//        button4.put("url","https://zhgj.shofw.com/wx/queryMutipUsr");
//        buttonArray.set(2,button4);
//
////        JSONObject button3 = new JSONObject();
////        button3.put("type","click");
////        button3.put("name","ccc");
////        // click等点击类型必须,菜单KEY值，用于消息接口推送，不超过128字节;
////        button3.put("key","ccc");
////        buttonArray.set(2,button3);
//
//        object.put("button",buttonArray);
//        logger.info("[菜单配置],配置菜单JSON数据:{}",object.toJSONString());
//
//        return object.toJSONString();
//    }


    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        // 查询所有公众号
        WechatPub wechatPub = new WechatPub();
        List<WechatPub> list = wechatPubDaoMapper.getList(wechatPub);
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

    }
