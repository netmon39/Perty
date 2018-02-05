package com.example.netipol.perty.Home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.Event;
import com.example.netipol.perty.EventListAdapter;
import com.example.netipol.perty.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private RecyclerView mEventList;
    private FirebaseFirestore mFirestore;
    private List<Event> eventList;
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
        eventListAdapter = new EventListAdapter(eventList);

        mEventList = (RecyclerView) v.findViewById(R.id.event_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null){
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }

                for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                    if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                        Event events = change.getDocument().toObject(Event.class);
                        eventList.add(events);

                        eventListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

        return v;
    }

}
