package com.sicao.smartwine.xuser.address;

import java.io.Serializable;

public class XCityEntity implements Serializable{

	/**
	 * 增加城市地址信息
	 */
	private static final long serialVersionUID = 1L;
	private String province;//省份
	private String prosort;//省份代号
	private String city;//城市
	private String proid;//城市代号
	private String zone;//区域
	private String zoneid;//区域id;
	private String id;//执行步骤  如果到第三部结束就让listView消失
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProsort() {
		return prosort;
	}
	public void setProsort(String prosort) {
		this.prosort = prosort;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProid() {
		return proid;
	}
	public void setProid(String proid) {
		this.proid = proid;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getZoneid() {
		return zoneid;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "CityEntity [province=" + province + ", prosort=" + prosort
				+ ", city=" + city + ", proid=" + proid + ", zone=" + zone
				+ ", zoneid=" + zoneid + ", id=" + id + "]";
	}
}
