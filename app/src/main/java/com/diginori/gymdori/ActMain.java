package com.diginori.gymdori;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmQuery;

public class ActMain  extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
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

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    SharedPreferences p_user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));


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
                                playSong();
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
                playSong();
            }
        });
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


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.navigation_drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((NavigationDrawer) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
