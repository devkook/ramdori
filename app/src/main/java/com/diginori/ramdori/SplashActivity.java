package com.diginori.ramdori;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;

import android.os.Handler;
import android.widget.TextView;


public class SplashActivity extends Activity
{
    private TextView mVersionInfo;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mVersionInfo = (TextView) findViewById(R.id.textViewVersionInfo);
        String version;
        try {
            PackageInfo i = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = i.versionName;
            mVersionInfo.setText(version);
        } catch(PackageManager.NameNotFoundException e) { }

        initialize();


    }

    private void initialize()
    {
        Handler handler =    new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                finish();    // 액티비티 종료
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);    // ms, 3초후 종료시킴
    }
}
