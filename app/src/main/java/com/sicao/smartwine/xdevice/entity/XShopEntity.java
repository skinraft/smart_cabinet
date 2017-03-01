package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/***
 * 商家实体
 */
public class XShopEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4228158995581434176L;
	/*
	 * 商家ID
	 */
	String id;
	/*
	 * 商家名称
	 */
	String name;

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

}
