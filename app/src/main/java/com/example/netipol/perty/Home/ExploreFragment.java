package com.example.netipol.perty.Home;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Profile.ProfileFragment;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private RecyclerView mExpA, mExpB, mExpC;
    private FirebaseFirestore mFirestore;
    private List<Event> expAList, expBList, expCList;
    private List<String> chosenOnesA, allOfA, chosenOnesB, allOfB, chosenOnesC, allOfC, categList, randomPicks;//List that stores Events
    private EventListAdapter expAListAdapter, expBListAdapter, expCListAdapter;
    private String mPost_id, event_doc_id, event_doc_A, event_doc_B, event_doc_C;
    private Random rand;
    private int randomNum;
    public TextView expA, expB, expC;
    private Fragment mFragment;
    private Bundle mBundle;
    public FragmentManager fManager;
    private ProgressDialog mProgress;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mProgress = new ProgressDialog(getActivity());

        setHasOptionsMenu(true);

        expA = v.findViewById(R.id.txta);
        expAList = new ArrayList<>();
        expAListAdapter = new EventListAdapter(getApplicationContext(), expAList,getFragmentManager());

        expB = v.findViewById(R.id.txtb);
        expBList = new ArrayList<>();
        expBListAdapter = new EventListAdapter(getApplicationContext(), expBList,getFragmentManager());

        expC = v.findViewById(R.id.txtc);
        expCList = new ArrayList<>();
        expCListAdapter = new EventListAdapter(getApplicationContext(), expCList, getFragmentManager());

        chosenOnesA = new ArrayList<>();
        chosenOnesB = new ArrayList<>();
        chosenOnesC = new ArrayList<>();
        allOfA = new ArrayList<>();
        allOfB = new ArrayList<>();
        allOfC = new ArrayList<>();
        categList = new ArrayList<>();
        categList.add("SPORTS");
        categList.add("EDUCATION");
        categList.add("RECREATION");
        categList.add("MUSIC");
        categList.add("ART");
        categList.add("THEATRE");
        categList.add("TECHNOLOGY");
        categList.add("OUTING");
        categList.add("CAREER");

        /*
        * 1. Create random categ chooser -> 3 categs
        * 2. Query the 3 categs; Public only
        * 3. Randomly select 3 events of each categ
        * 4. populate recyler views A, B, C
        * */

        randomPicks = pickRandom(categList, 3);
        Log.d("explorer","Size: "+String.valueOf(randomPicks.size()));
        Log.d("explorer",randomPicks.get(0));
        Log.d("explorer",randomPicks.get(1));
        Log.d("explorer",randomPicks.get(2));
        populateExpA(randomPicks.get(0));
        populateExpB(randomPicks.get(1));
        populateExpC(randomPicks.get(2));

        //Featured: 5 randomly selected featured (sponsored?) Events
        mExpA = v.findViewById(R.id.explore_list_a);
        mExpA.setHasFixedSize(true);
        mExpA.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));//Main Activity
        mExpA.setAdapter(expAListAdapter);//to fill recycler view with Events
        expA.setText("Recommended from "+randomPicks.get(0));

        //Popular: Query top 5 public events with most joining members (events>[eventId]>joining.count)
        mExpB = v.findViewById(R.id.explore_list_b);
        mExpB.setHasFixedSize(true);
        mExpB.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));//Main Activity
        mExpB.setAdapter(expBListAdapter);//to fill recycler view with Events
        expB.setText("Recommended from "+randomPicks.get(1));


        //Recommended: 5 ranndomly selected according to user's categ_key vs. all event's categ
        mExpC = v.findViewById(R.id.explore_list_c);
        mExpC.setHasFixedSize(true);
        mExpC.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));//Main Activity
        mExpC.setAdapter(expCListAdapter);//to fill recycler view with Events
        expC.setText("Recommended from "+randomPicks.get(2));

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomPicks = pickRandom(categList, 3);
                Log.d("explorer","Random Size: "+String.valueOf(randomPicks.size()));
                Log.d("explorer",randomPicks.get(0));
                Log.d("explorer",randomPicks.get(1));
                Log.d("explorer",randomPicks.get(2));
                populateExpA(randomPicks.get(0));
                populateExpB(randomPicks.get(1));
                populateExpC(randomPicks.get(2));
                expA.setText("Recommended from "+randomPicks.get(0));
                expB.setText("Recommended from "+randomPicks.get(1));
                expC.setText("Recommended from "+randomPicks.get(2));
            }
        });

        return v;
    }

    public static List<String> pickRandom(List<String> lst, int n) {
        List<String> copy = new LinkedList<String>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    public void populateExpA(String categ){

        mProgress.setMessage("Finding events for you ...");
        mProgress.show();

        allOfA.clear();
        chosenOnesA.clear();
        expAList.clear();

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("events")
                .whereEqualTo("categ", categ)
                .whereEqualTo("type", "Public")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("olo", document.getId() + " => " + document.getData());
                                if (!document.get("hostid").equals(Profile.getCurrentProfile().getId())){
                                    allOfA.add(document.getId());//all events
                                }
                            }
                        }

                        if(!allOfA.isEmpty()){
                            chosenOnesA = pickRandom(allOfA, 1);

                            mFirestore.collection("events")
                                    .document(chosenOnesA.get(0))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String event_id = document.getId();
                                                event_doc_A = event_id;
                                                Event events = document.toObject(Event.class).withId(event_id);
                                                expAList.add(events);//add new events whenever there is a change
                                                expAListAdapter.notifyDataSetChanged();
                                            }

                                            mProgress.dismiss();

                                        }
                                    });

                            /*mFirestore.collection("events")
                                .document(chosenOnesA.get(1))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            String event_id = document.getId();
                                            event_doc_1 = event_id;
                                            Event events = document.toObject(Event.class).withId(event_id);
                                            expAList.add(events);//add new events whenever there is a change
                                            expAListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                        mFirestore.collection("events")
                                .document(chosenOnesA.get(2))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            String event_id = document.getId();
                                            event_doc_1 = event_id;
                                            Event events = document.toObject(Event.class).withId(event_id);
                                            expAList.add(events);//add new events whenever there is a change
                                            expAListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                        Log.d("explorer","Size of expA: "+String.valueOf(chosenOnesA.size()));
                        Log.d("explorer",chosenOnesA.get(0));
                        Log.d("explorer",chosenOnesA.get(1));
                        Log.d("explorer",chosenOnesA.get(2));
                        */

                        }else{
                            expAListAdapter.notifyDataSetChanged();
                            mProgress.dismiss();
                        }


                    }

                });
        allOfA.clear();
        chosenOnesA.clear();
        expAList.clear();
    }


    public void populateExpB(String categ){

        allOfB.clear();
        chosenOnesB.clear();
        expBList.clear();

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("events")
                .whereEqualTo("categ", categ)
                .whereEqualTo("type", "Public")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("olo", document.getId() + " => " + document.getData());
                                if (!document.get("hostid").equals(Profile.getCurrentProfile().getId())){
                                    allOfB.add(document.getId());//all events
                                }
                            }


                        }

                        if(!allOfB.isEmpty()){
                            chosenOnesB = pickRandom(allOfB, 1);
                            mFirestore.collection("events")
                                    .document(chosenOnesB.get(0))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String event_id = document.getId();
                                                event_doc_B = event_id;
                                                Event events = document.toObject(Event.class).withId(event_id);
                                                expBList.add(events);//add new events whenever there is a change
                                                expBListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });

                        }else{
                            expBListAdapter.notifyDataSetChanged();
                        }

                    }

                });
        allOfB.clear();
        chosenOnesB.clear();
        expBList.clear();
    }


    public void populateExpC(String categ){

        allOfC.clear();
        chosenOnesC.clear();
        expCList.clear();

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("events")
                .whereEqualTo("categ", categ)
                .whereEqualTo("type", "Public")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("olo", document.getId() + " => " + document.getData());
                                if (!document.get("hostid").equals(Profile.getCurrentProfile().getId())){
                                    allOfC.add(document.getId());//all events
                                }
                            }

                            if(!allOfC.isEmpty()){
                                chosenOnesC = pickRandom(allOfC, 1);//change to 3?
                                mFirestore.collection("events")
                                        .document(chosenOnesC.get(0))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    String event_id = document.getId();
                                                    event_doc_C = event_id;
                                                    Event events = document.toObject(Event.class).withId(event_id);
                                                    expCList.add(events);//add new events whenever there is a change
                                                    expCListAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                            }else{
                                expCListAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
        allOfC.clear();
        chosenOnesC.clear();
        expCList.clear();

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle("Explore");
        bar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.explore_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.searchbutt:

                mFragment = new SearchFragment();
                //mBundle = new Bundle();
                //mBundle.putString("host_id",eventHostId);
                //mFragment.setArguments(mBundle);
                //intent to view stranger's profile
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, mFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
