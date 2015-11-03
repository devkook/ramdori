package com.diginori.gymdori;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.mopub.mobileads.MoPubView;


public class SplashActivity extends Activity
{
    private TextView mVersionInfo;
    private Context mContext;
    private MoPubView moPubView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        moPubView = (MoPubView) findViewById(R.id.mopub_sample_ad);
        // TODO: Replace this test id with your personal ad unit id
        moPubView.setAdUnitId("d3edc2d646e34b6fbc7721c582b9f4a5");
        moPubView.loadAd();


        mVersionInfo = (TextView) findViewById(R.id.textViewVersionInfo);
        String version;
        try {
            PackageInfo i = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = i.versionName;
            mVersionInfo.setText(version);
        } catch(PackageManager.NameNotFoundException e) { }

        initialize();


    }

    @Override
    protected void onDestroy(){
        moPubView.destroy();
        super.onDestroy();

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
