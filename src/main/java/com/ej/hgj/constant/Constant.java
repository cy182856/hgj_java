package com.ej.hgj.constant;

public class Constant {

    // 删除标识 0-未删除
    public static Integer DELETE_FLAG_NOT = 0;
    // 删除标识 1-已删除
    public static Integer DELETE_FLAG_YES = 1;

    // 项目号
    //public static String PROJECT_NUM = "1000100000";
    // 初始密码
    public static String INIT_PASSWORD = "111111";
    // web平台
    public static Integer PLAT_FORM_WEB = 1;
    // 企微平台
    public static Integer PLAT_FORM_WE_COM = 2;
    // 返回值code-成功
    public static Integer SUCCESS_RESULT_CODE = 20000;
    // 返回值message-成功
    public static String SUCCESS_RESULT_MESSAGE = "成功";
    // 返回值code-失败
    public static Integer FAIL_RESULT_CODE = 99999;
    // 返回值message-失败
    public static String FAIL_RESULT_MESSAGE = "失败";
    // 微信公众号orgId
    public static String WE_CHAT_PUB_ORG_ID = "wechat_pub_org_id";
    //public static String WECHAT_PUB_ORG_ID = "gh_d3bdf6adb0cb";
    // 微信公众号
    public static String WE_CHAT_PUB_APP = "wechat_pub_app";
    //public static String WECHAT_PUB_APP_ID = "wxf32478b0185ce964";
    //public static String WECHAT_PUB_APP_SECRET = "a69e45d063ba9be3176e4ff4dbd64dc5";
    // 临时二维码时效 单位 s
    // public static final String DEFAULT_SECOND = "600";
    public static final String QR_DEFAULT_SECOND = "qr_default_second";
    // token
    public static final String TOKEN = "hgj20230719";

    public static final String RESP_MESSAGE_TYPE_TEXT = "text";

    /**上海朴由科技有限公司  菜单C1点击事件返回**/
    public static final String PY_CLICK_TEXT = "感谢您的关注！\n" +
            "\n" +
            "联系方式：\n" +
            "139 1672 9765  Allen庞\n" +
            "133 8601 8090  Bob王\n" +
            "\n" +
            "联系地址：\n" +
            "上海市长宁区凯旋路1398弄IM长宁国际4号楼3楼";


    /**绑定页面-房主**/
    public static final String BIND_PAGE_OWNER = "pages/hu/hubind/hubind";

    /**绑定页面-租户**/
    public static final String BIND_PAGE_TENANT = "pages/hu/hubindTenant/hubindTenant";

    // 小程序appId
    //public static final String MINI_PROGRAM_APP = "mini_program_app";
    //public static final String MINIPROGRAM_APPID = "wx9dea656366f2c395";
    // 微信小程序智慧社区
    public static final String MINI_PROGRAM_APP_EJ_ZHSQ = "mini_program_app_ej_zhsq";
    // 企业微信id,东方渔人码头
    public static final String WE_COM_APP = "we_com_app";
    // 企业微信通讯录秘钥,东方渔人码头
    public static final String WE_COM_ADDRESS_BOOK_SECRET = "we_com_address_book_secret";
    // 消息模板id
    //public static final String TEMP_LATE_ID = "x8uO5Kg9FcgzgeA9HUaLIFFGngQJiZ2uTcG4r4YTENE";
    public static final String TEMP_LATE_ID = "temp_late_id";

    /** 匿名投诉建议表扬主页 **/
    public static final String ADVICE_PAGE = "pages/anonymityMain/anonymityMain";

    public static final String MD5SALT = "pzucp57mn7j9183ppww4bm7omje47449";

    // 入住角色 0-房主
    public static Integer INTO_ROLE_OWNER = 0;
    // 入住角色 1-租户
    public static Integer INTO_ROLE_TENANT = 1;
    // 入住状态 0-未入住
    public static Integer INTO_STATUS_N = 0;
    // 入住角色 1-已入住
    public static Integer INTO_STATUS_Y = 1;

    /**
     * 应用的调用身份密钥 凡享物管小程序
     */
    //public static final String SuiteSecret = "eeeedfa5daa65e760069248871806ee4";
    public static final String MINI_PROGRAM_APP_EJ = "mini_program_app_ej";

    /** 获取第三方应用凭证 */
    public static final String GET_SUITE_TOKEN = " https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";

    /** 获取企业凭证 */
    public static final String GET_CORP_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN";

    /**
     * 第三方应用的suite_ticket
     */
    public static final String SUITE_TICKET = "suite_ticket";
}
