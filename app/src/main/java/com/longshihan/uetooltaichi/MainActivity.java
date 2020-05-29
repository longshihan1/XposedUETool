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
        initSSL();
    }

    private void initSSL() {
        try {
            stop();
            IntentFilter intentFilter = new IntentFilter(ReceiveACTION);
            receiveBroadcastReceiver = new ReceiveBroadcastReceiver();
            registerReceiver(receiveBroadcastReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }

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
            }catch (Exception e){
                Log.d(TAG,":"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
