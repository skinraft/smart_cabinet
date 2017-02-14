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
}