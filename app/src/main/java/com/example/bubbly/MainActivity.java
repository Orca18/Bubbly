package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbly.Kim_Bottom.Bottom1_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom2_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom3_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom4_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Bottom1_Fragment bottom1_fragment = new Bottom1_Fragment();
    private Bottom2_Fragment bottom2_fragment = new Bottom2_Fragment();
    private Bottom3_Fragment bottom3_fragment = new Bottom3_Fragment();
    private Bottom4_Fragment bottom4_fragment = new Bottom4_Fragment();


    // 내비뷰 메뉴 레이아웃에 직접 구현
    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    public static DrawerLayout drawerLayout;
    public static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.home_frame, bottom1_fragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelecedListener2());

        navigationView = findViewById(R.id.main_navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        LayoutInflater.from(getApplicationContext()).inflate(R.layout.navi_header, navigationView);

        view = navigationView.inflateHeaderView(R.layout.navi_header);
        // 사이드메뉴
        // 내비 안 메뉴
        myAccount = view.findViewById(R.id.navi_header_profileimg);
        myActivity = view.findViewById(R.id.navi_header_myActivity);
        myList = view.findViewById(R.id.navi_header_myList);
        myCommunity = view.findViewById(R.id.navi_header_myCommunity);
        settingOption = view.findViewById(R.id.navi_header_setting_option);
        info = view.findViewById(R.id.navi_header_info);
        logout = view.findViewById(R.id.navi_header_logout);

        NaviTouch();
    }

    private class ItemSelecedListener2 implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(item.getItemId()){
                case R.id.mm_home:
                    transaction.replace(R.id.home_frame, bottom1_fragment).commitAllowingStateLoss();

                    break;

                case R.id.mm_issue:
                    transaction.replace(R.id.home_frame, bottom2_fragment).commitAllowingStateLoss();
                    break;

                case R.id.mm_message:
                    transaction.replace(R.id.home_frame, bottom3_fragment).commitAllowingStateLoss();
                    break;

                case R.id.mm_profile:
                    transaction.replace(R.id.home_frame, bottom4_fragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    // 내비 터치
    private void NaviTouch() {
        // 내비뷰 메뉴 레이아웃에 직접 구현
        // CircleImageView myAccount;
        // LinearLayout myActivity, myList, myCommunity;
        // TextView settingOption, info, logout;
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent3);
                finish();
            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent wallet = new Intent(getApplicationContext(), MM_Wallet.class);
                startActivity(wallet);
            }
        });
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                //Toast.makeText(getApplicationContext(), "겉멋", Toast.LENGTH_SHORT).show();
            }
        });
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent mIntent = new Intent(getApplicationContext(), Community_Home_Feeds.class);
                startActivity(mIntent);
            }
        });
        settingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent settingIntent = new Intent(getApplicationContext(), SS_Setting.class);
                startActivity(settingIntent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
            }
        });
    }






}