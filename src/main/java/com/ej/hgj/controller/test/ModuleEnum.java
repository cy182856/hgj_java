package com.ej.hgj.controller.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.StringUtils;

public enum ModuleEnum {

    M00(9999, "00", "/pages/main/main", "主页"),
    M01(1, "01", "/subpages/appt/show_apptObjInfo", "预约"),
    M011(1, "011", "/subpages/appt/mine_appt", "我的预约"),
    M02(9999, "02", "/subpages/pfee/pfeeMonBillSubmit", "缴费"),
    M03(3, "03", "/subpages/heo/heoinfo/heoinfo", "邻里圈"),
    M031(3, "031", "/subpages/heo/heodetail/heodetail", "邻里圈帖子详情"),
    M032(3, "032", "/subpages/heo/mineRelease/mineRelease", "邻里圈我的发布"),
    M04(4, "04", "/subpages/qn/queryQnInfo/queryQnInfo", "问卷调查"),
    M05(9999, "05", "/pages/await/await", "水电煤抄表"),
    M06(9999, "06", "/pages/await/await", "店铺收款"),
    M07(9999, "07", "/subpages/visit/visitInfo/visitInfo", "访客管理"),
    M08(9999, "08", "/subpages/repair/pages/repairApply/repairApply", "报修申请"),
    M081(9999, "081", "/subpages/repair/pages/repairDetailPage/repairDetailPage", "报修详情"),
    M082(9999, "082", "/subpages/repair/pages/niming/repairDetail", "报修详情"),
    M09(9999, "09", "/subpages/repair/pages/ranking/ranking", "共建家园"),
    M10(9999, "10", "/pages/smartDoor/smartDoor", "业主码"),
    M11(9999, "11", "/pages/wuyeInfo/wuyeInfo", "便民信息"),
    M12(9999, "12", "/subpages/gonggao/query/queryGGListPage", "公告"),
    M13(9999, "13", "/pages/await/await", "建议和投诉"),
    M131(9999, "131", "/subpages/advice/detail/addviceDetail", "反馈详情"),
    M14(9999, "14", "/pages/await/await", "房屋租售展示"),
    M18(18, "18", "/subpages/charge/charge", "充电桩充电"),
    M0121(9999, "0121", "/subpages/charge/chargedetail/chargedetail", "充电桩充电详情"),
    M15(9999, "16", "/subpages/order/order", "订单");

    private int bit;
    private String hgjModuleId;
    private String pagePath;
    private String pageDesc;

    private ModuleEnum(int bit, String hgjModuleId, String pagePath, String pageDesc) {
        this.bit = bit;
        this.hgjModuleId = hgjModuleId;
        this.pagePath = pagePath;
        this.pageDesc = pageDesc;
    }

    public String getHgjModuleId() {
        return this.hgjModuleId;
    }

    public void setHgjModuleId(String hgjModuleId) {
        this.hgjModuleId = hgjModuleId;
    }

    public String getPagePath() {
        return this.pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getPageDesc() {
        return this.pageDesc;
    }

    public void setPageDesc(String pageDesc) {
        this.pageDesc = pageDesc;
    }

    public int getBit() {
        return this.bit;
    }

    public void setBit(int bit) {
        this.bit = bit;
    }

    public static JSONArray moduleValues(String authBit, String huStat) {
        String defaultPage = "/pages/await/await";
        char[] chars = authBit.toCharArray();
        JSONArray jsonArray = new JSONArray();
        ModuleEnum[] var5 = values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            ModuleEnum value = var5[var7];
            int bit = value.getBit();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hgjModuleId", value.getHgjModuleId());
            if (bit != 9999 && chars[bit - 1] != '1' && (chars[bit - 1] != '2' ||  StringUtils.equals(huStat, "P"))) {
                jsonObject.put("pagePath", defaultPage);
            } else {
                jsonObject.put("pagePath", value.getPagePath());
            }

            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }
}
