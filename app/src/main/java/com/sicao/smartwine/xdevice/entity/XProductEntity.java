package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/**
 * 商品实体
 */
public class XProductEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7840618065885052551L;
    String id;//酒品ID
    String name;//酒品名称
    String english_name;//英文名
    String origin_price;//原价
    String current_price;//现价
    String featured_price;//活动价格
    String max_bought;//库存
    String icon;//商品图片

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin_price(String origin_price) {
        this.origin_price = origin_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public void setFeatured_price(String featured_price) {
        this.featured_price = featured_price;
    }

    public void setMax_bought(String max_bought) {
        this.max_bought = max_bought;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrigin_price() {
        return origin_price;
    }

    public String getCurrent_price() {
        return current_price;
    }

    public String getFeatured_price() {
        return featured_price;
    }

    public String getMax_bought() {
        return max_bought;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "XProductEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", english_name='" + english_name + '\'' +
                ", origin_price='" + origin_price + '\'' +
                ", current_price='" + current_price + '\'' +
                ", featured_price='" + featured_price + '\'' +
                ", max_bought='" + max_bought + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
