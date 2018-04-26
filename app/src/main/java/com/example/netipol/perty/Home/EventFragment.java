package com.example.netipol.perty.Home;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.example.netipol.perty.Profile.FavoritesFragment;
import com.example.netipol.perty.Profile.FriendFragment;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mEventList;
    private FirebaseFirestore mFirestore;
    private List<Event> eventList;//List that stores Events
    public List<String> friendsList;
    private EventListAdapter eventListAdapter;
    //private String userCategKey;
    private char[] cArray, ppArray;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public boolean friendsListEmpty = false;
    private boolean publicToggle = false;
    private boolean privateToggle = false;
    public String mUser_id =null;
    private DrawerLayout mDrawerLayout;
    private boolean cat0, cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10;
    private MenuItem menuItem0, menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6, menuItem7, menuItem8, menuItem9, menuItem10;
    private String categ_key_new = "";
    private String categ_key_current = "";
    private String pp_key_new = "";
    private String pp_key_current = "";
    public ProgressDialog mProgress;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);



        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList , getFragmentManager(),1);

        friendsList = new ArrayList<>();

        mUser_id = Profile.getCurrentProfile().getId();//current user's id
        mProgress = new ProgressDialog(getActivity());

        //RecyclerView setup
        mEventList = v.findViewById(R.id.event_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mEventList.setAdapter(eventListAdapter);//to fill recycler view with Events
        //mEventList.invalidate();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container3);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });

        setHasOptionsMenu(true);

        mDrawerLayout = v.findViewById(R.id.drawer_layout);

        NavigationView navigationView = v.findViewById(R.id.categ_nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        // close drawer when item is tapped
                        //mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()) {

                            case R.id.categ_apply:
                                //Set user preferences

                                categ_key_new = "";
                                pp_key_new = "";

                                if(cat0==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("0")
                                            .toString();
                                }
                                if(cat1==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("1")
                                            .toString();
                                }
                                if(cat2==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("2")
                                            .toString();
                                }
                                if(cat3==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("3")
                                            .toString();
                                }
                                if(cat4==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("4")
                                            .toString();
                                }
                                if(cat5==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("5")
                                            .toString();
                                }
                                if(cat6==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("6")
                                            .toString();
                                }
                                if(cat7==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("7")
                                            .toString();
                                }
                                if(cat8==true){
                                    categ_key_new = new StringBuilder()
                                            .append(categ_key_new)
                                            .append("8")
                                            .toString();
                                }
                                if(cat9==true){//Public
                                    pp_key_new = new StringBuilder()
                                            .append(pp_key_new)
                                            .append("a")
                                            .toString();
                                }
                                if(cat10==true){//Public
                                    pp_key_new = new StringBuilder()
                                            .append(pp_key_new)
                                            .append("b")
                                            .toString();
                                }

                                //set new categ_key in firebase
                                Map<String, Object> categ = new HashMap<>();
                                categ.put("categ_key", categ_key_new);
                                categ.put("pp_key", pp_key_new);

                                mFirestore.collection("users")
                                        .document(mUser_id)
                                        .set(categ, SetOptions.merge());

                                //reload recylcer view
                                loadRecyclerViewData();


                                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                                break;
                        }


                        return true;
                    }
                });

        //populateCategCheck(mUser_id);
        Menu menu = navigationView.getMenu();

        populateCategCheck(mUser_id, menu);

        checkCategCheckBoolean(menu);

        MenuItem tools= menu.findItem(R.id.categ_apply);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        // Set title
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle("Perty"); // for set actionbar title
        bar.setDisplayHomeAsUpEnabled(false); // for add back arrow in action bar
    }

    // create an action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();

        if (id == R.id.categButt) {
            // do something here
            return true;
            //Intent intent = new Intent(getApplicationContext(), SelectPrefActivity.class);
            //startActivity(intent);
        }*/

        switch (item.getItemId()) {
            case R.id.categButt:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
                else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        // Fetching data from server
        loadRecyclerViewData();
    }

    public void populateCategCheck(String userid, final Menu menu){

        mFirestore = FirebaseFirestore.getInstance();
        //1. Get user's categ_key
        mFirestore.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("categkey", "DocumentSnapshot data: " + document.getData());

                        categ_key_current = document.get("categ_key").toString();
                        pp_key_current = document.get("pp_key").toString();

                        Log.d("categkey", categ_key_current);

                        //2. Decrypt key and store it into an array
                        cArray = categ_key_current.toCharArray();
                        ppArray = pp_key_current.toCharArray();

                        //3. For loop the cArray, while querying "events" to look for corresponding categories
                        for(int i = 0; i < cArray.length; i++) {

                            Log.d("retrieved key", String.valueOf(cArray[i]));

                            switch (String.valueOf(cArray[i])) {
                                case "0":
                                    //menu.findItem(R.id.categ_sports).setChecked(true);
                                    menuItem0 = menu.findItem(R.id.categ_sports);
                                    CheckBox checkBox0 = (CheckBox) menuItem0.getActionView();
                                    checkBox0.setChecked(true);
                                    menuItem0.setChecked(true);
                                    cat0=true;
                                    break;
                                case "1":
                                    menuItem1 = menu.findItem(R.id.categ_education);
                                    CheckBox checkBox1 = (CheckBox) menuItem1.getActionView();
                                    checkBox1.setChecked(true);
                                    menuItem1.setChecked(true);
                                    cat1=true;
                                    break;
                                case "2":
                                    menuItem2 = menu.findItem(R.id.categ_recreation);
                                    CheckBox checkBox2 = (CheckBox) menuItem2.getActionView();
                                    checkBox2.setChecked(true);
                                    menuItem2.setChecked(true);
                                    cat2=true;
                                    break;
                                case "3":
                                    menuItem3 = menu.findItem(R.id.categ_music);
                                    CheckBox checkBox3 = (CheckBox) menuItem3.getActionView();
                                    checkBox3.setChecked(true);
                                    menuItem3.setChecked(true);
                                    cat3=true;
                                    break;
                                case "4":
                                    menuItem4 = menu.findItem(R.id.categ_art);
                                    CheckBox checkBox4 = (CheckBox) menuItem4.getActionView();
                                    checkBox4.setChecked(true);
                                    menuItem4.setChecked(true);
                                    cat4=true;
                                    break;
                                case "5":
                                    menuItem5 = menu.findItem(R.id.categ_th);
                                    CheckBox checkBox5 = (CheckBox) menuItem5.getActionView();
                                    checkBox5.setChecked(true);
                                    menuItem5.setChecked(true);
                                    cat5=true;
                                    break;
                                case "6":
                                    menuItem6 = menu.findItem(R.id.categ_tech);
                                    CheckBox checkBox6 = (CheckBox) menuItem6.getActionView();
                                    checkBox6.setChecked(true);
                                    menuItem6.setChecked(true);
                                    cat6=true;
                                    break;
                                case "7":
                                    menuItem7 = menu.findItem(R.id.categ_out);
                                    CheckBox checkBox7 = (CheckBox) menuItem7.getActionView();
                                    checkBox7.setChecked(true);
                                    menuItem7.setChecked(true);
                                    cat7=true;
                                    break;
                                case "8":
                                    menuItem8 = menu.findItem(R.id.categ_career);
                                    CheckBox checkBox8 = (CheckBox) menuItem8.getActionView();
                                    checkBox8.setChecked(true);
                                    menuItem8.setChecked(true);
                                    cat8=true;
                                    break;
                            }

                        }

                        for(int i = 0; i < ppArray.length; i++) {
                            switch (String.valueOf(ppArray[i])) {
                                case "a":
                                    //menu.findItem(R.id.categ_sports).setChecked(true);
                                    menuItem9 = menu.findItem(R.id.categ_public);
                                    Switch checkBox9 = (Switch) menuItem9.getActionView();
                                    checkBox9.setChecked(true);
                                    menuItem9.setChecked(true);
                                    cat9 = true;
                                    break;
                                case "b":
                                    menuItem10 = menu.findItem(R.id.categ_private);
                                    Switch checkBox10 = (Switch) menuItem10.getActionView();
                                    checkBox10.setChecked(true);
                                    menuItem10.setChecked(true);
                                    cat10 = true;
                                    break;
                            }


                        }


                        //categ_key_ = "";

                    } else {
                        Log.d("olo", "No such document");
                    }
                } else {
                    Log.d("olo", "get failed with ", task.getException());
                }
            }
        });
    }

    public void checkCategCheckBoolean(Menu menu){
        menuItem0 = menu.findItem(R.id.categ_sports);
        View actionView0 = MenuItemCompat.getActionView(menuItem0);
        actionView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem0.isChecked()){
                    menuItem0.setChecked(false);
                    cat0=false;
                }else{
                    menuItem0.setChecked(true);
                    cat0=true;
                }
            }
        });
        menuItem1 = menu.findItem(R.id.categ_education);
        View actionView1 = MenuItemCompat.getActionView(menuItem1);
        actionView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem1.isChecked()){
                    menuItem1.setChecked(false);
                    cat1=false;
                }else{
                    menuItem1.setChecked(true);
                    cat1=true;
                }
            }
        });
        menuItem2 = menu.findItem(R.id.categ_recreation);
        View actionView2 = MenuItemCompat.getActionView(menuItem2);
        actionView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem2.isChecked()){
                    menuItem2.setChecked(false);
                    cat2=false;
                }else{
                    menuItem2.setChecked(true);
                    cat2=true;
                }
            }
        });
        menuItem3 = menu.findItem(R.id.categ_music);
        View actionView3 = MenuItemCompat.getActionView(menuItem3);
        actionView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem3.isChecked()){
                    menuItem3.setChecked(false);
                    cat3=false;
                }else{
                    menuItem3.setChecked(true);
                    cat3=true;
                }
            }
        });
        menuItem4 = menu.findItem(R.id.categ_art);
        View actionView4 = MenuItemCompat.getActionView(menuItem4);
        actionView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem4.isChecked()){
                    menuItem4.setChecked(false);
                    cat4=false;
                }else{
                    menuItem4.setChecked(true);
                    cat4=true;
                }
            }
        });
        menuItem6 = menu.findItem(R.id.categ_tech);
        View actionView6 = MenuItemCompat.getActionView(menuItem6);
        actionView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem6.isChecked()){
                    menuItem6.setChecked(false);
                    cat6=false;
                }else{
                    menuItem6.setChecked(true);
                    cat6=true;
                }
            }
        });
        menuItem7 = menu.findItem(R.id.categ_out);
        View actionView7 = MenuItemCompat.getActionView(menuItem7);
        actionView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem7.isChecked()){
                    menuItem7.setChecked(false);
                    cat7=false;
                }else{
                    menuItem7.setChecked(true);
                    cat7=true;
                }
            }
        });
        menuItem8 = menu.findItem(R.id.categ_career);
        View actionView8 = MenuItemCompat.getActionView(menuItem8);
        actionView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem8.isChecked()){
                    menuItem8.setChecked(false);
                    cat8=false;
                }else{
                    menuItem8.setChecked(true);
                    cat8=true;
                }
            }
        });
        menuItem5 = menu.findItem(R.id.categ_th);
        View actionView5 = MenuItemCompat.getActionView(menuItem5);
        actionView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem5.isChecked()){
                    menuItem5.setChecked(false);
                    cat5=false;
                }else{
                    menuItem5.setChecked(true);
                    cat5=true;
                }
            }
        });
        menuItem9 = menu.findItem(R.id.categ_public);
        View actionView9 = MenuItemCompat.getActionView(menuItem9);
        actionView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem9.isChecked()){
                    menuItem9.setChecked(false);
                    cat9=false;
                }else{
                    menuItem9.setChecked(true);
                    cat9=true;
                }
            }
        });
        menuItem10 = menu.findItem(R.id.categ_private);
        View actionView10 = MenuItemCompat.getActionView(menuItem10);
        actionView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItem10.isChecked()){
                    menuItem10.setChecked(false);
                    cat10=false;
                }else{
                    menuItem10.setChecked(true);
                    cat10=true;
                }
            }
        });

    }


    public void loadRecyclerViewData()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        eventList.clear();
        friendsList.clear();

        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mEventList.setAdapter(eventListAdapter);//to fill recycler view with Events

        mFirestore = FirebaseFirestore.getInstance();

        mProgress.setMessage("Loading your feed ...");
        mProgress.show();

        publicToggle=false;
        privateToggle=false;

        //get list of user's friends uid
        mFirestore.collection("users").document(mUser_id).collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                       if(!task.getResult().isEmpty()){//if have friends

                           Log.d("ddog", "has friends");
                           friendsListEmpty = false;

                           if (task.isSuccessful()) {

                               for (DocumentSnapshot document : task.getResult()) {
                                   //Convert to lowercase
                                   friendsList.add(document.get("uid").toString());
                                   Log.d("ddog", "friendsList size: "+Integer.toString(friendsList.size()));
                               }
                           }
                       }else{//if dont have friends
                           friendsListEmpty = true;
                           Log.d("ddog", "no friends");

                       }

                        mFirestore.collection("users").document(mUser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {

                                        String userPpKey = document.get("pp_key").toString();

                                        //2. Decrypt key and store it into an array
                                        ppArray = userPpKey.toCharArray();

                                        //3. For loop the cArray, while querying "events" to look for corresponding categories
                                        for(int i = 0; i < ppArray.length; i++) {

                                            switch (String.valueOf(ppArray[i])) {
                                                case "a":
                                                    Log.d("ddog public", String.valueOf(publicToggle));

                                                    publicToggle = true;

                                                    Log.d("ddog public", String.valueOf(publicToggle));

                                                    break;
                                                case "b":

                                                    Log.d("ddog private", String.valueOf(privateToggle));

                                                    privateToggle = true;

                                                    Log.d("ddog private", String.valueOf(privateToggle));

                                                    break;

                                            }

                                        }

                                        //1. Get user's categ_key
                                        mFirestore.collection("users").document(mUser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null && document.exists()) {
                                                        Log.d("categkey", "DocumentSnapshot data: " + document.getData());

                                                        String userCategKey = document.get("categ_key").toString();

                                                        Log.d("categkey", userCategKey);

                                                        //2. Decrypt key and store it into an array
                                                        cArray = userCategKey.toCharArray();

                                                        mProgress.dismiss();

                                                        //3. For loop the cArray, while querying "events" to look for corresponding categories
                                                        for(int i = 0; i < cArray.length; i++) {

                                                            Log.d("categkey", String.valueOf(cArray[i]));

                                                            switch (String.valueOf(cArray[i])) {
                                                                case "0":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "SPORTS")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });

                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "SPORTS")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "1":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "EDUCATION")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "EDUCATION")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "2":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "RECREATION")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "RECREATION")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "3":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "MUSIC")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "MUSIC")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "4":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "ART")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()  && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "ART")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "5":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "THEATRE")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "THEATRE")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "6":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "TECHNOLOGY")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "TECHNOLOGY")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "7":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "OUTING")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "OUTING")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;
                                                                case "8":
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "CAREER")
                                                                            .whereEqualTo("type", "Public")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && publicToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            if(!document.get("hostid").equals(mUser_id)){
                                                                                                Log.d("olo", document.getId() + " => " + document.getData());
                                                                                                //Add into arraylist<Event>
                                                                                                String event_id = document.getId();
                                                                                                Event events = document.toObject(Event.class).withId(event_id);
                                                                                                eventList.add(events);
                                                                                                eventListAdapter.notifyDataSetChanged();
                                                                                                //4. Sort eventList by timestamp
                                                                                                Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                Collections.sort(eventList);
                                                                                                Collections.reverse(eventList);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    mFirestore.collection("events")
                                                                            .whereEqualTo("categ", "CAREER")
                                                                            .whereEqualTo("type", "Private")
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful() && friendsListEmpty==false && privateToggle==true) {
                                                                                        for (DocumentSnapshot document : task.getResult()) {
                                                                                            Log.d("olo", document.getId() + " => " + document.getData());

                                                                                            for(int i=0;i<friendsList.size();i++){
                                                                                                if(friendsList.get(i).equals(document.get("hostid").toString()) && !mUser_id.equals(document.get("hostid").toString())) {
                                                                                                    //Add into arraylist<Event>
                                                                                                    String event_id = document.getId();
                                                                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                                                                    eventList.add(events);
                                                                                                    eventListAdapter.notifyDataSetChanged();
                                                                                                    //4. Sort eventList by timestamp
                                                                                                    Log.d("categkey", "Finished looping and begin sorting by most recent");
                                                                                                    Collections.sort(eventList);
                                                                                                    Collections.reverse(eventList);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    } else {

                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                    break;



                                                            }



                                                        }

                                                        //mProgress.dismiss();

                                                        if(cArray.length==0){
                                                            Toast toast = Toast.makeText(getApplicationContext(),"Please select at least 1 event category.",Toast.LENGTH_SHORT);
                                                            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }
                                                        if(ppArray.length==0){
                                                            Toast toast = Toast.makeText(getApplicationContext(),"Please toggle an event type or both.",Toast.LENGTH_SHORT);
                                                            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }

                                                    } else {
                                                        Log.d("olo", "No such document");
                                                    }
                                                } else {
                                                    Log.d("olo", "get failed with ", task.getException());
                                                }
                                            }


                                        });//End



                                    } else {
                                    Log.d("olo", "No such document");
                                }
                            } else {
                                Log.d("olo", "get failed with ", task.getException());
                            }
                        }


                    });


                    }



                });





        /*mFirestore.collection("events").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }

                //Change all this to "Pull to refresh"
                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                        String event_id = change.getDocument().getId();
                        Log.d("GETID at EventFrag", event_id);

                        Event events = change.getDocument().toObject(Event.class).withId(event_id);
                        eventList.add(0,events);//add new events whenever there is a change
                        Collections.sort(eventList);
                        Collections.reverse(eventList);
                        eventListAdapter.notifyItemInserted(0);
                        //mEventList.smoothScrollToPosition(0);
                        //eventListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });*/

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgress.dismiss();
    }
}
