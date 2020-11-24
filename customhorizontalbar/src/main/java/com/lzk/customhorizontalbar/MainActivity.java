package com.lzk.customhorizontalbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private HorizontalBar mHorizontalBar;
    private TagHorizontalBar mTagHorizontalBar;
    private ElectricityView mElectricityView;
    private Button mMainRandomBtn;
    private Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHorizontalBar = findViewById(R.id.main_hb);
        mTagHorizontalBar = findViewById(R.id.main_tag_hb);
        mElectricityView = findViewById(R.id.main_elect_view);

        mMainRandomBtn = findViewById(R.id.main_random_btn);

        mMainRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mRandom.nextInt(100);
                mHorizontalBar.setUI(progress,100,true);
                mTagHorizontalBar.setUI(progress,100,"同比增长");
                mElectricityView.setProgress(progress);
            }
        });
    }
}
