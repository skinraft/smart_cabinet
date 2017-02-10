package com.sicao.smartwine.xwidget.zxing.view;

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
