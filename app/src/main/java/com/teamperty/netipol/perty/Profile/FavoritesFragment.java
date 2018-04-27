package com.teamperty.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.teamperty.netipol.perty.Event.Event;
import com.teamperty.netipol.perty.Event.EventListAdapter;
import com.teamperty.netipol.perty.Home.MainActivity;
import com.teamperty.netipol.perty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 09/02/2018.
 */

public class FavoritesFragment extends Fragment {//implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "TabHistFragment";

    private RecyclerView mEventList;
    private List<Event> eventList;//clear this
    private List<String> favList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;
    private String mUser_id;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView notfaved;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    public FavoritesFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_tab,container,false);

        MainActivity activity = (MainActivity) getActivity();
        mUser_id = activity.getHostId();

        Log.d("hello", "data: "+mUser_id);

        eventList = new ArrayList<>();
        favList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList,getActivity().getSupportFragmentManager(),1);

        mEventList = view.findViewById(R.id.fav_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        notfaved = view.findViewById(R.id.notfaved);
        notfaved.setVisibility(View.VISIBLE);

        /*/ SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container3);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });*/

        mFirestore = FirebaseFirestore.getInstance();

        //get list of joined events and store it into a arraylist
        mFirestore.collection("users").document(mUser_id).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                favList.add(document.get("eid").toString());
                            }

                            if(!favList.isEmpty()){
                                notfaved.setVisibility(View.INVISIBLE);
                                CollectionReference eventsRef = mFirestore.collection("events");
                                eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d("Upcoming", "Error : " + e.getMessage());
                                        }

                                        for (DocumentSnapshot doc : documentSnapshots.getDocuments()){//getDocuments) {

                                            for (int i = 0; i < favList.size(); i++) {

                                                if (doc.getId().equals(favList.get(i).toString())) {//&& change.getType() == DocumentChange.Type.ADDED) {

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
                            }else {
                                notfaved.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });

        return view;
    }

    /*@Override
    public void onRefresh() {

        // Fetching data from server
        loadRecyclerViewData();
    }

    public void loadRecyclerViewData()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        eventList.clear();
        favList.clear();

        mFirestore = FirebaseFirestore.getInstance();

        //get list of joined events and store it into a arraylist
        mFirestore.collection("users").document(mUser_id).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                favList.add(document.get("eid").toString());
                            }

                            CollectionReference eventsRef = mFirestore.collection("events");
                            eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d("Upcoming", "Error : " + e.getMessage());
                                    }

                                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()){//getDocuments) {

                                        for (int i = 0; i < favList.size(); i++) {

                                            if (doc.getId().equals(favList.get(i).toString())) {//&& change.getType() == DocumentChange.Type.ADDED) {

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


        mSwipeRefreshLayout.setRefreshing(false);
    }*/
}

