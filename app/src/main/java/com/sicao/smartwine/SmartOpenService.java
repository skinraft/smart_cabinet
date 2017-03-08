package com.sicao.smartwine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.putaoji.android.XInterface;

public class SmartOpenService extends Service {
    public SmartOpenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new XBinder();
    }

    class XBinder extends XInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openActivity(String type, String id) throws RemoteException {
            SmartSicaoApi.log("收到信息");
        }
    }
}
