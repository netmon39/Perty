package com.example.netipol.perty.Profile;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Friend.FriendReqListAdapter;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mFriendReqList;
    private FirebaseFirestore mFirestore;
    private List<FriendReq> friendreqList;//List that stores Events
    private FriendReqListAdapter friendreqListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    public FriendFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend, container, false);

        NotificationFragment notiFrag = new NotificationFragment();

        friendreqList = new ArrayList<>();
        friendreqListAdapter = new FriendReqListAdapter(getApplicationContext(),friendreqList, notiFrag,FriendFragment.this, 1);

        //RecyclerView setup
        mFriendReqList = v.findViewById(R.id.friendlist);
        mFriendReqList.setHasFixedSize(true);
        mFriendReqList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mFriendReqList.setAdapter(friendreqListAdapter);//to fill recycler view with Events

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container2);
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

        return v;
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

        mFirestore.collection("users").document(Profile.getCurrentProfile().getId()).collection("friends")
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
