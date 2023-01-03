package com.sunmi.pda.utils;



import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.log.LogUtils;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler mAppCrashHandler;
    //系统默认的处理器
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private CrashHandler() {
    }

    public void initCrashHandler() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置系统
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        if (mAppCrashHandler == null) {
            mAppCrashHandler = new CrashHandler();
        }
        return mAppCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 回调函数，处理异常
        // 在这里将崩溃日志读取出来，然后保存到SD卡，或者直接上传到日志服务器
        // 注意 保存或者上传到日志服务器需要将系统版本，手机唯一标识等系统信息和崩溃信息一同上传，方便分析crash问题
        // 如果用户没有处理则让系统默认的异常处理器来处理
        LogUtils.d("CrashHandler", "ex--->" + AppUtil.getVersionName(AndroidApplication.getInstance()) + "----->"+ ex.getMessage());
    }
}
