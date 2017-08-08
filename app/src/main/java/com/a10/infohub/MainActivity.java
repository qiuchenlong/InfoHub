package com.a10.infohub;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a10.infohub.fix.Calculate;
import com.a10.infohub.fix.DexManager;
import com.a10.infohub.ui.Linerlayout;

import java.io.File;

/**
 * Created by qiuchenlong on 2017/6/24.
 */

public class MainActivity extends AppCompatActivity{

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        findViewById(R.id.calculator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calculate calculate = new Calculate();
                result.setText("" + calculate.caculutor());
            }
        });
        findViewById(R.id.fix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fix();
            }
        });

        LinearLayout layout = new LinearLayout(MainActivity.this);

        int left, top, right, bottom;
        left = top = right = bottom = 4;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);

        TextView textView = new TextView(this);
        textView.setText("Qiuchenlong");
        textView.setLayoutParams(params);
        textView.setBackgroundResource(R.drawable.btn_bg);

        TextView textView2 = new TextView(this);
        textView2.setText("邱晨龙");
        textView2.setLayoutParams(params);
        textView2.setBackgroundResource(R.drawable.btn_bg);
        textView2.setTextSize(30);

        TextView textView3 = new TextView(this);
        textView3.setText("福州大学");
        textView3.setLayoutParams(params);
        textView3.setBackgroundResource(R.drawable.btn_bg);

        TextView textView4 = new TextView(this);
        textView4.setText("计算机科学与技术");
        textView4.setLayoutParams(params);
        textView4.setBackgroundResource(R.drawable.btn_bg);
        textView4.setTextSize(50);



        TextView textView5 = new TextView(this);
        textView5.setText("十全十美");
        textView5.setLayoutParams(params);
        textView5.setBackgroundResource(R.drawable.btn_bg);

        TextView textView6 = new TextView(this);
        textView6.setText("1024");
        textView6.setLayoutParams(params);
        textView6.setBackgroundResource(R.drawable.btn_bg);
        textView6.setTextSize(40);



//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//        tv.setLayoutParams(lp);

        LinearLayout lls = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lls.setLayoutParams(lp);
//        lls.addView(tv1);
//        lls.addView(tv2);
//        lls.addView(tv3);
//        lls.addView(tv4);
//        lls.addView(tv5);
//        lls.addView(tv6);
//        lls.addView(tv7);
//        lls.addView(tv8);


        Linerlayout ll = (Linerlayout) findViewById(R.id.activity_main_linerlayout);
        ll.addView(textView);
        ll.addView(textView2);
        ll.addView(textView3);
        ll.addView(textView4);
        ll.addView(textView5);
        ll.addView(textView6);

//        addContentView();


//        TextView tv = findViewById(R.id.sample);
//        tv.setText(stringFromJNI());
    }

//    @Override
//    protected void onResume() {
//        /**
//         * 设置为横屏
//         */
//        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        super.onResume();
//    }

    //    public native String stringFromJNI();

    static {
        System.loadLibrary("native-lib");
    }

    private void fix() {
//        File file = new File(Environment.getExternalStorageDirectory(), "out.dex");
        File file = new File("/sdcard/Download", "out.dex");
        Log.d("TAG", "MainActivity..." + file.getAbsolutePath());
        Toast.makeText(MainActivity.this, "" + file.getAbsoluteFile(), Toast.LENGTH_SHORT).show();
        DexManager dexManager = new DexManager(this);
        dexManager.loadDex(file);
    }


    public void startOther(View v){
        Intent intent = new Intent(this, OtherActivity.class);
        startActivity(intent);
    }

}
