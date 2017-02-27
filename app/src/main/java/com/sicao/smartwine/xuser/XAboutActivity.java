package com.sicao.smartwine.xuser;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xapp.AppManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class XAboutActivity extends SmartCabinetActivity {

    GLSurfaceView glSurfaceView;
    //版本号
    TextView mAppVersion;
    RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected int setView() {
        return R.layout.activity_about;
    }

    public void init() {
        mAppVersion = (TextView) findViewById(R.id.version_text);
        mAppVersion.setText("v" + AppManager.getVersionName(this));
        content = (RelativeLayout) findViewById(R.id.surface);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(mRender);
        content.addView(glSurfaceView);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(SmartCabinetApplication.metrics.widthPixels/3,SmartCabinetApplication.metrics.widthPixels/3);
        params.gravity= Gravity.CENTER;
        content.setLayoutParams(params);
    }

    //正方形的8个顶点[6个面]
    float[] data = new float[]{
            -0.25f, -0.25f, 0.25f,//第一个面[正面]
            0.25f, -0.25f, 0.25f,
            -0.25f, 0.25f, 0.25f,
            0.25f, 0.25f, 0.25f,

            0.25f, -0.25f, 0.25f,//第二个面[右侧面]
            0.25f, -0.25f, -0.25f,
            0.25f, 0.25f, 0.25f,
            0.25f, 0.25f, -0.25f,

            0.25f, -0.25f, -0.25f,//第三个面[背面]
            -0.25f, -0.25f, -0.25f,
            0.25f, 0.25f, -0.25f,
            -0.25f, 0.25f, -0.25f,

            -0.25f, -0.25f, -0.25f,//第四个面[左面]
            -0.25f, -0.25f, 0.25f,
            -0.25f, 0.25f, -0.25f,
            -0.25f, 0.25f, 0.25f,

            -0.25f, 0.25f, 0.25f,//第五个面[上面]
            0.25f, 0.25f, 0.25f,
            -0.25f, 0.25f, -0.25f,
            0.25f, 0.25f, -0.25f,

            -0.25f, -0.25f, 0.25f,//第六个面[下面]
            0.25f, -0.25f, 0.25f,
            -0.25f, -0.25f, -0.25f,
            0.25f, -0.25f, -0.25f
    };
    //顶点索引顺序
    short[] index = new short[]{
            0, 1, 2,
            1, 2, 3,

            4, 5, 6,
            5, 6, 7,

            8, 9, 10,
            9, 10, 11,

            12, 13, 14,
            13, 14, 15,

            16, 17, 18,
            17, 18, 19,

            20, 21, 22,
            21, 22, 23

    };

    //  颜色数组
    float[] cubeColors = {
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,

            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,

            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,

            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,


            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,

            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 0f, 1f,
    };
    private GLSurfaceView.Renderer mRender = new GLSurfaceView.Renderer() {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(1, 1, 1, 1);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清理面板
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //装载数据
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer tritex = byteBuffer.asFloatBuffer();
            tritex.put(data);
            tritex.position(0);

            ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(cubeColors.length * 4);
            byteBuffer1.order(ByteOrder.nativeOrder());
            FloatBuffer tritex1 = byteBuffer1.asFloatBuffer();
            tritex1.put(cubeColors);
            tritex1.position(0);

            ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(index.length * 2);
            byteBuffer2.order(ByteOrder.nativeOrder());
            ShortBuffer tritex2 = byteBuffer2.asShortBuffer();
            tritex2.put(index);
            tritex2.position(0);

            //
            gl.glPointSize(8.0f);
            gl.glLoadIdentity();
            gl.glRotatef(rotate, 1f, 1f, 0);
            //
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            //
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, tritex);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, tritex1);
            gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_SHORT, tritex2);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            rotate -= 1;
        }
    };
    float rotate;

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}
