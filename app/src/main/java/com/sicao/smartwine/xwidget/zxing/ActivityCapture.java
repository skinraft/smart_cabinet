package com.sicao.smartwine.xwidget.zxing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizDeviceSharing;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartSicaoApi;
import com.sicao.smartwine.xapp.AppManager;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xdevice.SmartCabinetBindStatusActivity;
import com.sicao.smartwine.xdevice.SmartCabinetDeviceListActivity;
import com.sicao.smartwine.xwidget.zxing.camera.CameraManager;
import com.sicao.smartwine.xwidget.zxing.decoding.CaptureActivityHandler;
import com.sicao.smartwine.xwidget.zxing.decoding.InactivityTimer;
import com.sicao.smartwine.xwidget.zxing.view.RGBLuminanceSource;
import com.sicao.smartwine.xwidget.zxing.view.ViewfinderView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.Vector;

/***
 * 扫码页面</br>
 * <ol>
 * <li>扫码结果 {@link ActivityCapture#onResultHandler(String, Bitmap)}
 * 方法,请在该方法内处理结果;
 * </ol>
 */
public class ActivityCapture extends SmartCabinetActivity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    /**
     * 扫描相册中的二维码
     **/
    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private Bitmap scanBitmap;

    @Override
    protected int setView() {
        return R.layout.activity_device_scan;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(getApplication());
        // 扫描框
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        //相册
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("相册");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                ActivityCapture.this.startActivityForResult(wrapperIntent,
                        REQUEST_CODE);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PARSE_BARCODE_SUC:// 从相册选择图片扫描成功
                    onResultHandler((String) msg.obj, scanBitmap);
                    break;
                case PARSE_BARCODE_FAIL:// 从相册选择图片扫描失败
                    Toast.makeText(ActivityCapture.this, (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;

            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    // 获取选中图片的路径
                    Uri uri = data.getData();
                    if (uri != null) {
                        final Bitmap bm = decodeUriAsBitmap(uri);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Result result = scanningImage(bm);
                                if (result != null) {
                                    Message m = mHandler.obtainMessage();
                                    m.what = PARSE_BARCODE_SUC;
                                    m.obj = result.getText();
                                    mHandler.sendMessage(m);
                                } else {
                                    Message m = mHandler.obtainMessage();
                                    m.what = PARSE_BARCODE_FAIL;
                                    m.obj = "请放入正确的二维码";
                                    mHandler.sendMessage(m);
                                }
                            }
                        }).start();

                    } else {
                        Toast.makeText(ActivityCapture.this, "扫描失败,请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 扫描二维码图片的方法
     *
     * @return
     */
    public Result scanningImage(Bitmap bitmap) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码
        scanBitmap = bitmap;
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = "UTF-8";

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        onResultHandler(resultString, barcode);
    }

    /**
     * 处理扫描结果
     *
     * @param resultString 扫描内容
     * @param bitmap       图片
     */
    private void onResultHandler(String resultString, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(ActivityCapture.this, "神马也木有撒?", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        /***
         * 处理扫面结果
         * http://www.gizwits.com?product_key=cd688469b42446e99a6130c9550c9ee7&did=D2vcp7K6LnqMZK9SFBZr6i&passcode=123456
         */
        SmartSicaoApi.log(resultString);
        if (resultString.contains("product_key=") && resultString.contains("did=") && resultString.contains("passcode=")) {
            showProgress(true);
            GizWifiSDK.sharedInstance().bindDevice(XUserData.getCabinetUid(this), XUserData.getCabinetToken(this), getParamFomeUrl(resultString, "did"),
                    getParamFomeUrl(resultString, "passcode"), null);
        } else if(resultString.contains("type=share")&&resultString.contains("code=")){
            //分享过来的二維碼
            //type=share&code=5c5829aea65f4ced98b9a73a0668683e
            code=getParamFomeUrl(resultString, "code");
            GizDeviceSharing.checkDeviceSharingInfoByQRCode(XUserData.getCabinetToken(this),code);
            showProgress(true);
            mHintText.setVisibility(View.VISIBLE);
            mHintText.setText("正在识别二维码...");
        }else{
            finish();
            Toast.makeText(this, "抱歉,无法识别该二维码", Toast.LENGTH_LONG).show();
        }
    }

    String code="";
    /***
     * 检测二维码OK
     */
    @Override
    public void checkShareingCodeSuccess() {
        GizDeviceSharing.acceptDeviceSharingByQRCode(XUserData.getCabinetToken(this),code);
        showProgress(true);
        mHintText.setText("二维码识别成功，正在绑定设备...");
    }
    /***
     * 检测二维码失败
     */
    @Override
    public void checkShareIngCodeError(String result) {
        showProgress(false);
        startActivity(new Intent(this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
        finish();
    }
    /***
     * 绑定二维码分享的设备OK
     */
    @Override
    public void acceptShareingByCodeSuccess() {
        showProgress(false);
        startActivity(new Intent(this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
        finish();
    }
    /***
     * 绑定二维码分享的设备失败
     */
    @Override
    public void acceptShareIngByCodeError(String result) {
        showProgress(false);
        startActivity(new Intent(this, SmartCabinetBindStatusActivity.class).putExtra("status", "2"));
        finish();
    }
    @Override
    public void bindSuccess(String did) {
        super.bindSuccess(did);
        /**
         * 绑定OK
         */
        showProgress(false);
        Toast.makeText(this, "操作成功!", Toast.LENGTH_LONG).show();
        XUserData.setCurrentCabinetId(this,did);
        startActivity(new Intent(ActivityCapture.this, SmartCabinetBindStatusActivity.class).putExtra("status", "1"));
        finish();
    }
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 当用户从相册选择图片时获取的uri转成Bitmap
     *
     * @param uri
     * @return Bitmap
     */

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri));
            bitmap = readBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap readBitmap(Bitmap bitmap) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图 565代表对应三原色占的位数
        opt.inInputShareable = true;
        opt.inPurgeable = true;// 设置图片可以被回收
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, opt);
    }

    private String getParamFomeUrl(String url, String param) {
        String product_key = "";
        int startindex = url.indexOf(param + "=");
        startindex += (param.length() + 1);
        String subString = url.substring(startindex);
        int endindex = subString.indexOf("&");
        if (endindex == -1) {
            product_key = subString;
        } else {
            product_key = subString.substring(0, endindex);
        }
        return product_key;
    }
}