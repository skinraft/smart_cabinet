package com.sicao.smartwine;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param appSecret
     * @param phone     手机号码
     */
    public static void requestCode(String appSecret, String phone) {
        GizWifiSDK.sharedInstance().requestSendPhoneSMSCode(appSecret, phone);
    }

    /***
     * 重置密码
     *
     * @param phone       登录的手机号
     * @param newPassword 新密码
     * @param code        手机验证码
     */
    public static void resetPassword(String phone, String newPassword, String code) {
        GizWifiSDK.sharedInstance().resetPassword(phone, code, newPassword, GizUserAccountType.GizUserPhone);
    }

    /***
     * 修改密码
     *
     * @param token       用户Token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public static void changePassword(String token, String oldPassword, String newPassword) {
        GizWifiSDK.sharedInstance().changeUserPassword(token, oldPassword, newPassword);
    }

    /***
     * AirLink配置;
     * AirLink使用UDP广播方式，由手机端发出含有目标路由器名称和密码的广播，设备上的Wifi模块接收到广播包后自动连接目标路由器，连上路由器后发出配置成功广播，通知手机配置已完成。
     * <p>
     * 模块开启AirLink模式后，如果一分钟内未收到AirLink广播或无法正确连上路由器，将进入SoftAP模式
     *
     * @param ssid
     * @param key
     */
    public static void configAirLink(String ssid, String key) {
        List<GizWifiGAgentType> types = new ArrayList<GizWifiGAgentType>();
        types.add(GizWifiGAgentType.GizGAgentESP);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(ssid, key, GizWifiConfigureMode.GizWifiAirLink, null, 60, types);
    }

}
