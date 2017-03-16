package com.sicao.smartwine.xwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetApplication;

/**
 * 广告轮播
 *
 * @author putaoji
 */
public class CircularBannerView extends RelativeLayout {
    private Context context;
    private XBannerEntity[] imageUrl = null;
    private String[] imageDesc = null;
    public MyAdapter adapter;
    private TextView image_desc;
    private LinearLayout pointGroup;
    protected int lastPosition = 0;
    private LinearLayout ll_bottom;
    private int circle_res = R.drawable.point_bg;
    private ViewPager vp;
    // 是否自动轮播（默认自动轮播）
    private boolean queueNext = true;
    private boolean isOneLoad = true;
    // Banner是否可点击（默认可点击）
    private boolean clickable = true;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    vp.setCurrentItem(vp.getCurrentItem() + 1);
                    handler.sendEmptyMessageDelayed(0, 4000);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public CircularBannerView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public CircularBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public CircularBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView(context);
    }

    public void setImageUrl(XBannerEntity[] imageUrl) {
        if (imageUrl != null && imageUrl.length > 0) {
            vp.removeAllViews();
            pointGroup.removeAllViews();
            vp.invalidate();
            pointGroup.invalidate();
            this.imageUrl = imageUrl;
            setPoints(imageUrl.length);
            adapter.notifyDataSetChanged();
        }
    }

    public void setImageDesc(String[] imageDesc) {
        if (imageDesc != null && imageDesc.length > 0) {
            image_desc.setVisibility(View.VISIBLE);
            this.imageDesc = imageDesc;
            adapter.notifyDataSetChanged();
            image_desc.setText(imageDesc[0]);
        }
    }

    public void setDescTextColor(int color) {
        if (image_desc.getVisibility() == View.VISIBLE) {
            image_desc.setTextColor(color);
        }
    }

    public void setBottomBkColor(int color) {
        ll_bottom.setBackgroundColor(color);
    }

    public void setCircleSelector(int res) {
        circle_res = res;
    }

    public void setImageResouce(XBannerEntity imageurl[], String imagedesc[]) {
        vp.removeAllViews();
        pointGroup.removeAllViews();
        vp.invalidate();
        pointGroup.invalidate();
        setImageUrl(imageurl);
        adapter = new MyAdapter();
        vp.setAdapter(adapter);
        lastPosition = 0;// 复位
        setImageDesc(imagedesc);
        if (imageurl.length > 1 && isQueueNext() && isOneLoad) {
            handler.sendEmptyMessage(0);
            isOneLoad = false;
        }
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isClickable() {
        return clickable;
    }

    @SuppressLint("InflateParams")
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.circularbanner, null);
        vp = (ViewPager) view.findViewById(R.id.viewpager);
        image_desc = (TextView) view.findViewById(R.id.image_desc);
        pointGroup = (LinearLayout) view.findViewById(R.id.point_group);
        ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        imageUrl = new XBannerEntity[]{};
        imageDesc = new String[]{};
        adapter = new MyAdapter();
        vp.setAdapter(adapter);
        vp.setCurrentItem(Integer.MAX_VALUE / 2);
        vp.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (imageUrl.length > 0) {
                    position = position % imageUrl.length;
                    if (imageDesc.length > 0) {
                        image_desc.setText(imageDesc[position]);
                    }
                    pointGroup.getChildAt(position).setEnabled(true);
                    pointGroup.getChildAt(lastPosition).setEnabled(false);
                    lastPosition = position;
                    pointGroup.invalidate();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        addView(view);
    }

    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final SimpleDraweeView imageView = new SimpleDraweeView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    SmartCabinetApplication.metrics.widthPixels,
                    (SmartCabinetApplication.metrics.widthPixels * 16 / 25));
            imageView.setLayoutParams(params);
            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            hierarchy.setPlaceholderImage(R.mipmap.ic_launcher);
            PointF point = new PointF(0.5f, 0.5f);
            hierarchy.setActualImageFocusPoint(point);
            final int index = position;
            if (imageUrl.length > 0) {
                int b = index % imageUrl.length;
                XBannerEntity a = imageUrl[b];
                Uri uri = Uri.parse(a.getCover_image());
                imageView.setImageURI(uri);
            }
            container.addView(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // 点击事件
                        XBannerEntity banner = imageUrl[index % imageUrl.length];
//                        context.startActivity(new Intent(context, XBBSActivity.class).putExtra("xbbsID",banner.getId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void setPoints(int count) {
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 5;
            params.leftMargin = 5;
            point.setLayoutParams(params);
            if (count > 1) {
                point.setBackgroundResource(circle_res);
            }
            if (i == 0) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
            pointGroup.addView(point);
        }
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    public boolean isQueueNext() {
        return queueNext;
    }

    public void setQueueNext(boolean queueNext) {
        this.queueNext = queueNext;
    }
}
