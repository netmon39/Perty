package com.example.netipol.perty.Home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.Util.EventListAdapter;
import com.example.netipol.perty.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private RecyclerView mEventList;
    private FirebaseFirestore mFirestore;
    private List<Event> eventList;//List that stores Events
    private EventListAdapter eventListAdapter;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList);

        //RecyclerView setup
        mEventList = v.findViewById(R.id.event_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mEventList.setAdapter(eventListAdapter);//to fill recycler view with Events

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("events").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }


                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                        String event_id = change.getDocument().getId();
                        Log.d("GETID at EventFrag", event_id);

                        Event events = change.getDocument().toObject(Event.class).withId(event_id);

                        eventList.add(events);//add new events whenever there is a change

                        eventListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

        return v;
    }

}
