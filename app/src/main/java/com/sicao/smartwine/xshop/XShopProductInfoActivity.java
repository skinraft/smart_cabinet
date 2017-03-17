package com.sicao.smartwine.xshop;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.SmartCabinetApplication;
import com.sicao.smartwine.xdevice.entity.XProductEntity;
import com.sicao.smartwine.xdevice.entity.XWineEntity;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xwidget.CircularBannerView;
import com.sicao.smartwine.xwidget.TWebView;
import com.sicao.smartwine.xwidget.XBannerEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/***
 * 商品详情
 */
public class XShopProductInfoActivity extends SmartCabinetActivity {

    //图片轮播
    CircularBannerView circularBannerView;
    //酒款的名字
    TextView mWineName;
    //酒款的一句话概述
    TextView mWineDestail;
    //酒款的价格
    TextView mWinePrice;
    //酒款的介绍
    TWebView webView;
    //商品ID
    String productID = "";
    //商品详情
    XProductEntity xWineEntity;

    @Override
    protected int setView() {
        return R.layout.activity_xshop_product_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    void init() {
        productID = getIntent().getExtras().getString("productID");
        circularBannerView = (CircularBannerView) findViewById(R.id.image_banner);
        mWineName = (TextView) findViewById(R.id.good_name);
        mWineDestail= (TextView) findViewById(R.id.good_destail);
        mWinePrice = (TextView) findViewById(R.id.price);
        webView = (TWebView) findViewById(R.id.web_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                SmartCabinetApplication.metrics.widthPixels, SmartCabinetApplication.metrics.widthPixels);
        circularBannerView.setLayoutParams(params);
        circularBannerView.setQueueNext(false);
        getProductInfo();
        mRightText.setText("分享");
        mRightText.setVisibility(View.GONE);
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != xWineEntity.getShare())
                    xCabinetApi.shareProduct(new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            Toast("分享成功");
                        }
                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            Toast("分享中断，请重试");
                        }
                        @Override
                        public void onCancel(Platform platform, int i) {
                            Toast("您已取消分享");
                        }
                    }, xWineEntity.getShare());
            }
        });
    }

    void getProductInfo() {
        xSicaoApi.getProductInfo(this, productID, new XApiCallBack() {
            @Override
            public void response(Object object) {
                xWineEntity = (XProductEntity) object;
                mWineName.setText(xWineEntity.getName());
                mWinePrice.setText("￥"+xWineEntity.getCurrent_price());
                mWineDestail.setText(xWineEntity.getDescription());
                String content = xWineEntity.getBrief();
                if (null != xWineEntity && null != xWineEntity.getImgs()
                        && xWineEntity.getImgs().length > 0) {
                    String[] subimgs = xWineEntity.getImgs();
                    XBannerEntity[] banners = new XBannerEntity[subimgs.length];
                    for (int i = 0; i < subimgs.length; i++) {
                        XBannerEntity entity = new XBannerEntity();
                        entity.setCover_image(subimgs[i]);
                        banners[i] = entity;
                    }
                    circularBannerView.setImageResouce(banners, null);
                    circularBannerView.adapter.notifyDataSetChanged();
                }
                if (content != null) {
                    // 获取本地html文件并将内容填充到html文件中
                    StringBuilder builder = new StringBuilder();
                    builder.append("<!DOCTYPE html>");
                    builder.append("<html>");
                    builder.append("<!DOCTYPE html><head>");
                    builder.append("<meta charset='utf-8'>");
                    builder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=2, minimum-scale=1, user-scalable=yes'><style> img {max-width:100%; box-sizing: border-box; vertical-align: middle;}</style></head>");
                    builder.append("<body><div class='content'><div id='ptj_mobile_content'></div><script type='text/javascript'>function openActivity(type,id){window.Android.openActivity(type,id);}</script></div></body></html>");
                    Document docString = Jsoup.parse(builder.toString());
                    Element e = docString.getElementById("ptj_mobile_content");
                    e.append(content);
                    webView.loadDataWithBaseURL("", docString.html(), "text/html",
                            "utf-8", null
                    );
                }
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                Toast(error);
                finish();
            }
        });
    }
}
