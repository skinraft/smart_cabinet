package com.sicao.smartwine.xdata;

import android.content.Context;

public class XUserData {
    /**
     * 用户UID
     */
    public static String UID = "UID";

    /**
     * 设置用户UID
     */
    public static String getUID(Context context) {
        return XShareps.getString(context, UID, UID);
    }

    /**
     * 获取用户UID
     */
    public static void setUID(Context context, String UID) {
        XShareps.putString(context, UID, UID, UID);
    }

    /**
     * 用户会话令牌信息
     */
    public static String TOKEN = "TOKEN";

    /**
     * 获取用户Token信息
     */
    public static String getToken(Context context) {
        return XShareps.getString(context, TOKEN, TOKEN);
    }

    /**
     * 保存用户Token信息
     */
    public static void saveToken(Context context, String token) {
        XShareps.putString(context, TOKEN, TOKEN, token);
    }

    /***
     * 接口加密
     */
    public static String PUTAOJI_HTTP_PARAMS = "PUTAOJI_HTTP_PARAMS";

    public static void setCode(Context context, String code) {
        XShareps.putString(context, PUTAOJI_HTTP_PARAMS, PUTAOJI_HTTP_PARAMS, code);
    }

    public static String getCode(Context context) {
        String code = XShareps.getString(context, PUTAOJI_HTTP_PARAMS, PUTAOJI_HTTP_PARAMS);
        if ("".equals(code))
            code = "e10adc3949ba59ab";
        return code;
    }

    /**
     * 校验码使用次数
     */
    public static String PUTAOJI_HTTP_PARAMS_USE_COUNT = "PUTAOJI_HTTP_PARAMS_USE_COUNT";

    public static void setPutaojiHttpParamsUseCount(Context context, int putaojiHttpParamsUseCount) {
        XShareps.putInt(context, PUTAOJI_HTTP_PARAMS_USE_COUNT, PUTAOJI_HTTP_PARAMS_USE_COUNT, putaojiHttpParamsUseCount);
    }

    public static int getPutaojiHttpParamsUseCount(Context context) {
        return XShareps.getInt(context, PUTAOJI_HTTP_PARAMS_USE_COUNT, PUTAOJI_HTTP_PARAMS_USE_COUNT);
    }


    /**
     * 用户UID
     */
    public static String CABINET_UID = "CABINET_UID";

    /**
     * 设置用户UID
     */
    public static String getCabinetUid(Context context) {
        return XShareps.getString(context, CABINET_UID, CABINET_UID);
    }

    /**
     * 获取用户UID
     */
    public static void saveCabinetUid(Context context, String cabinet_uid) {
        XShareps.putString(context, CABINET_UID, CABINET_UID, CABINET_UID);
    }

    /**
     * 用户会话令牌信息
     */
    public static String CABINET_TOKEN = "CABINET_TOKEN";

    /**
     * 获取用户Token信息
     */
    public static String getCabinetToken(Context context) {
        return XShareps.getString(context, CABINET_TOKEN, CABINET_TOKEN);
    }

    /**
     * 保存用户Token信息
     */
    public static void saveCabinetToken(Context context, String cabinet_token) {
        XShareps.putString(context, CABINET_TOKEN, CABINET_TOKEN, cabinet_token);
    }


    /***
     * 当前主面板显示的设备ID
     */
    public static String CURRENT_CABINET_ID = "CURRENT_CABINET_ID";
    public static void setCurrentCabinetId(Context context, String currentCabinetId) {
        XShareps.putString(context, CURRENT_CABINET_ID, CURRENT_CABINET_ID, currentCabinetId);
    }
    public static String getCurrentCabinetId(Context context) {
        return XShareps.getString(context, CURRENT_CABINET_ID, CURRENT_CABINET_ID);
    }
}
