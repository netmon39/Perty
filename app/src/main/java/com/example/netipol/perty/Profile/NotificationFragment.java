package com.example.netipol.perty.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.Model.FriendReq;
import com.example.netipol.perty.R;
import com.example.netipol.perty.Util.EventListAdapter;
import com.example.netipol.perty.Util.FriendReqID;
import com.example.netipol.perty.Util.FriendReqListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import com.facebook.Profile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class NotificationFragment extends Fragment{

    private RecyclerView mFriendReqList;
    private FirebaseFirestore mFirestore;
    private List<FriendReq> friendreqList;//List that stores Events
    private FriendReqListAdapter friendreqListAdapter;

    private static final String TAG = "TabNotiFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_tab,container,false);

        //Friend Requests

        friendreqList = new ArrayList<>();
        friendreqListAdapter = new FriendReqListAdapter(getApplicationContext(),friendreqList);

        //RecyclerView setup
        mFriendReqList = v.findViewById(R.id.notice_list);
        mFriendReqList.setHasFixedSize(true);
        mFriendReqList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mFriendReqList.setAdapter(friendreqListAdapter);//to fill recycler view with Events

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("users").document(Profile.getCurrentProfile().getId().toString()).collection("requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }


                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                        String friendreq_id = change.getDocument().getId();
                        Log.d("GETID at EventFrag", friendreq_id);

                        FriendReq events = change.getDocument().toObject(FriendReq.class).withId(friendreq_id);

                        friendreqList.add(events);//add new events whenever there is a change

                        friendreqListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });



        return v;
    }
}
