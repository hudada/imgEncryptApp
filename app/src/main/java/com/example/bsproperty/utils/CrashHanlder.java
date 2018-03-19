package com.example.bsproperty.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.ui.CrashDialogActivity;
import com.litesuits.common.io.FileUtils;
import com.litesuits.common.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by John on 2018/3/14.
 */

public class CrashHanlder implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHanlder INSTANCE = new CrashHanlder();
    // 程序的Context对象
    private Context mContext;

    private CrashHanlder() {
    }

    public static CrashHanlder getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 异常捕获
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 跳转到崩溃提示Activity
            Intent intent = new Intent(mContext, CrashDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            MyApplication.getInstance().clearAct();
            System.exit(0);// 关闭已奔溃的app进程
        }
    }

    /**
     * 自定义错误捕获
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 收集错误信息
        getCrashInfo(ex);

        return true;
    }

    /**
     * 收集错误信息
     *
     * @param ex
     */
    private void getCrashInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String errorMessage = writer.toString();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String mFilePath = Environment.getExternalStorageDirectory() + "/" + MyApplication.ERROR_FILENAME;
            try {
                String rr = FileUtils.readFileToString(new File(mFilePath));
                try {
                    FileUtils.writeStringToFile(new File(mFilePath), rr + "\n" + errorMessage);
                } catch (IOException e1) {
                }
            } catch (IOException e) {
                try {
                    FileUtils.writeStringToFile(new File(mFilePath), errorMessage);
                } catch (IOException e1) {
                }
            }

//            FileTxt.WirteTxt(mFilePath, FileTxt.ReadTxt(mFilePath) + '\n' + errorMessage);
        } else {
            Log.i(MyApplication.TAG, "noSD");
        }

    }

}
