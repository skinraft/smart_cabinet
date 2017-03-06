package com.sicao.smartwine.xdevice.entity;

import java.io.Serializable;

/**
 * Created by techssd on 2017/3/6.
 */

public class XRfidEntity implements Serializable {
    String rfid;
    String tag;

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

    @Override
    public String toString() {
        return "XRfidEntity{" +
                "rfid='" + rfid + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
