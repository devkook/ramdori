package com.diginori.ramdori;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.GregorianCalendar;

public class ActMain extends Activity {
    private Context mContext;
    private ImageView mImgView;
    private EditText mMailTitle;
    private TextView mIsSuccessTextView;
    private Button mBtnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mContext = this;

        mImgView = (ImageView) findViewById(R.id.imageView);

        mMailTitle = (EditText) findViewById(R.id.et_mail_title);
        mIsSuccessTextView = (TextView) findViewById(R.id.isSuccestTextView);


        GregorianCalendar today = new GregorianCalendar ();
        int month = today.get ( today.MONTH ) + 1;
        int day = today.get ( today.DAY_OF_MONTH );
        String[] s = mMailTitle.getText().toString().split(" ");
        String user_name = s[2];
        String new_mail_title = String.format("[헬스장이용신청] %s월%s일 %s", month, day, user_name);
        mMailTitle.setText(new_mail_title);

        System.out.println(new_mail_title);

        mBtnGo = (Button) findViewById(R.id.button);
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rMsg = "T T";

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
                }catch (Exception e) {
                    rMsg = "T T " + e.getMessage();
                }

                mIsSuccessTextView.setText(rMsg);
                System.out.println("MSG:"+rMsg);
            }
        });
    }
}
