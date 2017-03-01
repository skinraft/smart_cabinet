package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/**
 * 分享实体
 */
public class XShareEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5107170555685468895L;
    //图标地址
    String cover;
    //分享出去的链接
    String url;
    //分享的标题
    String title;
    //分享的内容
    String content;
    //分享出去的二维码的图片
    String qrcode;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Override
    public String toString() {
        return "ShareEntity{" +
                "cover='" + cover + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", qrcode='" + qrcode + '\'' +
                '}';
    }
}
