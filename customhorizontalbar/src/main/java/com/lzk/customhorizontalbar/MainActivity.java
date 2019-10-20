package com.lzk.customhorizontalbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MainActivity extends AppCompatActivity {

    private HorizontalBar mHorizontalBar;
    private TagHorizontalBar mTagHorizontalBar;
    private int value = 0;
    private int value1 = 0;
    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    value += 1;
                    mHorizontalBar.setUI(value,100,true);
                    if (value < 100){
                        sendEmptyMessageDelayed(0,0);
                    }
                    break;
                case 1:
                    value1 += 5;
                    mTagHorizontalBar.setUI(value1,100,"同比增长");
                    if (value1 < 100){
                        sendEmptyMessageDelayed(1,500);
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHorizontalBar = findViewById(R.id.main_hb);
        mHandler.sendEmptyMessageDelayed(0,1*1000);
        mTagHorizontalBar = findViewById(R.id.main_tag_hb);
        mHandler.sendEmptyMessageDelayed(1,1000);
    }
}