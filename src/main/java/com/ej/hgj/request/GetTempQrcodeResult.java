package com.ej.hgj.request;

public class GetTempQrcodeResult extends BaseResult {

    /**  */
    private static final long serialVersionUID = -813680248836080032L;
    
    /** 当返回临时二维码为图片 */
    private String codeUrl;
    
    /** 当返回临时二维码为url， 需要前端实现二维码转换 */
    private String imgUrl;

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
