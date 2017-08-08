package com.a10.infohub;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.a10.infohub.bean.PhotoSetInfo;
import com.a10.infohub.http.HttpProcessor.HttpCallback;
import com.a10.infohub.http.HttpProcessor.HttpHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiuchenlong on 2017/8/6.
 */

public class OtherActivity extends Activity implements View.OnClickListener{

    private static final String TAG = OtherActivity.class.getName();
    private String url = "http://c.3g.163.com/photo/api/set/0001%2F2250173.json";
    private Map<String, Object> params = new HashMap<>();
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_layout);

        textView = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.btn_click);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_click:
                HttpHelper.obtain().post(url, params, new HttpCallback<PhotoSetInfo>() {
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(OtherActivity.this, "出错了。。。", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, error);
                    }

                    @Override
                    public void onSuccess(PhotoSetInfo result) {
                        StringBuffer sb = new StringBuffer();
                        if (result != null) {
                            sb.append("来源：")
                                    .append(result.getSource())
                                    .append("\r\n")
                                    .append("Tag:")
                                    .append(result.getSettag())
                                    .append("\r\n")
                                    .append("天气描述：")
                                    .append(result.getDesc());
                        }
                        textView.setText(sb.toString());
                    }
                });
                break;
        }
    }
}
