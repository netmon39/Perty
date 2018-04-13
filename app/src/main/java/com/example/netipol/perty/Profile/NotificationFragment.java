package com.example.netipol.perty.Profile;


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

import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.R;
import com.example.netipol.perty.Friend.FriendReqListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;


import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mFriendReqList;
    private FirebaseFirestore mFirestore;
    private List<FriendReq> friendreqList;//List that stores Events
    private FriendReqListAdapter friendreqListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        friendreqList.clear();

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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
