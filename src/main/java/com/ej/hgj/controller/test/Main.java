package com.ej.hgj.controller.test;

import com.alibaba.fastjson.JSONArray;

public class Main {

    public static void main(String[] args) throws Exception {
//        int total = 12;
//        int pageSize = 5;
//        int ceil = (int) Math.ceil((double)total/(double)pageSize);
//        System.out.println(ceil);

        //Client client = new Client(new URL("http://127.0.0.1:9990/configcenter/services/configurationService?wsdl"));
        //Object[] ob = client.invoke("getTheSystemAllConfigurations", new Object[]{"globalConfig", "dev"});
        //System.out.println(ob);
        //System.out.println(System.currentTimeMillis());

//        openid:   o9Lr64nHyP4ERDc7_MD7JqSUM4kY
//
//        MODULE_BITMAP:123456789101112131415161718
//        AUTH_BITMAP:9
//        AUTH_MASK_BM:000000000000000000000000000000

       String s = getAuthBitmap("100210000000000000000000000001","123456789101112131415161718","100000000000000000000000000001");
        JSONArray n = ModuleEnum.moduleValues(s, "N");

        System.out.println(n.toString());


    }

    private static String getAuthBitmap(String authBitmap, String moduleBitmap, String authMaskBm){
        int min = Math.min(moduleBitmap.length(), authBitmap.length());

        String bitmap = "";
        for (int i = 0; i < min; i++) {
            char c = moduleBitmap.charAt(i);
            char c1 = authBitmap.charAt(i);
            char c2 = authMaskBm.charAt(i);
            if(c == '1' && c1 == '1' && c2 == '0'){
                bitmap = bitmap.concat("1");
            }else if(c == '2' && c1 == '1' && c2 == '0'){
                bitmap = bitmap.concat("2");
            }else{
                bitmap = bitmap.concat("0");
            }
        }
        return bitmap;
    }
}
