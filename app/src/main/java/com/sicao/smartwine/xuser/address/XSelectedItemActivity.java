package com.sicao.smartwine.xuser.address;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.SmartCabinetActivity;
import java.util.ArrayList;

/**地址选择的调整
 * Created by android on 2016/4/22.
 */
public class XSelectedItemActivity extends SmartCabinetActivity{
    private StringBuilder builder = new StringBuilder();
    private StringBuffer privincenimei=new StringBuffer();
    private StringBuilder city=new StringBuilder();
    private ListView lv_address;
    private SelectAddressAdapter adapter;
    private TextView tv_mata;
    //
    private String CITYSORT = "CityID";
    private String mPorSort = "";
    private String CitySort = "";
    private ArrayList<XCityEntity> province;
    private SQLiteDatabase db;
    private TextView tv_private,tv_city,tv_city_lines,tv_privince_lines;
    XCityDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb=new XCityDatabase(this);
        initView();
    }

    // 控件初始化
    protected void initView() {
        tv_mata=(TextView) findViewById(R.id.tv_mata);
        tv_city=(TextView) findViewById(R.id.tv_city);
        tv_private=(TextView) findViewById(R.id.tv_private);
        tv_city_lines=(TextView) findViewById(R.id.tv_city_lines);
        tv_privince_lines=(TextView)findViewById(R.id.tv_privince_lines);
        lv_address = (ListView) findViewById(R.id.lv_address);// 选择条目
        province = new ArrayList<>();
        db = mDb.getReadableDatabase();
        adapter = new SelectAddressAdapter(province, this);
        lv_address.setAdapter(adapter);
        getPrivince();
        adapter.setOnclicks(new SelectAddressAdapter.onClicks() {
            @Override
            public void setOnclick(int position) {
                if(province.get(position).getId().equals("1")){
                    String name = province.get(position).getProvince();
                    CitySort = province.get(position).getProsort();
                    tv_mata.setText("市区地址");
                    builder.append(name);
                    builder.append(" ");
                    privincenimei.append(name);
                    // 获取城市列表
                    getCity();
                }else if(province.get(position).getId().equals("2")){
                    XCityEntity entity=province.get(position);
                    String name = entity.getProvince();
                    mPorSort = entity.getProsort();
                    tv_mata.setText("区域地址");
                    builder.append(name);
                    builder.append(" ");
                    city.append(name);
                    //获取区域列表
                    getZone();
                }else if(province.get(position).getId().equals("3")){
                    String name = province.get(position).getProvince();
                    CitySort = province.get(position).getProsort();
                    builder.append(name);
                    Intent intent=new Intent();
                    intent.putExtra("address",builder.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    // 得到省市
    public void getPrivince() {
        province.clear();
        String find = "select * from T_Province";
        Cursor cursor = db.rawQuery(find, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                XCityEntity entity = new XCityEntity();
                String name = cursor
                        .getString(cursor.getColumnIndex("ProName"));
                String porsort = cursor.getString(cursor
                        .getColumnIndex("ProSort"));
                entity.setProvince(name);
                entity.setId("1");
                entity.setProsort(porsort);
                province.add(entity);
            }
        }
        cursor.close();
        db.close();
        adapter.upData(province,"","");
    }

    // 获取城市
    public void getCity() {
        province.clear();
        String find = "select * from T_City where ProID='" + CitySort + "'";
        db = mDb.getReadableDatabase();
        Cursor cursor = db.rawQuery(find, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                XCityEntity entity = new XCityEntity();
                String name = cursor.getString(cursor
                        .getColumnIndex("CityName"));
                String porsort = cursor.getString(cursor
                        .getColumnIndex("CitySort"));
                entity.setProvince(name);
                entity.setProsort(porsort);
                entity.setId("2");
                province.add(entity);
            }
            ;
        }
        cursor.close();
        db.close();
        if(!builder.toString().equals("")){
            tv_private.setText(builder.toString());
            tv_private.setVisibility(View.VISIBLE);
            tv_private.setBackgroundColor(Color.parseColor("#f15899"));
            tv_privince_lines.setVisibility(View.VISIBLE);
        }
        adapter.upData(province,builder.toString(),"");
    }

    // 获取区域
    public void getZone() {
        province.clear();
        String find = "select * from T_Zone" + " where " + CITYSORT + "='"
                + mPorSort+"'";
        db = mDb.getReadableDatabase();
        Cursor cursor = db.rawQuery(find, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                XCityEntity entity = new XCityEntity();
                String name = cursor.getString(cursor
                        .getColumnIndex("ZoneName"));
                entity.setProvince(name);
                entity.setId("3");
                province.add(entity);
            }
        }
        cursor.close();
        db.close();
        if(province.size()==0){
            Intent intent=new Intent();
            intent.putExtra("address",builder.toString());
            setResult(RESULT_OK, intent);
            finish();
        }else{
            if(!privincenimei.toString().equals("")){
                tv_private.setText(privincenimei.toString());
                tv_private.setVisibility(View.VISIBLE);
                tv_private.setBackgroundColor(Color.parseColor("#f15899"));
                tv_privince_lines.setVisibility(View.VISIBLE);
            }
            if(!city.toString().equals("")){
                tv_city.setText(city.toString());
                tv_city.setVisibility(View.VISIBLE);
                tv_city.setBackgroundColor(Color.parseColor("#f15899"));
                tv_city_lines.setVisibility(View.VISIBLE);
            }
            adapter.upData(province,privincenimei.toString(),city.toString());
        }
    }
    @Override
    protected int setView() {
        return R.layout.activity_address_item;
    }
}
