package com.sicao.smartwine;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;

/**
 * Created by techssd on 2016/11/28.
 */

public class SmartCabinetApi {
    /***
     * 注册用户
     *
     * @param phone    用户手机号
     * @param password 密码
     * @param code     验证码
     */
    public static void registerByPhone(String phone, String password, String code) {
        //调用用户注册方法
        GizWifiSDK.sharedInstance().registerUser(phone, password, code, GizUserAccountType.GizUserPhone);
    }

    /***
     * 注册用户
     *
     * @param username 用户登录名
     * @param password 用户密码
     */
    public static void register(String username, String password) {
        //调用用户注册方法
        GizWifiSDK.sharedInstance().registerUser(username, password, null, GizUserAccountType.GizUserNormal);
    }

    /***
     * 获取手机号验证码
     * @param appSecret
     * @param phone  手机号码
     */
    public static void requestCode(String appSecret,String phone){
        GizWifiSDK.sharedInstance().requestSendPhoneSMSCode(appSecret, phone);
    }
    
}
