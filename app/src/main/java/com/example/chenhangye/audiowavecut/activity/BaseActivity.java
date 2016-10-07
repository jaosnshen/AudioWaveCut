package com.example.chenhangye.audiowavecut.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chenhangye.audiowavecut.R;

/**
 * Created by chenhangye on 2016/10/2.
 */

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutId());
        initVariable();
        initView();
        initDate();
    }


    protected abstract void initDate();

    protected abstract void initVariable();

    protected abstract void initView();
    protected abstract int LayoutId();
}
