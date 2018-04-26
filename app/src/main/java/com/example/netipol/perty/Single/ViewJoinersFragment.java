package com.example.netipol.perty.Single;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Friend.FriendReqListAdapter;
import com.example.netipol.perty.Profile.FriendFragment;
import com.example.netipol.perty.Profile.NotificationFragment;
import com.example.netipol.perty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewJoinersFragment extends Fragment {

    private List<FriendReq> friendreqList;
    private FriendReqListAdapter friendreqListAdapter;
    private RecyclerView mFriendReqList;
    private FirebaseFirestore mFirestore;
    private String mPost_id;
    private TextView noJoin;


    public ViewJoinersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_joiners, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        final NotificationFragment notiFrag = new NotificationFragment();
        FriendFragment friendFrag = new FriendFragment();

        friendreqList = new ArrayList<>();
        friendreqListAdapter = new FriendReqListAdapter(getApplicationContext(),friendreqList, notiFrag, friendFrag, 2, getFragmentManager());
        //RecyclerView setup
        mFriendReqList = v.findViewById(R.id.result_list_joiners);
        mFriendReqList.setHasFixedSize(true);
        mFriendReqList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mFriendReqList.setAdapter(friendreqListAdapter);//to fill recycler view with Events

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPost_id = bundle.getString("event_id");
        }

        noJoin = v.findViewById(R.id.no_joiners);
        noJoin.setVisibility(View.GONE);

        CollectionReference eventsRef = mFirestore.collection("events").document(mPost_id).collection("joiners");
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d("ddog", "Error : " + e.getMessage());
                }

                for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {//getDocuments) {

                        String friendreq_id = doc.getId();
                        FriendReq events = doc.toObject(FriendReq.class).withId(friendreq_id);
                        friendreqList.add(events);
                        friendreqListAdapter.notifyDataSetChanged();
                    //}
                }

                if(friendreqList.isEmpty()){
                    //no joiners
                    noJoin.setVisibility(View.VISIBLE);
                }

            }

        });
        return v;
    }

}
