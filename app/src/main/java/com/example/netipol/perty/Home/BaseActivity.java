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
import android.widget.FrameLayout;

import com.example.netipol.perty.R;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private EventFragment eventFragment;
    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;

    private int prevFrag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        //frag constructors
        eventFragment = new EventFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();

        //set default frag
        setFrag(eventFragment);
       /* switch (prevFrag) {
            case 1 : setFrag(eventFragment);
            case 2 : setFrag(searchFragment);
            case 4 : setFrag(eventFragment);
            case 5 : setFrag(eventFragment);
        }*/

        mMainNav.setOnNavigationItemSelectedListener(this);

    }

        @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_events :
                        setFrag(eventFragment);
                        prevFrag=1;
                        return true;

                    case R.id.nav_search ://Search
                        setFrag(searchFragment);
                        prevFrag=2;
                        return true;

                    case R.id.nav_create :
                        startActivity(new Intent(BaseActivity.this, PostActivity.class));
                        //"post repeat" bug when post event at EventFragment
                        return false;

                    case R.id.nav_notif :///Explore
                        setFrag(eventFragment);
                        prevFrag=4;
                        return true;

                    case R.id.nav_profile :
                        setFrag(eventFragment);
                        prevFrag=5;
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
