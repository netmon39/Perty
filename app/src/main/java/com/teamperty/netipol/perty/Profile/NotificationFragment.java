package com.teamperty.netipol.perty.Profile;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamperty.netipol.perty.Announce.Announce;
import com.teamperty.netipol.perty.Announce.AnnounceListAdapter;
import com.teamperty.netipol.perty.Friend.FriendReq;
import com.teamperty.netipol.perty.Home.MainActivity;
import com.teamperty.netipol.perty.R;
import com.teamperty.netipol.perty.Friend.FriendReqListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mFriendReqList, mAnnounceList;
    private FirebaseFirestore mFirestore;
    private List<FriendReq> friendreqList;//List that stores Events
    private List<Announce> announceList;
    private AnnounceListAdapter announceListAdapter;
    private FriendReqListAdapter friendreqListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> joiningList;
    private TextView notice_fr, notice_ann, notice_nonoti;
    private ScrollView notiSV;
    private ProgressDialog mProgress;

    private static final String TAG = "TabNotiFragment";

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    public NotificationFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_tab,container,false);

        FriendFragment friendfrag = new FriendFragment();
        //Friend Requests

        joiningList = new ArrayList<>();

        notiSV = v.findViewById(R.id.notiSV);

        mProgress = new ProgressDialog(getActivity());

        announceList = new ArrayList<>();
        announceListAdapter = new AnnounceListAdapter(getApplicationContext(),announceList,getFragmentManager());
        //RecyclerView setup
        mAnnounceList = v.findViewById(R.id.result_list_announce_noti);
        mAnnounceList.setHasFixedSize(true);
        mAnnounceList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mAnnounceList.setAdapter(announceListAdapter);//to fill recycler view with Events

        friendreqList = new ArrayList<>();
        friendreqListAdapter = new FriendReqListAdapter(getApplicationContext(),friendreqList,NotificationFragment.this, friendfrag, 0, getFragmentManager());

        //RecyclerView setup
        mFriendReqList = v.findViewById(R.id.notice_list);
        mFriendReqList.setHasFixedSize(true);
        mFriendReqList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mFriendReqList.setAdapter(friendreqListAdapter);//to fill recycler view with Events

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        notice_fr = v.findViewById(R.id.notice_fr);
        notice_ann = v.findViewById(R.id.notice_ann);
        notice_nonoti = v.findViewById(R.id.notice_nonoti);

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

        /*mFirestore.collection("users").document(Profile.getCurrentProfile().getId().toString()).collection("requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }


                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??
                        Log.d("NotiFrag", "ADDED");

                        String friendreq_id = change.getDocument().getId();
                        FriendReq events = change.getDocument().toObject(FriendReq.class).withId(friendreq_id);
                        friendreqList.add(events);//add new events whenever there is a change
                        friendreqListAdapter.notifyDataSetChanged();

                    }else if (change.getType()==DocumentChange.Type.REMOVED){
                        Log.d("NotiFrag", "REMOVED");

                        String friendreq_id = change.getDocument().getId();
                        FriendReq events = change.getDocument().toObject(FriendReq.class).withId(friendreq_id);
                        friendreqList.remove(events);
                        friendreqListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });*/



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle("Notice");
        bar.setDisplayHomeAsUpEnabled(false);

    }

    @Override
    public void onRefresh() {

        // Fetching data from server
        loadRecyclerViewData();
    }

    public void loadRecyclerViewData()
    {
        mSwipeRefreshLayout.setRefreshing(true);
        notiSV.fullScroll(View.FOCUS_UP);

        mProgress.setMessage("Loading your notifications ...");
        mProgress.show();

        friendreqList.clear();
        joiningList.clear();
        announceList.clear();
        notice_fr.setVisibility(View.GONE);
        notice_ann.setVisibility(View.GONE);
        notice_nonoti.setVisibility(View.GONE);
        mAnnounceList.setVisibility(View.GONE);
        mFriendReqList.setVisibility(View.GONE);

        mFirestore = FirebaseFirestore.getInstance();

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
                                friendreqListAdapter.notifyDataSetChanged();
                            }


                            if(!friendreqList.isEmpty()) {
                                notice_fr.setVisibility(View.VISIBLE);
                                mFriendReqList.setVisibility(View.VISIBLE);
                                notice_nonoti.setVisibility(View.GONE);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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
                                                                                        announceListAdapter.notifyDataSetChanged();
                                                                                        Collections.sort(announceList);
                                                                                        Collections.reverse(announceList);
                                                                                    }

                                                                                    if(!announceList.isEmpty()) {
                                                                                        notice_ann.setVisibility(View.VISIBLE);
                                                                                        mAnnounceList.setVisibility(View.VISIBLE);
                                                                                        notice_nonoti.setVisibility(View.GONE);
                                                                                    }
                                                                                    if(announceList.isEmpty() && friendreqList.isEmpty()){
                                                                                        notice_nonoti.setVisibility(View.VISIBLE);
                                                                                    }
                                                                                    mProgress.dismiss();

                                                                                } else {
                                                                                    Log.d("olo", "Error getting documents: ", task.getException());
                                                                                }

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }

                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }else{
                                if(announceList.isEmpty() && friendreqList.isEmpty()){
                                    notice_nonoti.setVisibility(View.VISIBLE);
                                }
                                mProgress.dismiss();
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        mSwipeRefreshLayout.setRefreshing(false);
    }
}
