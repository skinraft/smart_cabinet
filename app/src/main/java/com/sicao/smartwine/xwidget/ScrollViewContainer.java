package com.sicao.smartwine.xwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.Timer;
import java.util.TimerTask;

/***
 * 继续拖动查看图文详情控件
 */
public class ScrollViewContainer extends RelativeLayout {
    /**
     * /** 自动上滑
     */
    public static final int AUTO_UP = 0;
    /**
     * 自动下滑
     */
    public static final int AUTO_DOWN = 1;
    /**
     * 动画完成
     */
    public static final int DONE = 2;
    /**
     * 动画速度
     */
    public static final float SPEED = 6.5f;

    private boolean isMeasured = false;

    /**
     * 用于计算手滑动的速度
     */
    private VelocityTracker vt;

    private int mViewHeight;
    private int mViewWidth;

    private View topView;
    private View bottomView;

    private boolean canPullDown;
    private boolean canPullUp;
    private int state = DONE;

    /**
     * 记录当前展示的是哪个view，0是topView，1是bottomView
     */
    private int mCurrentViewIndex = 0;
    /**
     * 手滑动距离，这个是控制布局的主要变量
     */
    private float mMoveLen;
    private MyTimer mTimer;
    private float mLastY;
    /**
     * 用于控制是否变动布局的另一个条件，mEvents==0时布局可以拖拽了，mEvents==-1时可以舍弃将要到来的第一个move事件，
     * 这点是去除多点拖动剧变的关键
     */
    private int mEvents;

    Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mMoveLen != 0) {
                if (state == AUTO_UP) {
                    mMoveLen -= SPEED;
                    if (mMoveLen <= -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        state = DONE;
                        mCurrentViewIndex = 1;
                        mContext.sendBroadcast(new Intent("SCROLL_CONTENT_GOODS_INFO").putExtra("index", mCurrentViewIndex));
                    }
                } else if (state == AUTO_DOWN) {
                    mMoveLen += SPEED;
                    if (mMoveLen >= 0) {
                        mMoveLen = 0;
                        state = DONE;
                        mCurrentViewIndex = 0;
                        mContext.sendBroadcast(new Intent("SCROLL_CONTENT_GOODS_INFO").putExtra("index", mCurrentViewIndex));
                    }
                } else {
                    mTimer.cancel();
                }
            }
            requestLayout();
        }

    };

    public ScrollViewContainer(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        mTimer = new MyTimer(handler);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (vt == null)
                        vt = VelocityTracker.obtain();
                    else
                        vt.clear();
                    mLastY = ev.getY();
                    vt.addMovement(ev);
                    mEvents = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:// 多一只手指按下或抬起时舍弃将要到来的第一个事件move，防止多点拖拽的bug
                case MotionEvent.ACTION_POINTER_UP:
                    mEvents = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    vt.addMovement(ev);
                    if (canPullUp && mCurrentViewIndex == 0 && mEvents == 0) {
                        mMoveLen += (ev.getY() - mLastY);
                        if (mMoveLen > 0) {
                            mMoveLen = 0;
                            mCurrentViewIndex = 0;
                        } else if (mMoveLen < -mViewHeight) {
                            mMoveLen = -mViewHeight;
                            mCurrentViewIndex = 1;

                        }
                        if (mMoveLen < -8) {
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    } else if (canPullDown && mCurrentViewIndex == 1
                            && mEvents == 0) {
                        mMoveLen += (ev.getY() - mLastY);
                        if (mMoveLen < -mViewHeight) {
                            mMoveLen = -mViewHeight;
                            mCurrentViewIndex = 1;
                        } else if (mMoveLen > 0) {
                            mMoveLen = 0;
                            mCurrentViewIndex = 0;
                        }
                        if (mMoveLen > 8 - mViewHeight) {
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    } else
                        mEvents++;
                    mLastY = ev.getY();
                    requestLayout();
                    break;
                case MotionEvent.ACTION_UP:
                    mLastY = ev.getY();
                    vt.addMovement(ev);
                    vt.computeCurrentVelocity(700);
                    float mYV = vt.getYVelocity();
                    if (mMoveLen == 0 || mMoveLen == -mViewHeight)
                        break;
                    if (Math.abs(mYV) < 500) {
                        if (mMoveLen <= -mViewHeight / 2) {
                            state = AUTO_UP;
                        } else if (mMoveLen > -mViewHeight / 2) {
                            state = AUTO_DOWN;
                        }
                    } else {
                        if (mYV < 0)
                            state = AUTO_UP;
                        else
                            state = AUTO_DOWN;
                    }
                    mTimer.schedule(2);
                    try {
                        vt.recycle();
                        vt = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
            super.dispatchTouchEvent(ev);
            return true;
        } catch (Exception e) {
            mEvents = 0;
            return false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            topView.layout(0, (int) mMoveLen, mViewWidth,
                    topView.getMeasuredHeight() + (int) mMoveLen);
            mViewHeight = topView.getMeasuredHeight();
            bottomView.layout(0, mViewHeight + (int) mMoveLen,
                    mViewWidth, mViewHeight + (int) mMoveLen
                            + bottomView.getMeasuredHeight());
            //判断对比大小
            if (this.getMeasuredHeight() > bottomView.getMeasuredHeight()) {
                canPullDown = true;
            }
        } catch (Exception e) {
            mEvents = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (!isMeasured) {
                isMeasured = true;

                mViewHeight = getMeasuredHeight();
                mViewWidth = getMeasuredWidth();

                topView = getChildAt(0);
                bottomView = getChildAt(1);

                bottomView.setOnTouchListener(bottomViewTouchListener);
                topView.setOnTouchListener(topViewTouchListener);
            }
        } catch (Exception e) {
            mEvents = 0;
        }
    }

    private OnTouchListener topViewTouchListener = new OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                ScrollView sv = (ScrollView) v;
                sv.setFocusable(true);
                sv.setFocusableInTouchMode(true);
                sv.requestFocus();
                if (sv.getScrollY() == (sv.getChildAt(0).getMeasuredHeight() - sv
                        .getMeasuredHeight()) && mCurrentViewIndex == 0) {
                    canPullUp = true;

                } else {
                    canPullUp = false;
                }
            } catch (Exception e) {
                mEvents = 0;
                canPullUp = false;
            }
            return false;
        }
    };
    @SuppressLint("ClickableViewAccessibility")
    private OnTouchListener bottomViewTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                ScrollView sv = (ScrollView) v;
                sv.setFocusable(true);
                sv.setFocusableInTouchMode(true);
                sv.requestFocus();
                if (sv.getScrollY() == 0 && mCurrentViewIndex == 1) {
                    canPullDown = true;
                } else {
                    canPullDown = false;
                }
            } catch (Exception e) {
                mEvents = 0;
                canPullUp = false;
            }
            return false;
        }
    };

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

}
