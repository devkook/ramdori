package com.diginori.ramdori;

import android.app.Activity;
import android.content.Context;
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
        mtoMailId.setText(p_user_info.getString("to_mail_id","to@nbt.com"));

        System.out.println(new_mail_title);

        mBtnGo = (Button) findViewById(R.id.button);
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rMsg;

                GregorianCalendar today = new GregorianCalendar ();
                int month = today.get ( today.MONTH ) + 1;
                int day = today.get ( today.DAY_OF_MONTH );

                try{
                    String[] s = mMailTitle.getText().toString().split(" ");
                    String user_name = s[2];
                    String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", month, day, user_name);
                    mMailTitle.setText(new_mail_title);

                    System.out.println(new_mail_title);

                    rMsg = "OK ";

                    SharedPreferences.Editor e = p_user_info.edit();
                    e.putString("user_name",user_name);
                    e.putString("mail_id",mMailId.getText().toString());
                    e.putString("mail_passwd",mMailPasswd.getText().toString());
                    e.putString("to_mail_id",mtoMailId.getText().toString());
                    e.commit();


                    GMailSender sender = new GMailSender(mMailId.getText().toString(),mMailPasswd.getText().toString()); // SUBSTITUTE HERE
                    try {
                        sender.sendMail(
                                new_mail_title,   //subject.getText().toString(),
                                "메일 본문입니다..~~ ",           //body.getText().toString(),
                                mMailId.getText().toString(),          //from.getText().toString(),
                                "min.kyoungkook@nbt.com"            //to.getText().toString()
                        );
                    } catch (Exception eee) {
                        Log.e("SendMail", eee.getMessage(), eee);
                    }

            }catch (Exception e) {
                    rMsg = "T T " + e.getMessage();
                }

                mIsSuccessTextView.setText(rMsg);
                System.out.println("MSG:"+rMsg);
            }
        });

        mp3Player = MediaPlayer.create(mContext, R.raw.theme_song);

        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("imgView:");

                if(mp3Player.isPlaying()){
                    mp3Player.pause();
                }else{
                    mp3Player.start();
                }
            }
        });
    }

}
