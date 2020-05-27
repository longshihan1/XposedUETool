package com.longshihan.uetooltaichi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.longshihan.uetooltaichi.xposed.UEToolXposed;

import me.ele.uetool.MenuHelper;
import me.ele.uetool.UETMenu;
import me.ele.uetool.UETool;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "UEToolXposed";
    TextView open,close,openSSL,closeSSL;
    public static boolean isOpenSSL=false;
    private static final String ReceiveACTION="com.longshihan.uetooltaichi.xposed.receive";
    public static final String ACTION="com.longshihan.uetooltaichi.xposed";
    private static final String ACTIONTYPE_Receive="Receive_type";
    private ReceiveBroadcastReceiver receiveBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open = findViewById(R.id.open);
        close=findViewById(R.id.close);
        openSSL=findViewById(R.id.openSSL);
        closeSSL=findViewById(R.id.closeSSL);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UETool.showUETMenu();
            }
        });
        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UETool.dismissUETMenu();
            }
        });
        openSSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenSSL=true;
            }
        });
        closeSSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenSSL=false;
            }
        });

        initSSL();
    }

    private void initSSL() {
        stop();
        IntentFilter intentFilter = new IntentFilter(ReceiveACTION);
        receiveBroadcastReceiver = new ReceiveBroadcastReceiver();
        registerReceiver(receiveBroadcastReceiver, intentFilter);
    }

    public void stop(){
        try{
            if (receiveBroadcastReceiver!=null){
                unregisterReceiver(receiveBroadcastReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    static class ReceiveBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent1) {
            try {
                int type=intent1.getIntExtra(ACTIONTYPE_Receive,-1);
                Log.d(TAG, ":" + "收听:"+type);
                if (type==100){//通过插件进程，我需要知道现在有没有开启Https证书校验，回复一个广播
                    Intent intent = new Intent(ACTION);
                    intent.putExtra("type", 100);
                    intent.putExtra("isOpenSSL",isOpenSSL);
                    context.sendBroadcast(intent);
                    Log.d(TAG, ":" + "返回启动SSL广播:"+isOpenSSL);
                }
            }catch (Exception e){
                Log.d(TAG,":"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
