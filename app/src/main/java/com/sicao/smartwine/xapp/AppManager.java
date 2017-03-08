package com.sicao.smartwine.xapp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xdevice.SmartCabinetRFIDActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

/***
 * <p>
 * 应用管理
 * <p>
 * <a> 提供功能:为应用程序的包名,版本�?版本名称,以及安装应用,启动应用,卸载应用和正在运行的应用提供相关信息<br>
 *
 * @author li'mingqi
 */
public class AppManager {
    /**
     * 获取程序包信�?
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return pi;
    }

    /***
     * 获取内部版本号
     *
     * @param context 上下文对�?
     * @return 版本�?
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /***
     * 获取版本名称
     *
     * @param context 上下文对�?
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 程序包名
     *
     * @param context 上下文对�?
     * @return 程序包名
     */
    public static String getPackageName(Context context) {
        String pkgName = getPackageInfo(context).packageName;
        return pkgName;
    }

    /***
     * 卸载应用
     *
     * @param pkName  应用包名
     * @param context 上下文对�?
     * @return
     */
    public static boolean uninstall(String pkName, Context context) {
        Uri uri = Uri.parse("package:" + pkName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
        return true;
    }


    /***
     * 获取正在运行的应用的进程名称
     *
     * @param pkgName
     * @param context
     * @return
     */
    public static String getProcessName(String pkgName, Context context) {
        String procName = pkgName;
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的�?��应用程序�?
            for (int i = 0; i < pkgNameList.length; i++) {
                if (pkgName.equalsIgnoreCase(pkgNameList[i])) {
                    procName = appProcess.processName;
                    break;
                }
            }
        }
        if (procName == null)
            procName = pkgName;
        return procName;
    }

    /***
     * 安装apk
     *
     * @param context 上下文对�?
     * @param file    新版本apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行的数据类�?
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /***
     * 判断应用是否获取了某一项权限
     *
     * @param context    上下文对象
     * @param permission 权限名
     * @return 是否已获取该权限
     */
    public static boolean hasPermission(Context context, String permission) {
        PackageManager manager = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == manager.checkPermission(
                permission, getPackageName(context)));
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    // 获取android手机串号
    public static String getImei(Context context) {
        String imi = "";
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imi = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            imi = "获取手机串号异常";
        }
        return imi;
    }

    /***
     * 判断某个服务是否正在运行【信鸽推送辅助】
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        SmartSicaoApi.log("运行的進程---" + AppManager.getProcessName(AppManager.getPackageName(context), context));
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                SmartSicaoApi.log("运行的服务---" + service.service.getClassName());
                return true;
            }
        }
        return false;
    }

    /***
     * 获取手机MAC地址
     * @return
     */
    public static String getMac() {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return macSerial;
    }

    public static void noti(Context context, GizWifiDevice device, String content, int notifyid) {
        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String title = device.getRemark().equals("") ? "智能酒柜" : device.getRemark();
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher);
// Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(context, SmartCabinetRFIDActivity.class);
        notifyIntent.putExtra("cabinet", device);
// Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
// Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent);
// Notifications are issued by sending them to the
// NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds an anonymous Notification object from the builder, and
// passes it to the NotificationManager
        mNotificationManager.notify(notifyid, builder.build());
    }

    /***
     * 启动某一个应用
     * @param context
     * @param packageName
     * @param className
     */
    public static void openApp(Context context, String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        context.startActivity(intent);
    }
}
