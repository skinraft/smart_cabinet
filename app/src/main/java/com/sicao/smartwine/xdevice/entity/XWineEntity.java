package com.sicao.smartwine.xdevice.entity;

import com.gizwits.gizwifisdk.api.GizWifiDevice;

import java.io.Serializable;

/**
 * 酒柜中存放的酒款
 */

public class XWineEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7840618065885052551L;
    //酒柜信息
    GizWifiDevice gizWifiDevice;
    //酒款信息
    XProductEntity product;
    //标签数量
    String rfidnum;
    //增加的还是减少的又或者是上次存留的(add,remove,current),也有可能是无法识别的
    String tag;


    public void setRfidnum(String rfidnum) {
        this.rfidnum = rfidnum;
    }

    public String getRfidnum() {
        return rfidnum;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

    public void setGizWifiDevice(GizWifiDevice gizWifiDevice) {
        this.gizWifiDevice = gizWifiDevice;
    }

    public void setProduct(XProductEntity product) {
        this.product = product;
    }

    public XProductEntity getProduct() {
        return product;
    }

    public GizWifiDevice getGizWifiDevice() {
        return gizWifiDevice;
    }

}
