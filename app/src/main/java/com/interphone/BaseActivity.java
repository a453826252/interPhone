package com.interphone;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "【对讲服务】";
   public <T extends View> T f(int id){
        return findViewById(id);
    }
}
