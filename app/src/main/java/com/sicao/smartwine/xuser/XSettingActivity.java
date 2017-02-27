package com.sicao.smartwine.xuser;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xapp.FileUtils;

import java.io.File;
import java.util.List;

public class XSettingActivity extends SmartCabinetActivity implements View.OnClickListener {

    //缓存
    TextView mCache;

    @Override
    protected int setView() {
        return R.layout.activity_xsetting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init(){
        mCache= (TextView) findViewById(R.id.tv_cache);
        mCache.setText(calCache());
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_layout://个人资料
                startActivity(new Intent(XSettingActivity.this, XUserInfoActivity.class));
                break;
            case R.id.manage_address://管理收货地址
//                startActivity(new Intent(XSettingActivity.this, AddressListActivity.class));
                break;
            case R.id.lr_remove_cache://清理缓存
                cleanCache();
                break;
            case R.id.lr_suggest://意见反馈

                break;
            case R.id.lr_about_smart://关于智能酒柜

                break;
        }
    }
    /**
     * 清除缓存
     */
    public void cleanCache() {
        try {
            // 清除数据缓存
            clearCacheFolder(getFilesDir(), System.currentTimeMillis());
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
            long beforeMem = getAvailMemory(this);
            SmartSicaoApi.log("清理前内存---->>>" + beforeMem);
            if (infoList != null) {
                for (int i = 0; i < infoList.size(); ++i) {
                    ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                    // importance 该进程的重要程度 分为几个级别，数值越低就越重要。
                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                    if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        String[] pkgList = appProcessInfo.pkgList;
                        for (int j = 0; j < pkgList.length; ++j) {// pkgList
                            // 得到该进程下运行的包名
                            am.killBackgroundProcesses(pkgList[j]);
                        }
                    }
                }
            }
            long afterMem = getAvailMemory(this);
            SmartSicaoApi.log("清理后内存---->>>" + afterMem);
            deleteDatabase("webview.db");
            deleteDatabase("webview.db-shm");
            deleteDatabase("webview.db-wal");
            deleteDatabase("webviewCache.db");
            deleteDatabase("webviewCache.db-shm");
            deleteDatabase("webviewCache.db-wal");
            clearCacheFolder(getFilesDir(), System.currentTimeMillis());
            if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
                clearCacheFolder(this.getExternalCacheDir(),
                        System.currentTimeMillis());
            }
            mCache.setText("0KB");
        } catch (Exception e) {
        }
    }
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }
    public int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
    // 获取可用内存大小
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        SmartSicaoApi.log("可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem / (1024 * 1024);
    }
    /**
     * 计算缓存大小
     *
     * @return
     */
    private String calCache() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        fileSize += FileUtils.getDirSize(filesDir);
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        return cacheSize;
    }
}
