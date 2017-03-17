package com.sicao.smartwine;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.entity.XProductEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xhttp.XApisCallBack;
import com.sicao.smartwine.xhttp.XCallBack;
import com.sicao.smartwine.xhttp.XConfig;
import com.sicao.smartwine.xhttp.XHttpUtil;
import com.sicao.smartwine.xuser.address.XAddressEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 非硬件部分API
 */

public class SmartSicaoApi implements XApiException {

    public static String URL = "http://www.putaoji.com/apiwine/";

    /***
     * 检测接口执行状态status
     *
     * @param object 接口返回对象
     * @return 是否执行OK
     */
    public boolean status(JSONObject object) throws JSONException {
        return object.getBoolean("status");
    }

    /***
     * 接口错误信息打印
     *
     * @param error
     */
    @Override
    public void error(String error) {
        if (XConfig.DEBUG)
            Log.i(XConfig.LOG_TAG, error);
    }

    /***
     * 日志打印
     *
     * @param logs
     */
    public static void log(String logs) {
        if (XConfig.DEBUG)
            Log.i(XConfig.LOG_TAG, logs);
    }

    /***
     * 为url设置通用参数
     *
     * @param api     对应的具体接口(如apix/pubaTopic/lists)
     * @param context 上下文对象
     * @return 设置参数后的url
     */
    public String configParamsUrl(String api, Context context) {
        return URL + api + "?user_token=" + XUserData.getToken(context)
                + "&uid=" + XUserData.getUID(context) + "&api_version="
                + XConfig.API_VERSION  ;
    }

    /***
     * 接口校验参数之校验码的获取
     *
     * @param context
     * @param callback
     */
    public void getCode(final Context context, final XApiCallBack callback) {
        String url = configParamsUrl("ContentKey/getNewInfo", context);
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    log("切换KEY----" + response);
                    if (status(object)) {
                        if (null != callback) {
                            callback.response(object.getJSONObject("data").getString("newKey"));
                        }
                    } else {
                        if (null != callback) {
                            callback.response(XUserData.getCode(context));
                        }
                    }
                } catch (JSONException e) {
                    if (null != callback) {
                        callback.response(XUserData.getCode(context));
                    }
                }
            }

            @Override
            public void fail(String response) {
                if (null != callback) {
                    callback.response(XUserData.getCode(context));
                }
                error(response);
            }
        });
    }

    /***
     * 登录葡萄集平台,获取该平台中对应帐号的uid以便于登录机智云平台
     *
     * @param context      上下文对象
     * @param username     用户手机号
     * @param password     用户密码
     * @param xApiCallBack 结果回调
     */
    public void login(final Context context, final String username, final String password, final XApiCallBack xApiCallBack, final XApiException xApiException) {
        String url = configParamsUrl("user/login?mobile=" + username + "&value=" + password + "&type=2", context);
        log(url);
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    log("login----" + response);
                    if (status(object)) {
                        XUserData.saveToken(context, object.getJSONObject("data").getString("user_token"));
                        XUserData.setUID(context, object.getJSONObject("data").getString("uid"));
                        XUserData.setPassword(context, password);
                        XUserData.setUserName(context, username);
                        if (null != xApiCallBack) {
                            xApiCallBack.response("success");
                        }
                    } else {
                        error(object.getString("message"));
                        if (null != xApiException) {
                            xApiException.error(object.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    error(e.getMessage());
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != xApiException) {
                    xApiException.error(response);
                }
            }
        });
    }

    /***
     * 注册(使用手机号和验证码进行注册)
     *
     * @param context   上下文对象
     * @param mobile    手机号码
     * @param code      验证码
     * @param password  密码
     * @param callback  接口执行OK回调对象
     * @param exception 接口实现失败回调
     */
    public void register(final Context context, final String mobile, String code,
                         final String password, final XApiCallBack callback,
                         final XApiException exception) {
        String url = configParamsUrl("user/setPassword", context) + "&mobile="
                + mobile + "&code=" + code + "&password=" + password;
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        if (null != callback) {
                            XUserData.saveToken(context, object.getJSONObject("data").getString("user_token"));
                            XUserData.setUID(context, object.getJSONObject("data").getString("uid"));
                            XUserData.setPassword(context, password);
                            XUserData.setUserName(context, mobile);
                            callback.response("success");
                        }
                    } else {
                        if (null != exception) {
                            exception.error(object.getString("message"));
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception) {
                    exception.error(response);
                }
            }
        });
    }

    /***
     * 获取短信验证码
     *
     * @param context   上下文对象
     * @param mobile    手机号码
     * @param callback  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public void getCodeForRegister(final Context context, String mobile, String type,
                                   final XApiCallBack callback, final XApiException exception) {
        String url = configParamsUrl("User/verifymobile", context) + "&mobile="
                + mobile + "&type=" + type;
        XHttpUtil httpUtil = new XHttpUtil(context);
        httpUtil.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        if (null != callback) {
                            callback.response("true");
                        }
                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 拉取个人信息
     *
     * @param context     上下文对象
     * @param apiCallBack 接口执行OK回调对象
     * @param exception   接口执行失败回调对象
     */
    public void getUserInfo(Context context, final XApiCallBack apiCallBack, final XApiException exception) {
        String url = configParamsUrl("user/getProfile", context);
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        JSONObject info = object.getJSONObject("data")
                                .getJSONObject("profile");
                        if (null != apiCallBack) {
                            apiCallBack.response(info);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 用户反馈接口/帖子举报接口
     *
     * @param context 上下文对象
     * @param remark  type为1时标识为意见反馈，remark表示意见内容
     */
    public void feedBack(final Context context, final String remark,
                         final XApiCallBack callback, final XApiException exception) {
        String url = configParamsUrl("mine/feedback", context);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        // 用户反馈
        params.put("remark", remark + "");
        XHttpUtil httpUtil = new XHttpUtil(context);
        httpUtil.post(url, params, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        if (null != callback) {
                            callback.response(object);
                        }
                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }


    /***
     * 设置某一个地址为的默认地址
     *
     * @param context   上下文对象
     * @param id        地址ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public void configDefaultAddress(final Context context, String id,
                                     final XApiCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("DealAddress/setdefaultaddress", context) + "&id=" + id;
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(object);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 获取我的地址列表信息
     *
     * @param context   上下文对象
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public void getAddressList(final Context context,
                               final XApisCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("DealAddress/getaddress", context);
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        ArrayList<XAddressEntity> list = new ArrayList<>();
                        JSONObject info = object.getJSONObject("data");
                        JSONArray array = info.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            XAddressEntity add = new XAddressEntity();
                            JSONObject address = array.getJSONObject(i);
                            add.setAddress(address.getString("address"));
                            add.setPhone(address.getString("tel"));
                            add.setName(address.getString("realName"));
                            add.setId(address.getString("id"));
                            String defaults = address.getString("default");
                            if ("1".equals(defaults)) {
                                add.setIsdefault(true);
                            } else {
                                add.setIsdefault(false);
                            }
                            list.add(add);
                        }
                        if (null != callBack) {
                            callBack.response(list);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 根据某一个地址的地址ID删除某一个地址
     *
     * @param context   上下文对象
     * @param id        地址ID
     * @param callBack  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public void deleteAddressByID(final Context context, String id,
                                  final XApiCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("DealAddress/deleteaddress", context) + "&id=" + id;
        XHttpUtil http = new XHttpUtil(context);
        http.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        if (null != callBack) {
                            callBack.response(object);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 添加地址
     * @param context  上下文对象
     * @param name     收货人姓名
     * @param phone    收货人电话
     * @param address  收货人地址
     * @param callBack    接口执行OK回调对象
     * @param exception   接口执行失败回调对象
     */
    public void addAddress(final Context context, final String name, final String phone,
                           final String address, final XApiCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("DealAddress/addaddress", context);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_token", XUserData.getToken(context));
        params.put("realName", name);
        params.put("address", address);
        params.put("tel", phone);
        params.put("userId", XUserData.getUID(context));
        XHttpUtil httpUtil = new XHttpUtil(context);
        httpUtil.post(url, params, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        if (null != callBack) {
                            callBack.response(object);
                        }
                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 获取酒柜中的酒信息
     * @param context   上下文对象
     * @param mac       酒柜mac
     * @param callback  接口执行OK回调对象
     * @param exception 接口执行失败回调对象
     */
    public void getGoodsByMac(Context context, String mac,final XApisCallBack callback, final XApiException exception) {
        String url = configParamsUrl("Device/newWineLists", context)+"&mac="+mac;
        log(url);
        XHttpUtil httpUtil = new XHttpUtil(context);
        httpUtil.get(url,new XCallBack() {
            @Override
            public void success(String response) {
                log(response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        JSONArray array = object.getJSONObject("data").getJSONArray("products");
                        ArrayList<XWineEntity> mList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject wine=array.getJSONObject(i);
                            XWineEntity wineEntity=new XWineEntity();
                            XProductEntity xProductEntity=new XProductEntity();
                            xProductEntity.setName(wine.getString("name"));
                            xProductEntity.setCurrent_price(wine.getString("current_price"));
                            xProductEntity.setIcon(wine.getString("icon"));
                            xProductEntity.setId(wine.getString("id"));
                            wineEntity.setProduct(xProductEntity);
                            wineEntity.setRfidnum(wine.getString("num"));
                            mList.add(wineEntity);
                        }
                        if (null != callback) callback.response(mList);
                    } else {
                        log(object.getString("message"));
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 根据设备的mac信息获取该设备的标签数据
     * @param context
     * @param mac
     * @param callBack
     */
    public void getServerCabinetRfidsByMAC(Context context, String mac, final XApiCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("Device/tagLog", context) + "&mac=" + mac+"&page=1&row=1";
        log(url);
        XHttpUtil xHttpUtil = new XHttpUtil(context);
        xHttpUtil.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {
                        //"{"newCount":8,"deleteCount":8,"num":"21","date":"2017-03-17 09:38:06","time":"1489714686"}
                        if (null != callBack) callBack.response(object.getJSONObject("data").getJSONArray("tagLogs").getJSONObject(0));
                    } else {
                        log(object.getString("message"));
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }

    /***
     * 获取商品详情
     * @param context
     * @param productID
     * @param callBack
     * @param exception
     */
    public void getProductInfo(Context context, String productID, final XApiCallBack callBack, final XApiException exception) {
        String url = configParamsUrl("deal/get", context) + "&id=" + productID;
        log(url);
        XHttpUtil xHttpUtil = new XHttpUtil(context);
        xHttpUtil.get(url, new XCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (status(object)) {

                        XProductEntity entity=new Gson().fromJson(object.getJSONObject("data").getJSONObject("product").toString(),XProductEntity.class);
                        if (null != callBack) callBack.response(entity);
                    } else {
                        log(object.getString("message"));
                    }
                } catch (JSONException e) {
                }
            }
            @Override
            public void fail(String response) {
                error(response);
                if (null != exception)
                    exception.error(response);
            }
        });
    }
}
