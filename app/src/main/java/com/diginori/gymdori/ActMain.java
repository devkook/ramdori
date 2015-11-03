package com.diginori.gymdori;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.crashlytics.android.Crashlytics;
import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.GregorianCalendar;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmQuery;

public class ActMain  extends Activity implements MoPubInterstitial.InterstitialAdListener {
    private Context mContext;
    private ImageView mImgView;
    private EditText mMailTitle;
    private EditText mtoMailId;
    private TextView mIsSuccessTextView;
    private Button mBtnGo;
    private MediaPlayer mp3Player;
    Realm realm;
    String yyyymmdd;
    String rMsg;
    int year;
    int mmm;
    int ddd;

    SharedPreferences p_user_info;

    private static MobileAnalyticsManager analytics;
    private static final int STATE_LOSE = 0;
    private static final int STATE_WIN = 1;

    private MoPubInterstitial interstitial;

    @Override
    protected void onPause() {
        super.onPause();
        if(analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new MoPub());
        setContentView(R.layout.activity_main);

        // TODO: Replace this test id with your personal ad unit id
        interstitial = new MoPubInterstitial(this, "939833da48484183aed05eff5067bb58");
        interstitial.setInterstitialAdListener(this);
        interstitial.load();


        initAmazonMobileAnalytics();

        this.mContext = this;
        p_user_info = PreferenceManager.getDefaultSharedPreferences(this.mContext);

        mImgView = (ImageView) findViewById(R.id.imageView);

        mMailTitle = (EditText) findViewById(R.id.et_mail_title);
        mtoMailId = (EditText) findViewById(R.id.et_to_mail);

        mIsSuccessTextView = (TextView) findViewById(R.id.isSuccestTextView);

        GregorianCalendar today = new GregorianCalendar ();
        int month = today.get ( today.MONTH ) + 1;
        int day = today.get(today.DAY_OF_MONTH);

        String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", month, day, p_user_info.getString("user_name","임꺽정"));
        mMailTitle.setText(new_mail_title);

        mtoMailId.setText(p_user_info.getString("to_mail_id", "to@nbt.com"));

        System.out.println(new_mail_title);

        realm = Realm.getInstance(mContext);

        mBtnGo = (Button) findViewById(R.id.button);
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GregorianCalendar today = new GregorianCalendar();
                year = today.get(today.YEAR);
                mmm = today.get(today.MONTH) + 1;
                ddd = today.get(today.DAY_OF_MONTH);

                yyyymmdd = String.format("%s-%02d-%02d", year, mmm, ddd);

                try {
                    //DB
                    RealmQuery<UserHistory> query = realm.where(UserHistory.class);
                    query.equalTo("yyyymmdd", yyyymmdd);
                    if (query.findAll().size() > 0) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("오늘은 메일을 보냈습니다!\n또 보낼까요?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendMail();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    throw new Exception("하루에 한번만!");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        sendMail();
                    }


                } catch (Exception e) {
                    rMsg = "T T " + e.getMessage();
                }

                mIsSuccessTextView.setText(rMsg);
                System.out.println("MSG:" + rMsg);
            }
        });



        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("imgView:");
            }
        });
    }

    private void initAmazonMobileAnalytics() {
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    "8ba6f183a54b4d94b065fc81cf374c1f", //Amazon Mobile Analytics App ID
                    "us-east-1:0bbc0715-e1f1-434c-be4b-26e38c7d2798" //Amazon Cognito Identity Pool ID
            );
        } catch(InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }
    }

    private void sendMail() {
        String[] s = mMailTitle.getText().toString().split(" ");
        String user_name = s[2];
        String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", mmm, ddd, user_name);
        mMailTitle.setText(new_mail_title);

        savePre(user_name);

        popupMailClinet(new_mail_title);

        //DB
        realm.beginTransaction();
        UserHistory uh = realm.createObject(UserHistory.class);
        uh.setYyyymmdd(yyyymmdd);
        realm.commitTransaction();
        rMsg = "카피카피 룸룸 ~";
    }

    private void popupMailClinet(String new_mail_title) {


        String bodyText = "카피카피 룸룸 ~";
        String uriText = "mailto:"+mtoMailId.getText().toString() +
                "?body=" + Uri.encode(bodyText);
        Uri uri = Uri.parse(uriText);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, new_mail_title);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Dialog dialog = new Dialog(this);
            dialog.setTitle("이메일 클라이언트 호출이상");
            dialog.show();
        }
    }

    private void savePre(String user_name) {
        SharedPreferences.Editor e = p_user_info.edit();
        e.putString("user_name", user_name);
        e.putString("to_mail_id", mtoMailId.getText().toString());
        e.commit();
    }

    @Override
    public void onDestroy(){
        interstitial.destroy();
        super.onDestroy();

    }

    // InterstitialAdListener methods
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        // This sample automatically shows the ad as soon as it's loaded, but
        // you can move this show call to a time more appropriate for your app.
        if (interstitial.isReady()) {
            interstitial.show();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Log.d("MoPub", "Interstitial load failed: " + errorCode);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
    }

}
