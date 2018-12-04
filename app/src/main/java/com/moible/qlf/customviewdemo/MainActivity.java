 package com.moible.qlf.customviewdemo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.moible.qlf.customviewdemo.doubleseekbar.DoubleSeekBar;

 public class MainActivity extends AppCompatActivity {
    private String TAG = "QLF";
    private Button mButton,mButtonCancel;
    private PopupWindow mPopWindow;
    private Context mContext = this;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mButton = findViewById(R.id.button);
         mButtonCancel = findViewById(R.id.buttonCancel);
         View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop, null);
         mPopWindow = new PopupWindow(contentView);

         mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
         mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
         mPopWindow.setFocusable(true);
         mPopWindow.setBackgroundDrawable(new BitmapDrawable());
         //设置整个窗口阴影消失
         //mPopWindow.setOnDismissListener(new PopOnDismissListener(cb));
         //backgroundAlpha(0.9f);
         //设置PopWindow出现动画
         //mPopWindow.setAnimationStyle(R.style.AnimationPreview);

         mButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mPopWindow.showAsDropDown(mButton);
             }
         });

         mButtonCancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 mPopWindow.dismiss();
             }
         });
         DoubleSeekBar mDoubleSeekBar = contentView.findViewById(R.id.doubleSeek);

         mDoubleSeekBar.setOnScaleListener(new DoubleSeekBar.OnScaleListener() {
             @Override
             public void getDoubleSeekValue(String strLow, String strHigh) {
                 Log.i(TAG, "===strLow: " + strLow);
                 Log.i(TAG, "===strHigh: " + strHigh);
             }
         });
    }

     public void backgroundAlpha(float bgAlpha) {
         WindowManager.LayoutParams lp = getWindow().getAttributes();
         lp.alpha = bgAlpha;
         getWindow().setAttributes(lp);
     }
}
