package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/**
 * Created by techssd on 2017/3/6.
 */

public class XRfidEntity implements Serializable {
    String rfid;
    String tag;
    String device_name;

    public String getRfid() {
        return rfid;
    }

    public String getTag() {
        return tag;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_name() {
        return device_name;
    }

    @Override
    public String toString() {
        return "XRfidEntity{" +
                "rfid='" + rfid + '\'' +
                ", tag='" + tag + '\'' +
                ", device_name='" + device_name + '\'' +
                '}';
    }
}
