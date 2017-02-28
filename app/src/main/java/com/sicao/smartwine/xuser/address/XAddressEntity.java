package com.sicao.smartwine.xuser.address;

import java.io.Serializable;

/**
 * 收货地址
 * 
 * @author mingqi'li
 * 
 */
public class XAddressEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9090590062300359809L;
	//收货地址的ID
	private String id;
	private String name;
	private String address;
	private String phone;
	
	private boolean isdefault;
	
	//判断是否设为默认地址  默认为真
	public void setIsdefault(boolean isdefault) {
		this.isdefault = isdefault;
	}

	public boolean isIsdefault() {
		return isdefault;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "{"+"name:"+name+",address:"+address+",phone:"+phone+",isdefault:"+isdefault+",id:"+id+"}";
	}

}
