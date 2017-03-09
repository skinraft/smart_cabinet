package com.sicao.smartwine.xhttp;

/***
 * 配置文件
 *
 * @author tagmic
 * @date 2016/05/13
 */
public class XConfig {
    /*
     * API环境(默认为正式环境)
     */
    public static boolean DEBUG = true;
    /*
     * API版本
     */
    public static String API_VERSION = "1.0.0";
    /*
     *日志TAG
     */
    public static String LOG_TAG = "yacht";
    /*
     *更新设备的工作模式action
     */
    public static int CONFIG_CABINET_WORK_MODEL_ACTION = 10086;
    /*
     *更新设备的设置温度action
     */
    public static int CONFIG_CABINET_SET_TEMP_ACTION = 10010;
    /*
     *混合更新工作模式和温度action
     */
    public static int CONFIG_CABINET_MODEL_TEMP_ACTION = 10011;
    /*
     *更新设备的设备灯开关action
     */
    public static int CONFIG_CABINET_SET_LIGHT_ACTION = 10017;
    /*
     *设备异常
     */
    public static int CABINET_HAS_EXCEPTION = 10018;

    /*
     *当前帐号没有绑定设备
     */
    public static int CURRENT_NO_CABINET = 10019;
    /*
     *更新首页的RFID数量
     */
    public static int CABINET_INFO_UPDATE_RFIDS_NUMBER=10020;
    /*
     *设置读写器的工作时间action
     */
    public static int CONFIG_CABINET_SET_WORK_TIME = 10097;
    /*
     *页面刷新
     */
    public static int BASE_UPDATE_ACTION = 777777;
    /*
     *页面加载更多
     */
    public static int BASE_LOAD_ACTION = 999999;
}
