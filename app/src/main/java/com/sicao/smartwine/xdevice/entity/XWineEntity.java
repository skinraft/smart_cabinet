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
    XGoodsEntity xGoodsEntity;

    public void setGizWifiDevice(GizWifiDevice gizWifiDevice) {
        this.gizWifiDevice = gizWifiDevice;
    }

    public void setxGoodsEntity(XGoodsEntity xGoodsEntity) {
        this.xGoodsEntity = xGoodsEntity;
    }

    public XGoodsEntity getxGoodsEntity() {
        return xGoodsEntity;
    }

    public GizWifiDevice getGizWifiDevice() {
        return gizWifiDevice;
    }

    @Override
    public String toString() {
        return "XWineEntity{" +
                "gizWifiDevice=" + gizWifiDevice +
                ", xGoodsEntity=" + xGoodsEntity +
                '}';
    }
}
