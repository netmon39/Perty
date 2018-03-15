package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.R;
import com.example.netipol.perty.Event.EventListAdapter;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class UpcomingFragment extends Fragment {
    private static final String TAG = "TabUPFragment";

    private RecyclerView mEventList;
    private List<Event> eventList;
    private List<String> joinList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;
    private String mUser_id;

    public static UpcomingFragment newInstance() {
        UpcomingFragment fragment = new UpcomingFragment();
        return fragment;
    }

    public UpcomingFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upcoming_tab, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mUser_id = Profile.getCurrentProfile().getId();

        eventList = new ArrayList<>();
        joinList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList, getActivity().getSupportFragmentManager());

        mEventList = v.findViewById(R.id.upcoming_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        //get list of joined events and store it into a arraylist
        mFirestore.collection("users").document(mUser_id).collection("joining")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                joinList.add(document.get("eid").toString());
                            }

                            CollectionReference eventsRef = mFirestore.collection("events");
                            eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d("Upcoming", "Error : " + e.getMessage());
                                    }

                                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()){//getDocuments) {

                                        for (int i = 0; i < joinList.size(); i++) {

                                            if (doc.getId().equals(joinList.get(i).toString())) {//&& change.getType() == DocumentChange.Type.ADDED) {

                                                String event_id = doc.getId();
                                                Log.d("GETID at UpcomingFrag", event_id);
                                                Event events = doc.toObject(Event.class).withId(event_id);
                                                eventList.add(events);
                                                eventListAdapter.notifyDataSetChanged();
                                            }
                                        }

                                    }
                                }

                            });
                        }
                    }
                });



        return v;
    }
}
