package com.longshihan.uetooltaichi.xposed;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.ele.uetool.MenuHelper;
import me.ele.uetool.UETMenu;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class UEToolXposed implements IXposedHookLoadPackage {
    private static final String TAG = "UEToolXposed";
    private static final String ACTION = "com.longshihan.uetooltaichi.xposed";
    private Context context;
    private MyBroadcastReceiver myBroadcastReceiver = null;
    public static String currentPackageName;
    private static XC_LoadPackage.LoadPackageParam loadPackageParam;
    private static final String ReceiveACTION="com.longshihan.uetooltaichi.xposed.receive";
    private static final String ACTIONTYPE_Receive="Receive_type";
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        currentPackageName = lpparam.packageName;
        loadPackageParam = lpparam;
        XposedBridge.log(TAG + " >> current package:" + lpparam.packageName);
        try {
//            findAndHookMethod("android.app.Application", lpparam.classLoader, "attach", Context.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            Log.d(TAG, "Hooking Application:attach");
//                            XposedBridge.log(TAG + " >> attach:" + param.args.length);
//                            context = (Context) param.args[0];
//                            startSSL();
//                        }
//                    }
//            );
            XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log(TAG + " >> onCreate:" + param.args.length);
                    context = AndroidAppHelper.currentApplication().getApplicationContext();
                    startWatch();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSSL() {
        Intent intent = new Intent(ReceiveACTION);
        intent.putExtra(ACTIONTYPE_Receive, 100);
        context.sendBroadcast(intent);
        Log.d(TAG, ":" + ":启动SSL广播");
    }

    private void startWatch() {
        if (myBroadcastReceiver != null) {
            stopWatch();
        }
        Log.d(TAG, ":" + ":启动UETool广播");
        IntentFilter intentFilter = new IntentFilter(ACTION);   // 设置广播接收器的信息过滤器，
        myBroadcastReceiver = new MyBroadcastReceiver();
        // 在代码中动态注册广播接收器，intentFilter为这个广播接收器能接收到的广播信息的动作类型，用于过滤广播信息
        context.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void stopWatch() {
        try {
            if (myBroadcastReceiver != null) {
                context.unregisterReceiver(myBroadcastReceiver);
                myBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent1) {
            try {
                int type = intent1.getIntExtra("type", MenuHelper.Type.TYPE_UNKNOWN);
                if (type == MenuHelper.Type.TYPE_EDIT_ATTR
                        || type == MenuHelper.Type.TYPE_LAYOUT_LEVEL
                        || type == MenuHelper.Type.TYPE_RELATIVE_POSITION
                        || type == MenuHelper.Type.TYPE_SHOW_GRIDDING
                        || type == MenuHelper.Type.TYPE_UNKNOWN) {
                    Log.d(TAG, "type:UETool:" + type);
                    UETMenu.open(type);
                } else if (type == 100) {//拿到SSL判断说明
                    boolean isSSL = intent1.getBooleanExtra("isOpenSSL", false);
                    if (isSSL) {
                        Log.d(TAG, "type:SSL:" + type);
                        SSLTrust.initSSLListener(currentPackageName, loadPackageParam,context);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, ":" + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}
