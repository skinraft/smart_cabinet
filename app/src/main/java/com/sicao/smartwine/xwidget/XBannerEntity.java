package com.sicao.smartwine.xwidget;

import java.io.Serializable;

/**
 * 轮播广告栏
 * 
 * @author putaoji
 * 
 */
public class XBannerEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3787879831317218778L;
	private String ctype;// 广告图类型
	private String cover_image;// 广告图的地址
	private String link;// 广告地址
	private String title;
	private String id;
	private String cid;

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCid() {
		return cid;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public String getCover_image() {
		return cover_image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
