package com.longshihan.uetooltaichi.xposed;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.ele.uetool.MenuHelper;
import me.ele.uetool.UETMenu;

public class UEToolXposed implements IXposedHookLoadPackage {
    private static final String TAG="UEToolXposed";
    private static final String ACTION="com.longshihan.uetooltaichi.xposed";
    private Context context;
    private int visibleActivityCount;
    private MyBroadcastReceiver myBroadcastReceiver=null;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log(TAG+" >> current package:" + lpparam.packageName);
        try {
            XposedHelpers.findAndHookMethod(Application.class,"onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log(TAG+" >> onCreate package:" + param.args.length);
                    context = AndroidAppHelper.currentApplication().getApplicationContext();
                    if (context.getApplicationContext() instanceof Application){
                        XposedBridge.log(TAG+" >>yes:");
                    }
                    startWatch();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                        @Override
                        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                            XposedBridge.log(TAG+" >> registerActivityLifecycleCallbacks:onActivityCreated:" +activity.getLocalClassName());
                            try{
                                if (activity instanceof FragmentActivity){
                                    ((FragmentActivity)activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(
                                            new FragmentManager.FragmentLifecycleCallbacks() {

                                                @Override
                                                public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
                                                    super.onFragmentCreated(fm, f, savedInstanceState);

                                                }

                                                @Override
                                                public void onFragmentStopped(FragmentManager fm, Fragment f) {
                                                    super.onFragmentStopped(fm, f);
//                                                    dismiss(activity);
                                                }
                                            }, true);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                            XposedBridge.log(TAG+" >> registerActivityLifecycleCallbacks:onActivityStarted:" +activity.getLocalClassName());
                            visibleActivityCount++;
                            if (visibleActivityCount == 1){
                                stopWatch(activity);
                                startWatch(activity);
                            }
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {
                        }

                        @Override
                        public void onActivityStopped(Activity activity) {
                            XposedBridge.log(TAG+" >> registerActivityLifecycleCallbacks:onActivityStopped:" +activity.getLocalClassName());
                            visibleActivityCount--;
                            if (visibleActivityCount == 0 ){
                                stopWatch(activity);
                            }
                            dismiss(activity);
                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                        }
                    });
                        }
                    },2000);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startWatch(){
        if (myBroadcastReceiver!=null){
            stopWatch();
        }
        Log.d(TAG,":"+":启动广播1");
        IntentFilter intentFilter = new IntentFilter(ACTION);   // 设置广播接收器的信息过滤器，
        myBroadcastReceiver = new MyBroadcastReceiver();
        // 在代码中动态注册广播接收器，intentFilter为这个广播接收器能接收到的广播信息的动作类型，用于过滤广播信息
        context.registerReceiver(myBroadcastReceiver, intentFilter);
    }
    private void startWatch(Activity activity){
        Log.d(TAG,":"+activity.getLocalClassName()+":启动广播");
        if (myBroadcastReceiver!=null){
            stopWatch(activity);
        }
        IntentFilter intentFilter = new IntentFilter(ACTION);   // 设置广播接收器的信息过滤器，
        myBroadcastReceiver = new MyBroadcastReceiver();
//         在代码中动态注册广播接收器，intentFilter为这个广播接收器能接收到的广播信息的动作类型，用于过滤广播信息
        activity.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void stopWatch(){
        try {
            if (myBroadcastReceiver!=null) {
                context.unregisterReceiver(myBroadcastReceiver);
                myBroadcastReceiver=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void stopWatch(Activity activity){
        try {
            if (myBroadcastReceiver!=null) {
                activity.unregisterReceiver(myBroadcastReceiver);
                myBroadcastReceiver=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent1) {
            try {
                int type=intent1.getIntExtra("type", MenuHelper.Type.TYPE_UNKNOWN);
                Log.d(TAG,":"+type);
                UETMenu.open(type);
            }catch (Exception e){
                Log.d(TAG,":"+e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private void dismiss(Activity currentTopActivity){
        UETMenu.dismiss(currentTopActivity);
    }
}
