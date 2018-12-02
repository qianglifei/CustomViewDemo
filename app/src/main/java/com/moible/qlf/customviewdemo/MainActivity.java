 package com.moible.qlf.customviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.moible.qlf.customviewdemo.doubleseekbar.DoubleSeekBar;

 public class MainActivity extends AppCompatActivity {
     private DoubleSeekBar mDoubleSeekBar;
     private String TAG = "QLF";

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mDoubleSeekBar = findViewById(R.id.doubleSeek);

         mDoubleSeekBar.setOnScaleListener(new DoubleSeekBar.OnScaleListener() {
             @Override
             public void getDoubleSeekValue(String strLow, String strHigh) {
                 Log.i(TAG, "===strLow: " + strLow);
                 Log.i(TAG, "===strHigh: " + strHigh);
             }
         });
    }
}
