package com.sicao.smartwine.xuser.address;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import com.sicao.smartwine.xdata.XUserData;
import com.sicao.smartwine.xhttp.XApiCallBack;
import com.sicao.smartwine.xhttp.XApiException;
import com.sicao.smartwine.xhttp.XApisCallBack;

import java.util.ArrayList;

/***
 * 地址管理页面
 */
public class XAddressListActivity extends SmartCabinetActivity {
    /***
     * 没有收货地址时显示的布局
     */
    LinearLayout mNullLayout;

    /***
     * 收货地址列表
     */
    ListView mAddress;
    /***
     * 收货地址适配器
     */
    ArrayList<XAddressEntity> mList = new ArrayList<>();
    /***
     * 收货地址数据
     */
    AddressAdapter mAdapter;

    @Override
    protected int setView() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("添加地址");
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
//                startActivityForResult(intent, Constants.ADD_USER_ADDRESS);
            }
        });

        mList = new ArrayList<>();
        mAdapter = new AddressAdapter(this, mList);
        mAddress.setAdapter(mAdapter);
        /***
         * 設置默認地址
         */
        mAdapter.setSetListener(new AddressAdapter.SetListener() {
            @Override
            public void setPosition(final int position) {
                XAddressEntity add = mList.get(position);
                swipeRefreshLayout.setRefreshing(true);
                xSicaoApi.configDefaultAddress(XAddressListActivity.this, add.getId(), new XApiCallBack() {
                    @Override
                    public void response(Object object) {
                        swipeRefreshLayout.setRefreshing(false);
                        for (int i = 0; i < mList.size(); i++) {
                            mList.get(i).setIsdefault(false);
                        }
                        mList.get(position).setIsdefault(true);
                        mAdapter.update(mList);
                    }
                }, null);
            }
        });
        /**
         * 删除地址
         */
        mAdapter.setdeleteListener(new AddressAdapter.DeleteListener() {
            @Override
            public void setPosition(int position) {
                XAddressEntity address = mList.get(position);
                deleteAddress(address.getId());
                //与默认地址对校
                if (address.getId().equals(XUserData.getDefaultAddressId(XAddressListActivity.this))) {
                    XUserData.setDefaultAddressId(XAddressListActivity.this, "");
                }
            }
        });
    }

    /***
     * 初始化控件
     */
    private void init() {
        mAddress = (ListView) findViewById(R.id.address_listview);
        mNullLayout = (LinearLayout) findViewById(R.id.address_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取地址
        swipeRefreshLayout.setRefreshing(true);
        getMyAddressList();
    }

    /***
     * 获取我的地址列表数据
     */
    public void getMyAddressList() {
        xSicaoApi.getAddressList(this, new XApisCallBack() {
            @Override
            public <T> void response(ArrayList<T> list) {
                swipeRefreshLayout.setRefreshing(false);
                mList = (ArrayList<XAddressEntity>) list;
                if (mList.size() > 0) {
                    mAdapter.update(mList);
                    mNullLayout.setVisibility(View.GONE);
                } else {
                    mNullLayout.setVisibility(View.VISIBLE);
                }
            }
        }, null);
    }

    /***
     * 删除某一个地址
     *
     * @param ID
     */
    public void deleteAddress(String ID) {
        swipeRefreshLayout.setRefreshing(true);
        xSicaoApi.deleteAddressByID(this, ID, new XApiCallBack() {
            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                getMyAddressList();
            }
        }, new XApiException() {
            @Override
            public void error(String error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(XAddressListActivity.this, "网络异常,请重试!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void message(Message msg) {
        super.message(msg);
        if (msg.what == 7777777) {
            getMyAddressList();
        }
    }
}
