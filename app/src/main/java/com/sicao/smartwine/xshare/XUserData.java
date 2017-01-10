package com.sicao.smartwine.xshare;

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
     * 用户TOKEN
     */
    public static String TOKEN = "TOKEN";

    /**
     * 设置用户TOKEN
     */
    public static String getTOKEN(Context context) {
        return XShareps.getString(context, TOKEN, TOKEN);
    }

    /**
     * 获取用户TOKEN
     */
    public static void setTOKEN(Context context, String TOKEN) {
        XShareps.putString(context, TOKEN, TOKEN, TOKEN);
    }

}
