package com.sicao.smartwine.xdata;

import java.io.Serializable;
import java.util.Arrays;

/***
 * 设备的基本信息
 */
public class SmartCabinet implements Serializable {

    //设备ID
    String id;
    //设备编号
    String number;
    //设备名称
    String name;
    //设备mac地址
    String mac;
    //设备的真实地址
    String realposition;
    //设备是否在线
    boolean online;
    //设备的设置温度
    String settemp;
    //设备的真实温度
    String realtemp;
    //设备内的标签集
    String[] tags;
    //设备的电源开关
    boolean on;
    //设备工作模式
    String model;
    //设备的工作状态
    String status;
    //设备的绑定状态
    String bandstatus;
    //设备灯开关
    boolean islight;

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getRealposition() {
        return realposition;
    }

    public boolean isOnline() {
        return online;
    }

    public String getRealtemp() {
        return realtemp;
    }

    public String getSettemp() {
        return settemp;
    }

    public String[] getTags() {
        return tags;
    }

    public boolean isOn() {
        return on;
    }

    public String getModel() {
        return model;
    }

    public String getStatus() {
        return status;
    }

    public String getBandstatus() {
        return bandstatus;
    }

    public boolean islight() {
        return islight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setRealposition(String realposition) {
        this.realposition = realposition;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setSettemp(String settemp) {
        this.settemp = settemp;
    }

    public void setRealtemp(String realtemp) {
        this.realtemp = realtemp;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBandstatus(String bandstatus) {
        this.bandstatus = bandstatus;
    }

    public void setIslight(boolean islight) {
        this.islight = islight;
    }

    @Override
    public String toString() {
        return "SmartCabinet{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", realposition='" + realposition + '\'' +
                ", online=" + online +
                ", settemp='" + settemp + '\'' +
                ", realtemp='" + realtemp + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", on=" + on +
                ", model='" + model + '\'' +
                ", status='" + status + '\'' +
                ", bandstatus='" + bandstatus + '\'' +
                ", islight=" + islight +
                '}';
    }
}
