package com.longshihan.uetooltaichi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import me.ele.uetool.UETool;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "UEToolXposed";
    TextView open,close;

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

    }

}
