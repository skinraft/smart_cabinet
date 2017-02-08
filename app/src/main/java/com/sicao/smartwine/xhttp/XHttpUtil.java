package com.sicao.smartwine.xhttp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XUserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class XHttpUtil {

    private Handler mHandler;
    private Context mContext;
    // 请求OK
    public static int SUCCESS = 2;
    // 请求失败
    public static int FAIL = 3;

    public XHttpUtil(Context context) {
        this.mContext = context;
    }

    public void get(final String url, final XCallBack callback) {
        mHandler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int what = msg.what;
                if (SUCCESS == what) {
                    // 成功
                    if (null != callback) {
                        callback.success((String) msg.obj);
                    }
                } else if (FAIL == what) {
                    // 失败
                    if (null != callback) {
                        callback.error((String) msg.obj);
                    }
                }
                return true;
            }
        });
        SmartCabinetApplication.mThreadPool.execute(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                    HttpURLConnection con = null;
                    try {
                        con = (HttpURLConnection) new URL(url)
                                .openConnection();
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        con.setUseCaches(false);
                        con.setRequestMethod("GET");
                        con.setConnectTimeout(5000);
                        con.setReadTimeout(5000);
                        con.setRequestProperty("Connection", "Keep-Alive");
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.addRequestProperty("ptj-stat-json",
                                getHttpHeaderVersin(mContext));
                        con.connect();
                        Log.i("debug", "url=" + url + "\n");
                        int res = con.getResponseCode();
                        InputStream is = null;
                        if (res == 200) {
                            is = con.getInputStream();
                            int ch;
                            StringBuffer b = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                b.append((char) ch);
                            }
                            is.close();
                            String string = new String(b.toString().getBytes(
                                    "ISO-8859-1"), "UTF-8");
                            msg.what = SUCCESS;
                            msg.obj = string;
                        } else {
                            is = con.getErrorStream();
                            int ch;
                            StringBuffer b = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                b.append((char) ch);
                            }
                            is.close();
                            String string = new String(b.toString().getBytes(
                                    "ISO-8859-1"), "UTF-8");
                            msg.what = FAIL;
                            msg.obj = string;
                            Log.i("debug", "url=" + url + "------>" + string);
                        }
                        con.disconnect();
                        mHandler.sendMessage(msg);
                    } catch (MalformedURLException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "URL异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (ProtocolException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "协议异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (SocketException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "SOCKET异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (IOException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "IO异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } finally {
                        if (null != con) {
                            con.disconnect();
                        }
                    }
            }
        });
    }

    public void post(final String url, final HashMap<String, String> params,
                     final XCallBack callback) {
        mHandler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int what = msg.what;
                if (SUCCESS == what) {
                    // 成功
                    if (null != callback) {
                        callback.success((String) msg.obj);
                    }
                } else if (FAIL == what) {
                    // 失败
                    if (null != callback) {
                        callback.error((String) msg.obj);
                    }
                }
                return true;
            }
        });
        SmartCabinetApplication.mThreadPool.execute(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                    HttpURLConnection con = null;
                    try {
                        con = (HttpURLConnection) new URL(url)
                                .openConnection();
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        con.setUseCaches(false);
                        con.setRequestMethod("POST");
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(10000);
                        con.setRequestProperty("Connection", "Keep-Alive");
                        con.setRequestProperty("Charset", "UTF-8");
                        con.addRequestProperty("ptj-stat-json",
                                getHttpHeaderVersin(mContext));
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestProperty("accept", "*/*");
                        con.connect();
                        Log.i("debug", "url=" + url + "\n");
                        Iterator<Entry<String, String>> iter = params.entrySet()
                                .iterator();
                        StringBuffer p = new StringBuffer();
                        while (iter.hasNext()) {
                            Entry<String, String> entry = iter.next();
                            String key = entry.getKey();
                            String val = entry.getValue();
                            p.append(key).append("=").append(val).append("&");
                        }
                        if (p.length() > 0) {
                            p.deleteCharAt(p.length() - 1);
                        }
                        byte[] bypes = p.toString().getBytes();
                        con.getOutputStream().write(bypes);// 输入参数
                        con.getOutputStream().flush();
                        int res = con.getResponseCode();
                        InputStream is = null;
                        if (res == 200) {
                            is = con.getInputStream();
                            int ch;
                            StringBuffer b = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                b.append((char) ch);
                            }
                            is.close();
                            String string = new String(b.toString().getBytes(
                                    "ISO-8859-1"), "UTF-8");
                            msg.what = SUCCESS;
                            msg.obj = string;
                            Log.i("debug", "url=" + url + "------>成功");
                        } else {
                            is = con.getErrorStream();
                            int ch;
                            StringBuffer b = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                b.append((char) ch);
                            }
                            is.close();
                            String string = new String(b.toString().getBytes(
                                    "ISO-8859-1"), "UTF-8");
                            msg.what = FAIL;
                            msg.obj = string;
                            Log.i("debug", "url=" + url + "------>" + string);
                        }
                        mHandler.sendMessage(msg);
                    } catch (MalformedURLException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "URL异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (ProtocolException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "协议异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (SocketException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "SOCKET异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } catch (IOException e) {
                        msg.what = FAIL;
                        msg.obj = null == e ? "IO异常" : e.getMessage() + "";
                        mHandler.sendMessage(msg);
                    } finally {
                        if (null != con) {
                            con.disconnect();
                        }
                    }
            }
        });
    }

    /**
     * 设置请求头里面的版本信息 system_name 例如： ios 9.2，android 6.0 system_type 例如：ios
     * system_version 例如 ：9.2 app_name 例如 ：putaoji/2.1.12* app_type 例如 ：putaoji*
     * app_version 例如 ：2.1.12* device_name 例如 ：iphone 5s * device_type 例如
     * ：iphone device_version 例如 ： device_uuid 例如 ：S6Jl-89sjk-ju789-a71aL
     *
     * @return
     */
    public static String getHttpHeaderVersin(Context context) {
        JSONObject object = new JSONObject();
        try {
            object.put("system_name", "android"
                    + android.os.Build.VERSION.RELEASE);
            object.put("system_type", "android");
            object.put("system_version", android.os.Build.VERSION.RELEASE);
            object.put("app_name",
                    "putaoji/" + AppManager.getVersionName(context));
            object.put("app_type", "putaoji");
            object.put("device_name", android.os.Build.MODEL);
            object.put("device_type", "android");
            object.put("device_version", android.os.Build.VERSION.RELEASE);
            object.put("device_uuid", AppManager.getImei(context));
            object.put("app_version", AppManager.getVersionName(context));
            object.put("channel",
                    AppManager.getAppMetaData(context, "CHANNEL"));
            object.put("HTTP_CONTENT_KEY", XUserData.getCode(context));
            int count = XUserData.getPutaojiHttpParamsUseCount(context);
            count++;
            XUserData.setPutaojiHttpParamsUseCount(context, count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] ni = object.toString().getBytes();
        return new String(Base64.encode(ni,Base64.DEFAULT));
    }
}
