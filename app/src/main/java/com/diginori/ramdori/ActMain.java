package com.diginori.ramdori;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmQuery;

public class ActMain extends Activity {
    private Context mContext;
    private ImageView mImgView;
    private EditText mMailTitle;
    private EditText mMailId;
    private EditText mMailPasswd;
    private EditText mtoMailId;
    private TextView mIsSuccessTextView;
    private Button mBtnGo;
    private MediaPlayer mp3Player;

    SharedPreferences p_user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mContext = this;
        p_user_info = PreferenceManager.getDefaultSharedPreferences(this.mContext);

        mImgView = (ImageView) findViewById(R.id.imageView);

        mMailTitle = (EditText) findViewById(R.id.et_mail_title);
        mMailId = (EditText) findViewById(R.id.et_mail_id);
        mMailPasswd = (EditText) findViewById(R.id.et_mail_passwd);
        mtoMailId = (EditText) findViewById(R.id.et_to_mail);

        mIsSuccessTextView = (TextView) findViewById(R.id.isSuccestTextView);

        GregorianCalendar today = new GregorianCalendar ();
        int month = today.get ( today.MONTH ) + 1;
        int day = today.get(today.DAY_OF_MONTH);

        String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", month, day, p_user_info.getString("user_name","임꺽정"));
        mMailTitle.setText(new_mail_title);

        mMailId.setText(p_user_info.getString("mail_id","myid@nbt.com"));
        mMailPasswd.setText(p_user_info.getString("mail_passwd","1234"));
        mtoMailId.setText(p_user_info.getString("to_mail_id", "to@nbt.com"));

        System.out.println(new_mail_title);

        mBtnGo = (Button) findViewById(R.id.button);
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rMsg;

                GregorianCalendar today = new GregorianCalendar();
                int year = today.get(today.YEAR);
                int month = today.get(today.MONTH) + 1;
                int day = today.get(today.DAY_OF_MONTH);

                String yyyymmdd = String.format("%s-%02d-%02d", year, month, day);

                try {
                    //DB
                    Realm realm = Realm.getInstance(mContext);
                    RealmQuery<UserHistory> query = realm.where(UserHistory.class);
                    query.equalTo("yyyymmdd", yyyymmdd);
                    if (query.findAll().size() > 0) {
                        playSong();
                        throw new Exception("하루에 한번만!");
                    }

                    //
                    String[] s = mMailTitle.getText().toString().split(" ");
                    String user_name = s[2];
                    String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", month, day, user_name);
                    mMailTitle.setText(new_mail_title);

                    savePre(user_name);

                    if(isSendMailFail(new_mail_title)){
                        playSong();
                        throw new Exception("메일발송 실패!!!");
                    }

                    //DB
                    realm.beginTransaction();
                    UserHistory uh = realm.createObject(UserHistory.class);
                    uh.setYyyymmdd(yyyymmdd);
                    realm.commitTransaction();

                    rMsg = "메일발송 성공!!!";
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
                playSong();
            }
        });
    }

    private void savePre(String user_name) {
        SharedPreferences.Editor e = p_user_info.edit();
        e.putString("user_name", user_name);
        e.putString("mail_id", mMailId.getText().toString());
        e.putString("mail_passwd", mMailPasswd.getText().toString());
        e.putString("to_mail_id", mtoMailId.getText().toString());
        e.commit();
    }

    private boolean isSendMailFail(String new_mail_title) {
        GMailSender sender = new GMailSender(mMailId.getText().toString(), mMailPasswd.getText().toString()); // SUBSTITUTE HERE
        try {
            sender.sendMail(
                    new_mail_title,   //subject.getText().toString(),
                    "밀크티만드는개발자@2015",           //body.getText().toString(),
                    mMailId.getText().toString(),          //from.getText().toString(),
                    mtoMailId.getText().toString()            //to.getText().toString()
            );
            return false;
        } catch (Exception eee) {
            Log.e("SendMail", eee.getMessage(), eee);
            return true;
        }
    }

    private void playSong() {
        if(mp3Player == null) {
            mp3Player = MediaPlayer.create(mContext, R.raw.theme_song);
        }

        if(mp3Player.isPlaying()){
            mp3Player.pause();
        }else{
            mp3Player.start();
        }
    }

}
