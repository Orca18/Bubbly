package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bubbly.Kim_Bottom.Bottom1_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom2_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom3_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom4_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Bottom1_Fragment bottom1_fragment = new Bottom1_Fragment();
    private Bottom2_Fragment bottom2_fragment = new Bottom2_Fragment();
    private Bottom3_Fragment bottom3_fragment = new Bottom3_Fragment();
    private Bottom4_Fragment bottom4_fragment = new Bottom4_Fragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.home_frame, bottom1_fragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelecedListener2());

    }

    private class ItemSelecedListener2 implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(item.getItemId()){
                case R.id.mm_home:
                    transaction.replace(R.id.home_frame, bottom1_fragment).commit();
                    break;

                case R.id.mm_issue:
                    transaction.replace(R.id.home_frame, bottom2_fragment).commit();
                    break;

                case R.id.mm_message:
                    transaction.replace(R.id.home_frame, bottom3_fragment).commit();
                    break;

                case R.id.mm_profile:
                    transaction.replace(R.id.home_frame, bottom4_fragment).commit();
                    break;
            }
            return false;
        }
    }

}