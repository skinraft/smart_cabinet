package com.sicao.smartwine.xhttp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XRfidDataUtil;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.entity.XProductEntity;
import com.sicao.smartwine.xdevice.entity.XRfidEntity;

/**
 * Created by techssd on 2017/1/12.
 */

public class XApiService extends Service {
    int time = 60;
    LongRunning thread = new LongRunning();
    SmartSicaoApi xApiClient = new SmartSicaoApi();
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 7777777) {
                update();
            } else if (what == 888888) {
                notiQueue();
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
            while (time > 0) {
//                SmartSicaoApi.log("更新倒计时剩余-----"+time+"秒-----校验码使用次数-----"+ XUserData.getPutaojiHttpParamsUseCount(XApiService.this));
                time--;
                //检测校验码使用次数 ，当校验码使用次数超过90时更新校验码，1分钟倒计准时更新校验码
                if (time == 0 || XUserData.getPutaojiHttpParamsUseCount(XApiService.this) >= 90) {
                    time = 60;
                    Message msg = mHandler.obtainMessage();
                    msg.what = 7777777;
                    mHandler.sendMessage(msg);
                }
                //检测是否有需要发送通知酒款信息取出/放入的消息通知的队列
                Message msg = mHandler.obtainMessage();
                msg.what = 888888;
                mHandler.sendMessage(msg);
                Thread.currentThread();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新接口CODE
     */
    public void update() {
        xApiClient.getCode(XApiService.this, new XApiCallBack() {
            @Override
            public void response(Object object) {
                XUserData.setCode(XApiService.this, (String) object);
                //重置校验码
                XUserData.setPutaojiHttpParamsUseCount(XApiService.this, 0);
            }
        });
    }

    public void notiQueue() {
        synchronized (SmartCabinetApplication.notiQueue) {
            SmartSicaoApi.log("消息通知队列信息-----" + SmartCabinetApplication.notiQueue.size());
            if (SmartCabinetApplication.notiQueue.size() > 0) {
                final XRfidEntity rfidEntity = SmartCabinetApplication.notiQueue.get(0);
                xApiClient.getProductByRFID(XApiService.this, rfidEntity.getRfid(), new XApiCallBack() {
                    @Override
                    public void response(Object object) {
                        //移除该消息
                        XProductEntity entity = (XProductEntity) object;
                        String content = "";
                        if (rfidEntity.getTag().equals("add")) {
                            content = "[放入]" + entity.getName();
                        } else if (rfidEntity.getTag().equals("remove")) {
                            content = "[取出]" + entity.getName();
                        }
                        String title = rfidEntity.getDevice_name().equals("") ? "智能酒柜" : rfidEntity.getDevice_name();
                        AppManager.noti(XApiService.this, title, content, XRfidDataUtil.HexToInt(rfidEntity.getRfid()));
                        SmartCabinetApplication.notiQueue.remove(rfidEntity);
                    }
                }, new XApiException() {
                    @Override
                    public void error(String error) {
                        //酒款信息获取失败，执行第二次获取
                        notiQueue(rfidEntity);
                    }
                });
            }
        }
    }

    public void notiQueue(final XRfidEntity rfidEntity) {
        synchronized (SmartCabinetApplication.notiQueue) {
            if (SmartCabinetApplication.notiQueue.size() > 0) {
                xApiClient.getProductByRFID(XApiService.this, rfidEntity.getRfid(), new XApiCallBack() {
                    @Override
                    public void response(Object object) {
                        //移除该消息
                        XProductEntity entity = (XProductEntity) object;
                        String content = "";
                        if (rfidEntity.getTag().equals("add")) {
                            content = "[放入]" + entity.getName();
                        } else if (rfidEntity.getTag().equals("remove")) {
                            content = "[取出]" + entity.getName();
                        }
                        String title = rfidEntity.getDevice_name().equals("") ? "智能酒柜" : rfidEntity.getDevice_name();
                        AppManager.noti(XApiService.this, title, content, XRfidDataUtil.HexToInt(rfidEntity.getRfid()));
                        SmartCabinetApplication.notiQueue.remove(rfidEntity);

                    }
                }, new XApiException() {
                    @Override
                    public void error(String error) {
                        //第二次酒款信息获取失败，直接移除
                        SmartCabinetApplication.notiQueue.remove(rfidEntity);
                    }
                });
            }
        }
    }

}
