package com.sicao.smartwine.xhttp;

/***
 * 接口回调
 * 
 * @author mingqi'li
 * 
 */
public interface XCallBack {
	// 执行成功
	public void success(String response);
	// 执行失败
	public void error(String response);
}
