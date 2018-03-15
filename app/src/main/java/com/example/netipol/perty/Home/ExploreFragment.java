package com.example.netipol.perty.Home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private RecyclerView mExpFeat, mExpPop, mExpRec;
    private FirebaseFirestore mFirestore;
    private List<Event> expFeatList, expPopList, expRecList;
    private List<String> evntList;//List that stores Events
    private EventListAdapter expFeatListAdapter, expPopListAdapter, expRecListAdapter;
    private String mPost_id, event_doc_id, event_doc_1, event_doc_2, event_doc_3;
    private Random rand;
    private int randomNum;
    public TextView exp1, exp2, exp3;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        exp1 = v.findViewById(R.id.txt1);
        expFeatList = new ArrayList<>();
        expFeatListAdapter = new EventListAdapter(getApplicationContext(), expFeatList,getFragmentManager());

        exp2 = v.findViewById(R.id.txt2);
        expPopList = new ArrayList<>();
        expPopListAdapter = new EventListAdapter(getApplicationContext(), expPopList,getFragmentManager());

        exp3 = v.findViewById(R.id.txt3);
        expRecList = new ArrayList<>();
        expRecListAdapter = new EventListAdapter(getApplicationContext(), expRecList, getFragmentManager());

        rand = new Random();

        /*
        * Popular
        * Recommended from Sports
        * Recommend from Education
        * Recommended from ...
        * */

        //Featured: 5 randomly selected featured (sponsored?) Events
        mExpFeat = v.findViewById(R.id.explore_list_featured);
        mExpFeat.setHasFixedSize(true);
        mExpFeat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));//Main Activity
        mExpFeat.setAdapter(expFeatListAdapter);//to fill recycler view with Events
        exp1.setText("Latest and Greatest");

        //Popular: Query top 5 public events with most joining members (events>[eventId]>joining.count)
        mExpPop = v.findViewById(R.id.explore_list_popular);
        mExpPop.setHasFixedSize(true);
        mExpPop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));//Main Activity
        mExpPop.setAdapter(expPopListAdapter);//to fill recycler view with Events
        exp2.setText("Recommended from MUSIC");


        //Recommended: 5 ranndomly selected according to user's categ_key vs. all event's categ
        mExpRec = v.findViewById(R.id.explore_list_recommend);
        mExpRec.setHasFixedSize(true);
        mExpRec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));//Main Activity
        mExpRec.setAdapter(expRecListAdapter);//to fill recycler view with Events
        exp3.setText("Popular from EDUCATION");

        mFirestore.collection("events").orderBy("timestamp", Query.Direction.DESCENDING).limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String event_id = document.getId();
                                event_doc_1 = event_id;
                                Event events = document.toObject(Event.class).withId(event_id);
                                expFeatList.add(events);//add new events whenever there is a change
                                expFeatListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        mFirestore.collection("events").whereEqualTo("categ","MUSIC").limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String event_id = document.getId();
                                event_doc_2 = event_id;
                                Event events = document.toObject(Event.class).withId(event_id);
                                expPopList.add(events);//add new events whenever there is a change
                                expPopListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        mFirestore.collection("events").whereEqualTo("categ","EDUCATION").limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String event_id = document.getId();
                                event_doc_3 = event_id;
                                Event events = document.toObject(Event.class).withId(event_id);
                                expRecList.add(events);//add new events whenever there is a change
                                expRecListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomNum = rand.nextInt((3 - 1) + 1) + 1;
                Log.d("random", String.valueOf(randomNum));

                switch(randomNum){
                    case 1://Student
                        event_doc_id = event_doc_1;
                        break;
                    case 2://Prof
                        event_doc_id = event_doc_2;
                        break;
                    case 3://Club
                        event_doc_id = event_doc_3;
                        break;
                }

                Intent singleEventIntent = new Intent(getApplicationContext(), SingleEventActivity.class);
                singleEventIntent.putExtra("event_id", event_doc_id);
                singleEventIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(singleEventIntent);
            }
        });

        return v;
    }

}
