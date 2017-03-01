package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/**
 * 商品实体
 */
public class XGoodsEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7840618065885052551L;
	// 商品ID
	String id;
	// 商品中文名
	String name;
	// 商品英文名
	String english_name;
	// 商品简介
	String description;
	// 现价
	String current_price;
	// 原价
	String origin_price;
	// 活动价格
	String featured_price;
	// 库存
	String max_bought;
	// 商品上下架状态
	String is_effect;
	// 商品购买地址（针对第三方外链商品）
	String mGoodsFromBuy;
	// 商品图片
	String icon;
	// 商品轮播图
	String[] imgs;
	// 商品内容
	String brief;
	// 商品平均评分
	String avg_point;
	// 商品评价总分
	String total_point;
	// 销量
	String bug_count;
	// 商品类型 1葡萄集商品，2智能酒柜商品
	String deal_type;
	// 分享字段
	XShareEntity share;
	// 是否点赞
	boolean is_support;
	// 商品是否选中
	boolean is_selected;
	// 点赞数
	String support_count;
	// 查看数
	String view_count;
	// 店铺信息
	XShopEntity shop;
	// 关联对象ID
	String source_related_id;
	// 加入类型（商品详情或者是每日一酒。。。）
	String source_type;
	// 关联对象的推荐者
	String source_related_uid;
	// 商品数量
	String quantity;
	// 购物车ID
	String cart_id;
	String selected;

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getCart_id() {
		return cart_id;
	}

	public void setCart_id(String cart_id) {
		this.cart_id = cart_id;
	}

	public boolean isIs_selected() {
		return is_selected;
	}

	public void setIs_selected(boolean is_selected) {
		this.is_selected = is_selected;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSource_related_id() {
		return source_related_id;
	}

	public void setSource_related_id(String source_related_id) {
		this.source_related_id = source_related_id;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public String getSource_related_uid() {
		return source_related_uid;
	}

	public void setSource_related_uid(String source_related_uid) {
		this.source_related_uid = source_related_uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglish_name() {
		return english_name;
	}

	public void setEnglish_name(String english_name) {
		this.english_name = english_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrent_price() {
		return current_price;
	}

	public void setCurrent_price(String current_price) {
		this.current_price = current_price;
	}

	public String getOrigin_price() {
		return origin_price;
	}

	public void setOrigin_price(String origin_price) {
		this.origin_price = origin_price;
	}

	public String getFeatured_price() {
		return featured_price;
	}

	public void setFeatured_price(String featured_price) {
		this.featured_price = featured_price;
	}

	public String getMax_bought() {
		return max_bought;
	}

	public void setMax_bought(String max_bought) {
		this.max_bought = max_bought;
	}

	public String getIs_effect() {
		return is_effect;
	}

	public void setIs_effect(String is_effect) {
		this.is_effect = is_effect;
	}

	public String getmGoodsFromBuy() {
		return mGoodsFromBuy;
	}

	public void setmGoodsFromBuy(String mGoodsFromBuy) {
		this.mGoodsFromBuy = mGoodsFromBuy;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getAvg_point() {
		return avg_point;
	}

	public void setAvg_point(String avg_point) {
		this.avg_point = avg_point;
	}

	public String getTotal_point() {
		return total_point;
	}

	public void setTotal_point(String total_point) {
		this.total_point = total_point;
	}

	public String getBug_count() {
		return bug_count;
	}

	public void setBug_count(String bug_count) {
		this.bug_count = bug_count;
	}

	public String getDeal_type() {
		return deal_type;
	}

	public void setDeal_type(String deal_type) {
		this.deal_type = deal_type;
	}

	public void setShare(XShareEntity share) {
		this.share = share;
	}

	public XShareEntity getShare() {
		return share;
	}

	public boolean isIs_support() {
		return is_support;
	}

	public void setIs_support(boolean is_support) {
		this.is_support = is_support;
	}

	public String getSupport_count() {
		return support_count;
	}

	public void setSupport_count(String support_count) {
		this.support_count = support_count;
	}

	public String getView_count() {
		return view_count;
	}

	public void setView_count(String view_count) {
		this.view_count = view_count;
	}

	public XShopEntity getShop() {
		return shop;
	}

	public void setShop(XShopEntity shop) {
		this.shop = shop;
	}

}
