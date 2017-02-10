package com.sicao.smartwine.xhttp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdata.XUserData;
/**
 * Created by techssd on 2017/1/12.
 */

public class XApiService extends Service {
    int time=60;
    LongRunning thread = new LongRunning();
    SmartSicaoApi xApiClient=new SmartSicaoApi();
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if (what==7777777){
                update();
            }
        }
    };
    public XApiService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 耗时操作
     */
    class LongRunning extends Thread {
        @Override
        public void run() {
            //耗时业务处理
            while (time>0){
//                SmartSicaoApi.log("更新倒计时剩余-----"+time+"秒-----校验码使用次数-----"+ XUserData.getPutaojiHttpParamsUseCount(XApiService.this));
                time--;
                //检测校验码使用次数 ，当校验码使用次数超过90时更新校验码，1分钟倒计准时更新校验码
                if (time==0||XUserData.getPutaojiHttpParamsUseCount(XApiService.this)>=90){
                    time=60;
                    Message msg=mHandler.obtainMessage();
                    msg.what=7777777;
                    mHandler.sendMessage(msg);
                }
                Thread.currentThread();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void update(){
        xApiClient.getCode(XApiService.this, new XApiCallBack() {
            @Override
            public void response(Object object) {
                XUserData.setCode(XApiService.this, (String) object);
                //重置校验码
                XUserData.setPutaojiHttpParamsUseCount(XApiService.this,0);
            }
        });
    }
}
