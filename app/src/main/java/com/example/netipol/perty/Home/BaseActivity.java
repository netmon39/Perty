package com.example.netipol.perty.Home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.netipol.perty.R;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private EventFragment eventFragment;
    private ProfileFragment profileFragment;

    private int prevFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        //frag constructors
        eventFragment = new EventFragment();
        profileFragment = new ProfileFragment();

        //set default frag
        setFrag(eventFragment);

        mMainNav.setOnNavigationItemSelectedListener(this);

    }

        @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_events :
                        prevFrag = R.id.nav_events;
                        setFrag(eventFragment);

                        return true;

                    case R.id.nav_search :
                        prevFrag = R.id.nav_search;
                        setFrag(eventFragment);
                        return true;

                    case R.id.nav_create :

                        startActivity(new Intent(BaseActivity.this, PostActivity.class));
                        mMainNav.setSelectedItemId(mMainNav.getMenu().findItem(R.id.nav_events).getItemId());




                        return true;

                    case R.id.nav_notif :
                        prevFrag = R.id.nav_notif;
                        setFrag(eventFragment);
                        return true;

                    case R.id.nav_profile :
                        prevFrag = R.id.nav_profile;
                        setFrag(eventFragment);
                        return true;

                    default:
                        return false;

                }
            }


    private void setFrag(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }
}
