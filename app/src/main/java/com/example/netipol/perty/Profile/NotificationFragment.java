package com.example.netipol.perty.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.R;
import com.example.netipol.perty.Util.EventListAdapter;
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

    private RecyclerView mEventList;
    private List<Event> eventList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;
    private String mUser_id;

    private static final String TAG = "TabNotiFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_tab,container,false);

        mFirestore = FirebaseFirestore.getInstance();
        mUser_id = Profile.getCurrentProfile().getId();

        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList);

        mEventList = v.findViewById(R.id.notice_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        CollectionReference eventsRef = mFirestore.collection("events");
        Query query = eventsRef.whereEqualTo("host", mUser_id);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }

                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                        String event_id = change.getDocument().getId();
                        Log.d("GETID at SearchFrag", event_id);
                        Event events = change.getDocument().toObject(Event.class).withId(event_id);
                        eventList.add(events);

                        eventListAdapter.notifyDataSetChanged();

                    }
                }

            }
        });

        return v;
    }
}
