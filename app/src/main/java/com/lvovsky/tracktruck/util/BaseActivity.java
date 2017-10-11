package com.lvovsky.tracktruck.util;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.lvovsky.tracktruck.R;


/**
 * Created by Aman on 16/05/17.
 */
public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public void initToolbar(String title) {
        initToolbar(title, true);
    }

    public void initToolbar(String title, boolean homeButtonEnabled) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null)
            return;

        getSupportActionBar().setDisplayHomeAsUpEnabled(homeButtonEnabled);
        getSupportActionBar().setHomeButtonEnabled(homeButtonEnabled);
    }
}
