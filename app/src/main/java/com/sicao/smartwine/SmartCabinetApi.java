package com.sicao.smartwine;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by techssd on 2016/11/28.
 */

public class SmartCabinetApi {
    /***
     * 手机号注册用户
     *
     * @param phone    用户手机号
     * @param password 密码
     * @param code     验证码
     */
    public void registerByPhone(String phone, String password, String code) {
        //调用用户注册方法
        GizWifiSDK.sharedInstance().registerUser(phone, password, code, GizUserAccountType.GizUserPhone);
    }

    /***
     * 普通帐号注册用户
     *
     * @param username 用户登录名
     * @param password 用户密码
     */
    public void register(String username, String password) {
        //调用用户注册方法
        GizWifiSDK.sharedInstance().registerUser(username, password, null, GizUserAccountType.GizUserNormal);
    }

    /***
     * 获取手机号验证码
     *
     * @param appSecret
     * @param phone     手机号码
     */
    public void requestCode(String appSecret, String phone) {
        GizWifiSDK.sharedInstance().requestSendPhoneSMSCode(appSecret, phone);
    }

    /***
     * 重置密码
     *
     * @param phone       登录的手机号
     * @param newPassword 新密码
     * @param code        手机验证码
     */
    public void resetPassword(String phone, String newPassword, String code) {
        GizWifiSDK.sharedInstance().resetPassword(phone, code, newPassword, GizUserAccountType.GizUserPhone);
    }

    /***
     * 修改密码
     *
     * @param token       用户Token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changePassword(String token, String oldPassword, String newPassword) {
        GizWifiSDK.sharedInstance().changeUserPassword(token, oldPassword, newPassword);
    }

    /***
     * 帐号密码登录
     * @param username
     * @param password
     */
    public void login(String username,String password){
        GizWifiSDK.sharedInstance().userLogin(username, password);
    }

    /***
     * AirLink模式配置;
     * AirLink使用UDP广播方式，由手机端发出含有目标路由器名称和密码的广播，设备上的Wifi模块接收到广播包后自动连接目标路由器，连上路由器后发出配置成功广播，通知手机配置已完成。
     * <p>
     * 模块开启AirLink模式后，如果一分钟内未收到AirLink广播或无法正确连上路由器，将进入SoftAP模式
     *
     * @param ssid
     * @param key
     */
    public void configAirLink(String ssid, String key) {
        List<GizWifiGAgentType> types = new ArrayList<GizWifiGAgentType>();
        types.add(GizWifiGAgentType.GizGAgentESP);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(ssid, key, GizWifiConfigureMode.GizWifiAirLink, null, 60, types);
    }

    /***
     * SoftAP模式配置
     * 设备进入SoftAP模式后，会产生一个Wifi热点。手机连上此热点后，将要配置的SSID和密码发给设备。设备上的Wi-Fi模块接收到SoftAP配置包后自动连接目标路由器，
     * 与airlink一样，连上路由器后发出配置成功广播，通知手机配置已完成。
     * 使用机智云提供的模组固件，设备产生的Wifi热点以“XPG-GAgent-”开头，密码为” 123456789”。其他厂商提供的模组，SoftAP热点名称由各自厂商指定。APP可以根据需要传入正确的热点前缀。
     *
     * @param ssid
     * @param key
     * @param gagent_hotspot_prefix
     */
    public void configSoftAp(String ssid, String key, String gagent_hotspot_prefix) {
        GizWifiSDK.sharedInstance().setDeviceOnboarding(ssid, key, GizWifiConfigureMode.GizWifiSoftAP, gagent_hotspot_prefix, 60, null);
    }

    /***
     * 获取缓存的设备列表信息
     *
     * @return 返回缓存的设备列表信息
     */
    public List<GizWifiDevice> getCacheDeviceList() {
        return GizWifiSDK.sharedInstance().getDeviceList();
    }

    /***
     * 主动刷新绑定设备列表、指定筛选的设备productKey
     *
     * @param your_uid   用户uid
     * @param your_token 用户token
     * @param productKey 指定筛选的设备productKey
     */
    public void refushDeviceList(String your_uid, String your_token, String productKey) {
        List<String> pks = new ArrayList<String>();
        pks.add(productKey);
        GizWifiSDK.sharedInstance().getBoundDevices(your_uid, your_token, pks);
    }

    /***
     * 设备订阅，绑定WIFI设备，所有通过SDK得到的设备，都可以订阅，订阅结果通过回调返回。订阅成功的设备，要在其网络状态变为可控时才能查询状态和下发控制指令。
     *
     * @param device   设备对象
     * @param listener 设备监控回调对象
     */
    public void bindDevice(GizWifiDevice device, GizWifiDeviceListener listener) {
        device.setListener(listener);
        device.setSubscribe(true);
    }

    /***
     * 设备解绑
     * 已绑定的设备可以解绑，解绑需要APP调用接口完成操作，SDK不支持自动解绑。
     * 对于已订阅的设备，解绑成功时会被解除订阅，同时断开设备连接，设备状态也不会再主动上报了。
     * 设备解绑后，APP刷新绑定设备列表时就得不到该设备了。
     *
     * @param your_uid        用户UID
     * @param your_token      用户token
     * @param your_device_did 设备did
     */
    public void unBindDevice(String your_uid, String your_token, String your_device_did) {
        GizWifiSDK.sharedInstance().unbindDevice(your_uid, your_token, your_device_did);
    }

    /***
     * 非局域网设备绑定（Wifi设备不需要远程绑定）
     * APP可以通过设备的mac、productKey、productSecret完成非局域网设备的绑定,可以用上述信息生成二维码，APP通过扫码方式绑定。
     * GPRS设备、蓝牙设备等都是无法通过Wifi局域网发现的设备，都属于非局域网设备。
     *
     * @param your_uid                用户UID
     * @param your_token              用户token
     * @param your_deivce_mac         设备mac
     * @param your_device_product_key 设备product_key
     * @param your_product_secret     密钥
     */
    public void bandDeviceByCode(String your_uid, String your_token, String your_deivce_mac, String your_device_product_key, String your_product_secret) {
        GizWifiSDK.sharedInstance().bindRemoteDevice(your_uid, your_token, your_deivce_mac, your_device_product_key, your_product_secret);
    }

    /***
     * 获取设备硬件信息
     * 不订阅设备也可以获取到硬件信息。APP可以获取模块协议版本号，mcu固件版本号等硬件信息，但是只能在小循环下才能获取。
     * @param device  设备
     */
    public void getHardwareInfo(GizWifiDevice device){
        device.getHardwareInfo();
    }

    /***
     * 设备状态查询
     * 设备订阅变成可控状态后，APP可以查询设备状态。设备状态查询结果也通过didReceiveData回调返回，
     * 回调参数sn为0。回调参数dataMap为设备回复的状态。
     * @param device
     */
    public  void getDeviceStatus(GizWifiDevice device){
        device.getDeviceStatus();
    }

    /***
     * 发送控制指令
     * 设备订阅变成可控状态后，APP可以发送操作指令。操作指令是字典格式，键值对为数据点名称和值。操作指令的确认回复，通过didReceiveData回调返回。
     * APP下发操作指令时可以指定action，通过回调参数中的sn能够对应到下发指令是否发送成功了。
     * 但回调参数dataMap有可能是空字典，这取决于设备回复时是否携带当前数据点的状态。
     * 如果APP下发指令后只关心是否有设备状态上报，那么下发指令的action可填0，这时回调参数action也为0。
     * @param device
     * @param key
     * @param object
     */
    public void controlDevice(GizWifiDevice device,String key,Object object,int action){
        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<String, Object>();
        command.put(key, object);
        device.write(command,action);
    }


}
