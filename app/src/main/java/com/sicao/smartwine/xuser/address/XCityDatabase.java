package com.sicao.smartwine.xuser.address;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by techssd on 2017/1/6.
 */

public class XCityDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "chinaprovincecityzone.db";
    private static final int DATABASE_VERSION = 1;

    public XCityDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
