package com.sicao.smartwine.xhttp;

/***
 * 用于触发更新设备函数
 */
public interface XSmartCabinetListener {
    /***
     * 回调函数
     *
     * @param update 是否需要更新设备信息
     * @param action 动作类型
     */
    public void update(boolean update, String action);


}
