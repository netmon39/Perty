package com.example.netipol.perty.Home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import com.example.netipol.perty.Announce.Announce;
import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Util.BottomNavigationViewHelper;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Login.LoginActivity;
import com.example.netipol.perty.Profile.NotificationFragment;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.example.netipol.perty.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //private AHBottomNavigation mMainNav;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private EventFragment eventFragment;
    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;
    private ExploreFragment exploreFragment;
    private NotificationFragment notiFragment;
    private int prevFrag=1, x;
    private String hostid;
    private FirebaseFirestore mFirestore;

    private List<FriendReq> friendreqList;//List that stores Events
    private List<Announce> announceList;
    private List<String> joiningList;

    private FirebaseAuth mAuth;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        joiningList = new ArrayList<>();
        announceList = new ArrayList<>();
        friendreqList = new ArrayList<>();

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);

        //mMainNav = (AHBottomNavigation) findViewById(R.id.main_nav);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        //frag constructors
        eventFragment = new EventFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();
        exploreFragment = new ExploreFragment();
        notiFragment = new NotificationFragment();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Log.d("whynologin", "Logged in as"+currentUser.getDisplayName());
        if(currentUser == null) {//First time user
            Log.d("lgn_onCreate", "current user null");
            sendToLogin();
            finish();
        }else{//logged in...
            setFrag(eventFragment);
            Log.d("lgn_onStart", "current user is logged in, show EventFeed");
        }

        //FirebaseUser currentUser = mAuth.getCurrentUser();

        /*int v = GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        Toast toast = Toast.makeText(getApplicationContext(),String.valueObf(v),Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();*/

        //set default frag
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //Log.d("whynologin", "Logged in as"+currentUser.getDisplayName());

        /*AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_white_24dp, R.color.purps);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Explore", R.drawable.ic_search_white_24dp, R.color.colorPrimaryDark);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Create", R.drawable.ic_add_circle_outline_white_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Notice", R.drawable.ic_notifications_white_24dp, R.color.purps);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Profile", R.drawable.ic_person_white_24dp, R.color.colorPrimaryDark);

        // Add items
        mMainNav.addItem(item1);
        mMainNav.addItem(item2);
        mMainNav.addItem(item3);
        mMainNav.addItem(item4);
        mMainNav.addItem(item5);

        // Set background color
        mMainNav.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        mMainNav.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));
        mMainNav.setInactiveColor(Color.parseColor("#747474"));

        mMainNav.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        // Customize notification (title, background, typeface)
        mMainNav.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Set listeners
        mMainNav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                switch (position){
                    case 0 :
                        setFrag(eventFragment);
                        prevFrag=1;
                        return true;

                    case 1 ://Explore
                        setFrag(exploreFragment);
                        prevFrag=2;
                        return true;

                    case 2 :
                        startActivity(new Intent(MainActivity.this, PostActivity.class));
                        //"post repeat" bug when post event at EventFragment
                        return false;

                    case 3 :///Explore
                        setFrag(notiFragment);
                        prevFrag=4;
                        //checkNoti();
                        return true;

                    case 4 :
                        setFrag(profileFragment);
                        prevFrag=5;
                        return true;

                    default:
                        return false;

                }
            }
        });
        */



        mMainNav.setOnNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_nav);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

    }

    public String getHostId(){
        return hostid;
    }

    public void setHostId(String id){
        hostid = id;
    }

    public String  getActionBarTitle(){
        return getSupportActionBar().getTitle().toString();
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override//Control items pressed in actionbar here
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_events :
                setFrag(eventFragment);
                prevFrag=1;
                return true;

            case R.id.nav_search ://Explore
                setFrag(exploreFragment);
                prevFrag=2;
                return true;

            case R.id.nav_create :
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                //"post repeat" bug when post event at EventFragment
                return false;

            case R.id.nav_exp ://Notice
                setFrag(notiFragment);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void setFrag(Fragment fragment){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_frame, fragment);
            fragmentTransaction.commit();

    }

    /*public void checkNoti(){

            friendreqList.clear();
            joiningList.clear();
            announceList.clear();
            x=0;

            mFirestore.collection("users").document(Profile.getCurrentProfile().getId()).collection("requests")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d("FriendList", document.getId() + " => " + document.getData());
                                    String friendreq_id = document.getId();
                                    FriendReq events = document.toObject(FriendReq.class).withId(friendreq_id);
                                    friendreqList.add(events);
                                }

                            } else {
                            }
                        }
                    });


            //see all joined events, keep in joinedList, go get all announcements from all events in joinedList
            mFirestore.collection("users").document(Profile.getCurrentProfile().getId()).collection("joining")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    joiningList.add(document.get("eid").toString());
                                }
                                if(!joiningList.isEmpty()){


                                    mFirestore.collection("events")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            for(int i=0; i<joiningList.size();i++){
                                                                if(joiningList.get(i).equals(document.getId())){//if this event is joined by user..
                                                                    mFirestore
                                                                            .collection("events")
                                                                            .document(document.getId())
                                                                            .collection("announcements")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("ann", document.getData().toString());
                                                                                            String announce_id = document.getId();
                                                                                            Announce announces = document.toObject(Announce.class).withId(announce_id);
                                                                                            announceList.add(announces);
                                                                                        }

                                                                                        x = friendreqList.size()+announceList.size();
                                                                                        Log.d("olo with join: ", String.valueOf(x) , task.getException());
                                                                                        if(x>0){
                                                                                            mMainNav.setNotification(String.valueOf(x), 3);
                                                                                        }else{
                                                                                            mMainNav.setNotification("", 3);
                                                                                        }

                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                    }
                                                }
                                            });
                                }else{

                                    // Add or remove notification for each item
                                    x = friendreqList.size();
                                    Log.d("olo no join: ", String.valueOf(x) , task.getException());
                                    if(x>0){
                                        mMainNav.setNotification(String.valueOf(x), 3);
                                    }else{
                                        mMainNav.setNotification("", 3);
                                    }

                                }


                            } else {
                            }
                        }
                    });

        }*/


    /*@Override
    public void onStart() {//is user logged in? START HERE
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Log.d("whynologin", "Logged in as"+currentUser.getDisplayName());
        if(currentUser == null) {//First time user
            Log.d("lgn_onStart", "current user null");
            sendToLogin();
            finish();
        }else{//logged in...
            //setFrag(eventFragment);
            Log.d("lgn_onStart", "current user is logged in");
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        //checkNoti();
    }

    public void sendToLogin(){

        //CHECK if user had previous authentication !!!!!!!
        //CHECK if user had previous authentication !!!!!!!

        //some code

        //CHECK if user had previous authentication !!!!!!!
        //CHECK if user had previous authentication !!!!!!!

        Intent accountIntent = new Intent(MainActivity.this, LoginActivity.class);
        accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//for 1st time user
        startActivity(accountIntent);
        //finish();
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
