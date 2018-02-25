package com.example.netipol.perty.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import com.example.netipol.perty.Login.LoginActivity;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private EventFragment eventFragment;
    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;
    private ExploreFragment exploreFragment;
    private int prevFrag=1;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        //frag constructors
        eventFragment = new EventFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();
        exploreFragment = new ExploreFragment();


        //set default frag
        setFrag(eventFragment);

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
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                //"post repeat" bug when post event at EventFragment
                return false;

            case R.id.nav_exp :///Explore
                setFrag(exploreFragment);
                prevFrag=4;
                return true;

            case R.id.nav_profile :
                setFrag(profileFragment);
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

    @Override
    public void onStart() {//is user logged in? START HERE
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {//First time user
            sendToLogin();
        }
    }

    public void sendToLogin(){

        //CHECK if user had previous authentication !!!!!!!
        //CHECK if user had previous authentication !!!!!!!

        //some code

        //CHECK if user had previous authentication !!!!!!!
        //CHECK if user had previous authentication !!!!!!!

        Intent accountIntent = new Intent(MainActivity.this, LoginActivity.class);//for 1st time user
        startActivity(accountIntent);
        finish();
    }

    /*
    @Override// *************** for demo purposes, don't forget to remove this **************
    public void onStop() {
        super.onStop();

        signOutFromAll();
    }*/

    public void signOutFromAll(){
        mAuth.getInstance().signOut();//of Firebase
        LoginManager.getInstance().logOut();//of Facebook
    }


}
