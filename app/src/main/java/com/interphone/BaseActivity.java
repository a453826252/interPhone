package com.interphone;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {
   public <T extends View> T f(int id){
        return findViewById(id);
    }
}
