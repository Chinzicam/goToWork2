package com.czc.gotowork.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 封装：读写薪资配置
 */
public class Prefs {

    private static final String NAME = "gotowork_prefs";
    private final SharedPreferences mSp;

    public Prefs(Context ctx) {
        mSp = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public double getDouble(String key, double def) {
        return Double.longBitsToDouble(mSp.getLong(key, Double.doubleToRawLongBits(def)));
    }

    public void setDouble(String key, double val) {
        mSp.edit().putLong(key, Double.doubleToRawLongBits(val)).apply();
    }

    public int getInt(String key, int def) {
        return mSp.getInt(key, def);
    }

    public void setInt(String key, int val) {
        mSp.edit().putInt(key, val).apply();
    }

    public boolean isConfigured() {
        return mSp.contains("salary_month");
    }

    public void clear() {
        mSp.edit().clear().apply();
    }
}
