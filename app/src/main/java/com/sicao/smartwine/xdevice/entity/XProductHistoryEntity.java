package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/***
 * 酒信息
 * 
 * @author techssd
 *
 */
public class XProductHistoryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4822859628198675973L;
	// 酒款id
	String id;
	// 酒款价格
	String price;
	// 酒款图片
	String icon;
	// 酒款名字
	String name;
	// 酒款便签(扩展使用)
	String tag;
	// 酒款数量
	int count;
	//
	String code;
	//
	String date;

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "XProductEntity [id=" + id + ", price=" + price + ", icon=" + icon + ", name=" + name + ", tag=" + tag
				+ ", count=" + count + ", code=" + code + ", date=" + date + "]";
	}

}
