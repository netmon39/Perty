package com.example.netipol.perty.Home;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Friend.FriendReqListAdapter;
import com.example.netipol.perty.Profile.FriendFragment;
import com.example.netipol.perty.Profile.NotificationFragment;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import com.example.netipol.perty.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    private RecyclerView mEventList, mEventList2, mFriendReqList;
    private List<Event> eventList, eventList2;
    private List<FriendReq> friendreqList;
    private EventListAdapter eventListAdapter, eventListAdapter2;
    private FriendReqListAdapter friendreqListAdapter;
    private FirebaseFirestore mFirestore;
    private ProgressDialog mProgress;

    private List<String> titleList, nameList;
    private String searchText;
    private SearchView mSearchView;
    private Fragment mFragment;
    public String currentUsr;
    public TextView found_users, found_events, no_matches;
    public String mUser_id;
    public List<String> friendsList;
    public boolean friendsListEmpty = false;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);

        mFirestore = FirebaseFirestore.getInstance();

        titleList = new ArrayList<>();//search for event
        nameList = new ArrayList<>();//search for user
        friendsList = new ArrayList<>();


        mProgress = new ProgressDialog(getActivity());
        mUser_id = Profile.getCurrentProfile().getId();

        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(), eventList,getFragmentManager());
        mEventList = v.findViewById(R.id.result_list_events);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        found_events = v.findViewById(R.id.found_events);
        found_users = v.findViewById(R.id.found_users);
        no_matches = v.findViewById(R.id.no_matches);
        no_matches.setVisibility(View.GONE);
        found_users.setVisibility(View.GONE);
        found_events.setVisibility(View.GONE);

        /*eventList2 = new ArrayList<>();
        eventListAdapter2 = new EventListAdapter(getApplicationContext(), eventList2,getFragmentManager());
        mEventList2 = v.findViewById(R.id.result_list_users);
        mEventList2.setHasFixedSize(true);
        mEventList2.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList2.setAdapter(eventListAdapter2);*/

        NotificationFragment notiFrag = new NotificationFragment();
        FriendFragment friendFrag = new FriendFragment();

        friendreqList = new ArrayList<>();
        friendreqListAdapter = new FriendReqListAdapter(getApplicationContext(),friendreqList, notiFrag, friendFrag, 2, getFragmentManager());
        //RecyclerView setup
        mFriendReqList = v.findViewById(R.id.result_list_users);
        mFriendReqList.setHasFixedSize(true);
        mFriendReqList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mFriendReqList.setAdapter(friendreqListAdapter);//to fill recycler view with Events

        //check if already requested once
        mFirestore.collection("users")
                .document(Profile.getCurrentProfile().getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentUsr = document.get("username").toString();
                    Log.d("usrname", currentUsr);
                } else {
                    //requestJoin.setEnabled(true);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle("Search");
        bar.setDisplayHomeAsUpEnabled(false);

    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.searchv:
                mFragment = new ExploreFragment();
                //mBundle = new Bundle();
                //mBundle.putString("host_id",eventHostId);
                //mFragment.setArguments(mBundle);
                //intent to view stranger's profile
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, mFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.searchv);
        final SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //Toast.makeText(ScrollingActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Toast.makeText(ScrollingActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();
                return true;
            }
        });
        item.expandActionView();//make searchview active at fragment initiation
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                //searchText = mSearchView.getQuery().toString();mSearchField.getText().toString();

                /*Toast toast = Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();*/

                eventList.clear();
                titleList.clear();
                friendreqList.clear();
                nameList.clear();
                no_matches.setVisibility(View.GONE);
                found_users.setVisibility(View.GONE);
                found_events.setVisibility(View.GONE);

                mProgress.setMessage("Searching...");
                mProgress.show();

                mFirestore.collection("users").document(mUser_id).collection("friends")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (!task.getResult().isEmpty()) {//if have friends

                                    Log.d("ddog", "has friends");
                                    friendsListEmpty = false;

                                    if (task.isSuccessful()) {

                                        for (DocumentSnapshot document : task.getResult()) {
                                            //Convert to lowercase
                                            friendsList.add(document.get("uid").toString());
                                            Log.d("ddog", "friendsList size: " + Integer.toString(friendsList.size()));
                                        }
                                    }

                                    } else {//if dont have friends
                                        friendsListEmpty = true;
                                        Log.d("ddog", "no friends");
                                    }

                                //get list of joined events and store it into a arraylist
                                mFirestore.collection("events")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        //Convert to lowercase
                                                        titleList.add(document.get("title").toString().toLowerCase());
                                                        Log.d("ddog", "titleList size: "+Integer.toString(titleList.size()));
                                                    }

                                                    CollectionReference eventsRef = mFirestore.collection("events");
                                                    eventsRef.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

                                                        @Override
                                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                                            if (e != null) {
                                                                Log.d("ddog", "Error : " + e.getMessage());
                                                            }

                                                            for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {//getDocuments) {

                                                                //for (int i = 0; i < titleList.size(); i++) {

                                                                if (doc.get("title").toString().toLowerCase().contains(query.toLowerCase()) && !doc.get("hostid").toString().toLowerCase().equals(mUser_id) && doc.get("type").toString().equals("Public")) {

                                                                    String event_id = doc.getId();
                                                                    Log.d("GETID at SearchFrag", event_id);
                                                                    Event events = doc.toObject(Event.class).withId(event_id);
                                                                    //eventList.add(0,events);//add new events whenever there is a change
                                                                    //eventListAdapter.notifyItemInserted(0);
                                                                    eventList.add(events);
                                                                    eventListAdapter.notifyDataSetChanged();

                                                                }else if(doc.get("title").toString().toLowerCase().contains(query.toLowerCase()) && !doc.get("hostid").toString().toLowerCase().equals(mUser_id) && doc.get("type").toString().equals("Private") && friendsListEmpty==false) {

                                                                    for(int i=0;i<friendsList.size();i++){
                                                                        if(friendsList.get(i).equals(doc.get("hostid").toString())) {
                                                                            //Add into arraylist<Event>
                                                                            String event_id = doc.getId();
                                                                            Log.d("GETID at SearchFrag", event_id);
                                                                            Event events = doc.toObject(Event.class).withId(event_id);
                                                                            //eventList.add(0,events);//add new events whenever there is a change
                                                                            //eventListAdapter.notifyItemInserted(0);
                                                                            eventList.add(events);
                                                                            eventListAdapter.notifyDataSetChanged();
                                                                        }
                                                                    }

                                                                }
                                                                //}
                                                            }

                                                            if(!eventList.isEmpty()) {
                                                                found_events.setVisibility(View.VISIBLE);
                                                                no_matches.setVisibility(View.GONE);
                                                            }
                                                            if(eventList.isEmpty() && friendreqList.isEmpty()){
                                                                no_matches.setVisibility(View.VISIBLE);
                                                            }

                                                            mProgress.dismiss();

                                                        }

                                                    });
                                                }
                                            }
                                        });

                                mFirestore.collection("users")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        //Convert to lowercase
                                                        nameList.add(document.get("username").toString().toLowerCase());
                                                        Log.d("ddog", "nameList size: "+Integer.toString(nameList.size()));
                                                    }

                                                    CollectionReference eventsRef = mFirestore.collection("users");
                                                    eventsRef.orderBy("username", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

                                                        @Override
                                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                                            if (e != null) {
                                                                Log.d("ddog", "Error : " + e.getMessage());
                                                            }

                                                            for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {//getDocuments) {

                                                                //for (int i = 0; i < titleList.size(); i++) {

                                                                if (doc.get("username").toString().toLowerCase().contains(query.toLowerCase()) && !doc.get("username").toString().toLowerCase().equals(currentUsr.toLowerCase())) {

                                                                    Log.d("usrname", doc.get("username").toString().toLowerCase()+", "+currentUsr.toLowerCase());

                                                    /*String event_id = doc.getId();
                                                    Log.d("GETID at SearchFrag", event_id);
                                                    Event events = doc.toObject(Event.class).withId(event_id);
                                                    //eventList.add(0,events);//add new events whenever there is a changeb

                                                    //eventListAdapter.notifyItemInserted(0);
                                                    eventList2.add(events);
                                                    eventListAdapter2.notifyDataSetChanged();*/
                                                                    String friendreq_id = doc.getId();
                                                                    FriendReq events = doc.toObject(FriendReq.class).withId(friendreq_id);
                                                                    friendreqList.add(events);
                                                                    friendreqListAdapter.notifyDataSetChanged();

                                                                }
                                                                //}
                                                            }

                                                            if(!friendreqList.isEmpty()){
                                                                found_users.setVisibility(View.VISIBLE);
                                                                no_matches.setVisibility(View.GONE);
                                                            }
                                                            if(eventList.isEmpty() && friendreqList.isEmpty()){
                                                                no_matches.setVisibility(View.VISIBLE);
                                                            }

                                                            mProgress.dismiss();
                                                        }

                                                    });
                                                }
                                            }
                                        });

                                eventList.clear();
                                titleList.clear();
                                friendreqList.clear();
                                nameList.clear();
                                searchView.clearFocus();

                            }


                        });


                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {


                                          }
                                      }
        );
    }
}