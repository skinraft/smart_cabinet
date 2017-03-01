package com.sicao.smartwine.xwidget;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;

import java.lang.reflect.Method;

/***
 * WebView客户端:
 * <p>
 * 使用权限 {@link permission#INTERNET}
 * <p>
 * 加载资源:
 * <ol>
 * <li>加载网络资源 : {@link WebView#loadUrl("http://www.google.com")}
 * <li>加载本地assets文件夹下的资源 {@link
 * WebView#loadUrl("file:///android_asset/index.html")}
 * </ol>
 * 
 * @author li'mingqi
 */
public class TWebView extends WebView {
	Context mContext;

	public TWebView(Context context) {
		super(context);
		init(context);
	}

	public TWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void init(Context context) {
		mContext = context;
		/***
		 * 触摸焦点起作用
		 */
		// requestFocusFromTouch();
		/***
		 * 滚动条样式在视图的边缘显示滚动条，
		 */
		setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setDefaultTextEncodingName("UTF-8");
		this.getSettings().setRenderPriority(RenderPriority.HIGH);// 渲染的优先级最高
		this.getSettings().setAllowFileAccess(false);// 可以读取文件缓存(manifest生效)
		this.getSettings().setAppCacheEnabled(false);// 开启应用程序缓存
		this.getSettings().setDomStorageEnabled(false);// 设置可以使用localStorage
		this.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小
		this.getSettings().setLoadWithOverviewMode(false);
		String appCachePath = context.getApplicationContext().getCacheDir()
				.getAbsolutePath();
		this.getSettings().setAppCachePath(appCachePath);// 设置应用缓存的路径
		this.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		setHorizontalScrollBarEnabled(false);// 水平不显示
		setVerticalScrollBarEnabled(false); // 垂直不显示
		addJavascriptInterface(new JavaScriptInterface(), "Android");
	}

	/***
	 * 监听系统手机的返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();// 返回上一个页面,而不是finish调该页面
			return true;
		}
		return false;
	}

	@Override
	public void destroy() {
		try {
			Class<?> localClass = super.getClass();
			Method localMethod = localClass.getMethod("freeMemory",
					new Class[0]);
			localMethod.invoke(this, new Object[0]);
			super.destroy();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class JavaScriptInterface {
		@JavascriptInterface
		@Override
		public String toString() {
			return "Android";
		}
	}

}
