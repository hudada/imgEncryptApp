package com.example.bsproperty;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.example.bsproperty.utils.CrashHanlder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by yezi on 2018/1/27.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    public final static String TAG = "x024";
    public final static String ERROR_FILENAME = "x024_error.log";
    private static ArrayList<AppCompatActivity> mActList;

    @Override
    public void onCreate() {
        super.onCreate();
        mActList = new ArrayList<>();
        CrashHanlder.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();

        }
        return instance;
    }

    public void addAct(AppCompatActivity activity){
        mActList.add(activity);
    }

    public void removeAct(AppCompatActivity activity){
        activity.finish();
        mActList.remove(activity);
    }

    public void clearAct(){
        for (AppCompatActivity appCompatActivity : mActList) {
            appCompatActivity.finish();
        }
    }
}
