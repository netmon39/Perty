package com.teamperty.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamperty.netipol.perty.Event.Event;
import com.teamperty.netipol.perty.Home.MainActivity;
import com.teamperty.netipol.perty.R;
import com.teamperty.netipol.perty.Event.EventListAdapter;
import com.facebook.Profile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class HostFragment  extends android.support.v4.app.Fragment{
    private static final String TAG = "TabHostFragment";

    private RecyclerView mEventList;
    private List<Event> eventList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;
    private String mUser_id;
    private Query query;
    private TextView notHosted;

    public static HostFragment newInstance() {
        HostFragment fragment = new HostFragment();
        return fragment;
    }

    public HostFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_host_tab,container,false);

        mFirestore = FirebaseFirestore.getInstance();

        MainActivity activity = (MainActivity) getActivity();
        mUser_id = activity.getHostId();

        CollectionReference eventsRef = mFirestore.collection("events");

        if(mUser_id.equals(Profile.getCurrentProfile().getId())){//as self
            query = eventsRef.whereEqualTo("hostid", mUser_id);
        }else{
            query = eventsRef.whereEqualTo("hostid", mUser_id).whereEqualTo("type","Public");
        }

        Log.d("hello", "data: "+mUser_id);

        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList,getActivity().getSupportFragmentManager(),1);

        mEventList = v.findViewById(R.id.hosting_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        notHosted = v.findViewById(R.id.nothosted);
        notHosted.setVisibility(View.VISIBLE);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }
                //for(DocumentChange change : documentSnapshots.getDocumentChanges()) {
                for(DocumentSnapshot change : documentSnapshots.getDocuments()){

                    //if (change.getType() == DocumentChange.Type.ADDED) { //MODIFIED, REMOVED ??

                        String event_id = change.getId();
                        Log.d("GETID at HostFrag", event_id);
                        Event events = change.toObject(Event.class).withId(event_id);
                        eventList.add(events);
                        eventListAdapter.notifyDataSetChanged();
                        Collections.sort(eventList);
                        Collections.reverse(eventList);
                   // }
                }

                if(!eventList.isEmpty()){
                    notHosted.setVisibility(View.INVISIBLE);
                }else{
                    notHosted.setVisibility(View.VISIBLE);
                }
            }
        });

        return v;
    }

}
