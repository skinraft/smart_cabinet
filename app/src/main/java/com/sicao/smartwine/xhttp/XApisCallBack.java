package com.sicao.smartwine.xhttp;

import java.util.ArrayList;

/*
 * 接口回调
 */
public interface XApisCallBack {
	// 执行成功
	 <T> void response(ArrayList<T> list);
}
