package com.example.netipol.perty.Single;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.example.netipol.perty.Profile.TutorialActivityAsGuide;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleEventFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public Event model;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SinglePostAct";

    private String mPost_id, mUser_id, reportType, mPost_type;
    private Button requestJoin, whiteBg, reportButt, joiners, announce, end;
    private String eventName, eventHostId, eventHost, singleCategString;
    private Spinner reportDropdown;

    private ProgressDialog mProgress;

    private ImageView singleImage, singleHostProf;
    private TextView singleTitle, singleDesc, singleHost, singleStart, singleEnd, singleLocaPreset, singleLocaDesc, singleCateg, singleEventTitle;

    private Fragment mFragment;
    private Bundle mBundle;
    public FragmentManager fManager;

    private boolean alreadyJoined, check, favboo, favboo2;
    private RelativeLayout eventControl;

    private RecyclerView mRec;
    private EventListAdapter recListAdapter;
    private TextView rec;
    private List<Event> recList;
    private List<String> chosenOnesRec;
    private ActionBar bar;
    private MainActivity activity;

    public SingleEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_event, container, false);

        activity = (MainActivity) getActivity();
        bar = activity.getSupportActionBar();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPost_id = bundle.getString("event_id");
            mPost_type = bundle.getString("event_type");
        }

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        Log.d("eventid", mPost_id);

        setHasOptionsMenu(true);

        mUser_id = Profile.getCurrentProfile().getId();
        //Get user info

        singleImage = (ImageView) v.findViewById(R.id.single_image);
        //singleTitle = (TextView) v.findViewById(R.id.single_title);
        singleDesc = (TextView) v.findViewById(R.id.single_desc);
        singleHost = (TextView) v.findViewById(R.id.single_hostname);

        singleStart = (TextView) v.findViewById(R.id.single_start_dateandtime);
        singleEnd = (TextView) v.findViewById(R.id.single_end_dateandtime);

        singleLocaPreset = (TextView) v.findViewById(R.id.single_loca_preset);
        singleLocaDesc = (TextView) v.findViewById(R.id.single_loca_desc);

        singleCateg = (TextView) v.findViewById(R.id.single_categ);
        singleHostProf = v.findViewById(R.id.single_hostprofile);
        singleEventTitle = v.findViewById(R.id.single_eventtitle);
        whiteBg = (Button) v.findViewById(R.id.single_cheat);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading...");
        mProgress.show();

        rec = v.findViewById(R.id.txtrec);
        recList = new ArrayList<>();
        chosenOnesRec = new ArrayList<>();
        recListAdapter = new EventListAdapter(getApplicationContext(), recList,getFragmentManager(),0);

        //Featured: 5 randomly selected featured (sponsored?) Events
        mRec = v.findViewById(R.id.single_list_rec);
        mRec.setHasFixedSize(true);
        mRec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));//Main Activity
        mRec.setAdapter(recListAdapter);//to fill recycler view with Events

        rec.setVisibility(View.GONE);
        mRec.setVisibility(View.GONE);

        requestJoin = (Button) v.findViewById(R.id.reqJoin);
        requestJoin.setVisibility(View.GONE);
        requestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> joinrequest = new HashMap<>();
                joinrequest.put("uid", mUser_id);

                Map<String, Object> record = new HashMap<>();
                record.put("eid", mPost_id);

                if(alreadyJoined==false){
                    // Add a new pending join request
                    db.collection("events")
                            .document(mPost_id)
                            .collection("joiners")
                            .document(mUser_id)
                            .set(joinrequest);

                    //Add to user's list of Upcoming events;
                    db.collection("users")
                            .document(mUser_id)
                            .collection("joining")
                            .document(mPost_id)
                            .set(record);

                    Toast.makeText(getActivity().getApplicationContext(), "You joined " + eventName, Toast.LENGTH_SHORT).show();
                    requestJoin.setBackground(getResources().getDrawable(R.drawable.btstyle));
                    requestJoin.setText("Unjoin event");
                    alreadyJoined=true;
                }else {
                    // Add a new pending join request
                    db.collection("events")
                            .document(mPost_id)
                            .collection("joiners")
                            .document(mUser_id)
                            .delete();

                    //Add to user's list of Upcoming events;
                    db.collection("users")
                            .document(mUser_id)
                            .collection("joining")
                            .document(mPost_id)
                            .delete();

                    Toast.makeText(getActivity().getApplicationContext(), "You unjoined " + eventName, Toast.LENGTH_SHORT).show();
                    requestJoin.setBackground(getResources().getDrawable(R.drawable.btstyle2));
                    requestJoin.setText("Join event");
                    alreadyJoined=false;
                }
                //requestJoin.setEnabled(false);
            }
        });

        eventControl = v.findViewById(R.id.eventControl_panel);
        eventControl.setVisibility(View.GONE);

        joiners = v.findViewById(R.id.eventControl_joiners);
        announce = v.findViewById(R.id.eventControl_announce);
        end = v.findViewById(R.id.eventControl_end);

        reportButt = v.findViewById(R.id.single_addtofav);
        reportButt.setVisibility(View.GONE);

        if(isConnectedToInternet()){//true
            //allow connection to db
            if(mPost_type.equals("Archived")){

                db.collection("archived")//get stuff to display in detail
                        .document(mPost_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                                    eventName = doc.get("title").toString();
                                    bar.setTitle(eventName);
                                    bar.setDisplayHomeAsUpEnabled(true);

                                    //eventHost = doc.get("host").toString();
                                    eventHostId = doc.get("hostid").toString();
                                    Log.w(TAG, eventHostId, task.getException());

                                    //singleTitle.setText(doc.get("title").toString());
                                    Glide.with(getActivity().getApplicationContext()).load(doc.get("image").toString()).into(singleImage);
                                    singleDesc.setText(doc.get("desc").toString());
                                    //singleHost.setText("get me from db");
                                    singleStart.setText(doc.get("date_start").toString()+", "+doc.get("time_start").toString());
                                    singleEnd.setText(doc.get("date_end").toString()+", "+doc.get("time_end").toString());
                                    singleLocaPreset.setText(doc.get("loca_preset").toString());
                                    singleLocaDesc.setText(doc.get("loca_desc").toString());
                                    singleCateg.setText(doc.get("categ").toString());


                                    db.collection("users")//get stuff to display in detail
                                            .document(eventHostId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot doc = task.getResult();
                                                        Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                                                        //singleTitle.setText(doc.get("title").toString());
                                                        Glide.with(getActivity().getApplicationContext()).load(doc.get("profimage").toString()).into(singleHostProf);
                                                        singleHost.setText(doc.get("username").toString());


                                                    } else {
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });


                                    mProgress.dismiss();
                                    whiteBg.setVisibility(View.GONE);


                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

            }else{
                db.collection("events")//get stuff to display in detail
                        .document(mPost_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                                    eventName = doc.get("title").toString();
                                    bar.setTitle(eventName);
                                    bar.setDisplayHomeAsUpEnabled(true);
                                    singleEventTitle.setText(eventName);

                                    //eventHost = doc.get("host").toString();
                                    eventHostId = doc.get("hostid").toString();
                                    Log.w(TAG, eventHostId, task.getException());

                                    //singleTitle.setText(doc.get("title").toString());
                                    Glide.with(getActivity().getApplicationContext()).load(doc.get("image").toString()).into(singleImage);
                                    singleDesc.setText(doc.get("desc").toString());
                                    //singleHost.setText("get me from db");
                                    singleStart.setText(doc.get("date_start").toString()+", "+doc.get("time_start").toString());
                                    singleEnd.setText(doc.get("date_end").toString()+", "+doc.get("time_end").toString());
                                    singleLocaPreset.setText(doc.get("loca_preset").toString());
                                    singleLocaDesc.setText(doc.get("loca_desc").toString());
                                    singleCateg.setText(doc.get("categ").toString());

                                    singleCategString = doc.get("categ").toString();

                                    db.collection("users")//get stuff to display in detail
                                            .document(eventHostId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot doc = task.getResult();
                                                        Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                                                        //singleTitle.setText(doc.get("title").toString());
                                                        Glide.with(getActivity().getApplicationContext()).load(doc.get("profimage").toString()).into(singleHostProf);
                                                        singleHost.setText(doc.get("username").toString());


                                                    } else {
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });

                                    mProgress.dismiss();
                                    whiteBg.setVisibility(View.GONE);


                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                //check if user is host
                db.collection("events").document(mPost_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());

                                if(mUser_id.equals(eventHostId)){
                                    //check=true;

                                    //requestJoin.setText("You are the host!");
                                    //requestJoin.setEnabled(false);

                            /*send to event control
                            * 1. edit event
                            * 2. delete event
                            * 3. make announcement to joiners
                            *
                             */
                                    eventControl.setVisibility(View.VISIBLE);
                                    joiners.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mFragment = new ViewJoinersFragment();
                                            mBundle = new Bundle();
                                            mBundle.putString("event_id",mPost_id);
                                            mFragment.setArguments(mBundle);
                                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.main_frame, mFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }
                                    });

                                    announce.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mFragment = new AnnouncementFragment();
                                            mBundle = new Bundle();
                                            mBundle.putString("event_id",mPost_id);
                                            mFragment.setArguments(mBundle);
                                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.main_frame, mFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();

                                        }
                                    });

                                    end.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE://delete
                                                            Toast.makeText(getApplicationContext(), "Event completed and archived.",Toast.LENGTH_LONG).show();


                                                /*
                                                1. add event to "archived"
                                                2. delete from all occurrences:

                                                    joiner's "joining" and "favorites"

                                                    "events"

                                                    keep image in storage ->change it so that the image name corresponds with eventid!

                                                3. Query to joiner's "joined" and host's "hosted"


                                                 */
                                                            //Add event to "archived"
                                                            db.collection("events").document(mPost_id)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot document = task.getResult();

                                                                                //If modifying this, mod at SingleEventFrag too.
                                                                                Map<String, Object> event = new HashMap<>();
                                                                                event.put("title", document.get("title").toString());
                                                                                event.put("desc", document.get("desc").toString());
                                                                                event.put("type", "Archived");
                                                                                event.put("categ", document.get("categ").toString());
                                                                                event.put("image", document.get("image").toString());
                                                                                event.put("timestamp", document.get("timestamp").toString());
                                                                                //event.put("host", document.get("host").toString());
                                                                                event.put("time_start", document.get("time_start").toString());
                                                                                event.put("time_end", document.get("time_end").toString());
                                                                                event.put("loca_desc", document.get("loca_desc").toString());
                                                                                event.put("loca_preset", document.get("loca_preset").toString());
                                                                                event.put("date_start", document.get("date_start").toString());
                                                                                event.put("date_end", document.get("date_end").toString());
                                                                                event.put("hostid", document.get("hostid").toString());

                                                                                // Add a new document with a generated ID
                                                                                db.collection("archived")//collection of completed events
                                                                                        .document(document.getId())
                                                                                        .set(event);

                                                                                //Add this event to each joiner's "joined" & Delete from joiner's joining
                                                                                db.collection("events").document(mPost_id).collection("joiners")
                                                                                        .get()
                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    for (DocumentSnapshot document : task.getResult()) {//for each joiner...

                                                                                                        //Delete
                                                                                                        db.collection("users")
                                                                                                                .document(document.getId().toString())
                                                                                                                .collection("joining")
                                                                                                                .document(mPost_id)
                                                                                                                .delete();

                                                                                                        //Add
                                                                                                        Map<String, Object> event = new HashMap<>();
                                                                                                        event.put("eid", mPost_id);//this event

                                                                                                        // Add a new document with a generated ID
                                                                                                        db.collection("users")
                                                                                                                .document(document.getId().toString())//joiner
                                                                                                                .collection("joined")
                                                                                                                .document(mPost_id)
                                                                                                                .set(event);
                                                                                                    }

                                                                                                    //Delete from each user's "favorited"
                                                                                                    db.collection("events").document(mPost_id).collection("favoriters")
                                                                                                            .get()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        for (DocumentSnapshot document : task.getResult()) {//for each user that have favorited this event...

                                                                                                                            //Delete
                                                                                                                            db.collection("users")
                                                                                                                                    .document(document.getId().toString())
                                                                                                                                    .collection("favorites")
                                                                                                                                    .document(mPost_id)
                                                                                                                                    .delete();
                                                                                                                        }


                                                                                                                        //Delete announcements
                                                                                                                        db.collection("events").document(mPost_id).collection("announcements")
                                                                                                                                .get()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                            for (DocumentSnapshot document : task.getResult()) {

                                                                                                                                                //Delete
                                                                                                                                                db.collection("events")
                                                                                                                                                        .document(mPost_id)
                                                                                                                                                        .collection("announcements")
                                                                                                                                                        .document(document.getId().toString())
                                                                                                                                                        .delete();
                                                                                                                                            }

                                                                                                                                            //Delete announcements
                                                                                                                                            db.collection("events").document(mPost_id).collection("joiners")
                                                                                                                                                    .get()
                                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                                for (DocumentSnapshot document : task.getResult()) {

                                                                                                                                                                    //Delete
                                                                                                                                                                    db.collection("events")
                                                                                                                                                                            .document(mPost_id)
                                                                                                                                                                            .collection("joiners")
                                                                                                                                                                            .document(document.getId().toString())
                                                                                                                                                                            .delete();
                                                                                                                                                                }


                                                                                                                                                                ////Add this event to host's "hosted"
                                                                                                                                                                Map<String, Object> event = new HashMap<>();
                                                                                                                                                                event.put("eid", mPost_id);//this event

                                                                                                                                                                // Add a new document with a generated ID
                                                                                                                                                                db.collection("users").document(mUser_id).collection("hosted")
                                                                                                                                                                        .document(mPost_id)
                                                                                                                                                                        .set(event)
                                                                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                                                                Log.d("eventcomplete", "DocumentSnapshot successfully deleted!");

                                                                                                                                                                                db.collection("events").document(mPost_id).delete();//delete event from "events"
                                                                                                                                                                            }
                                                                                                                                                                        })
                                                                                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                Log.w("eventcomplete", "Error deleting document", e);
                                                                                                                                                                            }
                                                                                                                                                                        });


                                                                                                                                                            } else {
                                                                                                                                                                Log.d("olo", "Error getting documents: ", task.getException());
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    });


                                                                                                                                        } else {
                                                                                                                                            Log.d("olo", "Error getting documents: ", task.getException());
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });



                                                                                                                    } else {
                                                                                                                        Log.d("olo", "Error getting documents: ", task.getException());
                                                                                                                    }
                                                                                                                }
                                                                                                            });

                                                                                                } else {
                                                                                                    Log.d("olo", "Error getting documents: ", task.getException());
                                                                                                }
                                                                                            }
                                                                                        });

                                                                            } else {
                                                                                Log.d("olo", "Error getting documents: ", task.getException());
                                                                            }

                                                                        }
                                                                    });


                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE://Decline friend request

                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                            builder.setMessage("Has the event been completed? After completion, the event will be archived in your History.").setPositiveButton("Complete", dialogClickListener)
                                                    .setNegativeButton("Cancel", dialogClickListener).show();

                                        }
                                    });



                                }else{//if not host
                                    check=false;

                                    rec.setVisibility(View.VISIBLE);
                                    mRec.setVisibility(View.VISIBLE);
                                    populateRec(singleCategString);
                                    rec.setText("Also from "+singleCategString);

                                    //check if already requested once
                                    db.collection("events")
                                            .document(mPost_id)
                                            .collection("joiners")//joiners
                                            .document(mUser_id)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            requestJoin.setVisibility(View.VISIBLE);
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                alreadyJoined = true;
                                                requestJoin.setText("Unjoin event");//Unjoin?
                                                requestJoin.setBackground(getResources().getDrawable(R.drawable.btstyle));
                                                //requestJoin.setEnabled(false);
                                            } else {
                                                alreadyJoined=false;
                                                requestJoin.setText("Join event");
                                                requestJoin.setBackground(getResources().getDrawable(R.drawable.btstyle2));
                                                //requestJoin.setEnabled(true);
                                            }
                                        }
                                    });

                                    singleHost.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            //specify hostid
                                            mFragment = new ProfileFragment();
                                            mBundle = new Bundle();
                                            mBundle.putString("host_id",eventHostId);
                                            mFragment.setArguments(mBundle);
                                            //intent to view stranger's profile
                                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.main_frame, mFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();

                                        }//END
                                    });

                                    //Show report button
                                    reportButt.setVisibility(View.VISIBLE);

                                    favboo2=false;

                                    db.collection("users")//check friend status
                                            .document(Profile.getCurrentProfile().getId())
                                            .collection("favorites")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            Log.d("lol", document.getId() + " => " + document.getData());
                                                            if(document.getId().equals(mPost_id)){//if event already favorited before...

                                                                favboo2 = true;
                                                                //setDialogRemoveFav(mPost_id);
                                                                reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                                                                break;
                                                            }

                                                        }

                                                        if(favboo2==false){
                                                            //add to favorite
                                                            //setDialogAddFav(mPost_id);
                                                            reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                                                        }

                                                    } else {
                                                        Log.d("lol", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                    reportButt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            favboo=false;

                                            Log.d("lol", "fav");
                                            reportButt.setEnabled(false);

                                            db.collection("users")//check friend status
                                                    .document(Profile.getCurrentProfile().getId())
                                                    .collection("favorites")
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (DocumentSnapshot document : task.getResult()) {
                                                                    Log.d("lol", document.getId() + " => " + document.getData());
                                                                    if(document.getId().equals(mPost_id)){//if event already favorited before...

                                                                        favboo = true;
                                                                        setDialogRemoveFav(mPost_id);
                                                                        //reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                                                                        break;
                                                                    }

                                                                }

                                                                if(favboo==false){
                                                                    //add to favorite
                                                                    setDialogAddFav(mPost_id);
                                                                    //reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                                                                }

                                                            } else {
                                                                Log.d("lol", "Error getting documents: ", task.getException());
                                                            }
                                                        }
                                                    });


                                        }
                                    });

                                }


                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }

        }else{
            //prompt
            mProgress.dismiss();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                                getActivity().onBackPressed();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Please check your internet connection.")
                        //.setPositiveButton("Remove", dialogClickListener)
                        .setNegativeButton("Retry", dialogClickListener)
                        .setCancelable(false)
                        .show();
        }

        return v;
    }

    public void setDialogAddFav(final String event_doc_id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        //Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                        //another one here
                        Map<String, Object> favtouser = new HashMap<>();
                        favtouser.put("eid", event_doc_id);//person to send request (Id of Viewing-user)

                        //holder.favbutt.setBackgroundResource(R.drawable.ic_favorite_white_24dp);

                        // Add a new pending join request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())//person to receive request
                                .collection("favorites")
                                .document(event_doc_id)
                                .set(favtouser);

                        //another one here
                        Map<String, Object> favtoevent = new HashMap<>();
                        favtoevent.put("uid", Profile.getCurrentProfile().getId());//person to send request (Id of Viewing-user)

                        //Add to event's "favoriters" collection
                        db.collection("events")
                                .document(event_doc_id)//person to receive request
                                .collection("favoriters")
                                .document(Profile.getCurrentProfile().getId())
                                .set(favtoevent);

                        reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                        Toast toast = Toast.makeText(getActivity(),"Favorited "+eventName,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        reportButt.setEnabled(true);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        //Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_LONG).show();
                        reportButt.setEnabled(true);

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add "+eventName+" to Favourites?").setPositiveButton("Add", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).setCancelable(false).show();
    }

    public void setDialogRemoveFav(final String event_doc_id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        //Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                        //unfavorite
                        Map<String, Object> friendreq = new HashMap<>();
                        friendreq.put("eid", event_doc_id);//person to send request (Id of Viewing-user)

                        // Add a new pending join request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())//person to receive request
                                .collection("favorites")
                                .document(event_doc_id)
                                .delete();


                        reportButt.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                        Toast toast = Toast.makeText(getActivity(),"Unfavorited "+eventName,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        reportButt.setEnabled(true);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        //Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_LONG).show();
                        reportButt.setEnabled(true);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove "+eventName+" from Favourites?").setPositiveButton("Remove", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).setCancelable(false).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        bar.setTitle(eventName);
        bar.setDisplayHomeAsUpEnabled(true);
    }


    public static List<String> pickRandom(List<String> lst, int n) {
        List<String> copy = new LinkedList<String>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    public void populateRec(String categ){

        recList.clear();
        chosenOnesRec.clear();

        db.collection("events")
                .whereEqualTo("categ", categ)
                .whereEqualTo("type", "Public")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("olo", document.getId() + " => " + document.getData());
                                if (!document.get("hostid").equals(Profile.getCurrentProfile().getId()) && !document.getId().equals(mPost_id)){
                                    chosenOnesRec.add(document.getId());//all events
                                }
                            }
                        }

                        if(!chosenOnesRec.isEmpty()){

                            if(chosenOnesRec.size()<4){//Size = 0,1,2, or 3
                                //for loop increment by size
                                List<String> chosenOnesA = pickRandom(chosenOnesRec, chosenOnesRec.size());

                                for(int i=0;i<chosenOnesA.size();i++){
                                    db.collection("events")
                                            .document(chosenOnesA.get(i))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        String event_id = document.getId();
                                                        String event_doc_rec = event_id;
                                                        Event events = document.toObject(Event.class).withId(event_id);
                                                        recList.add(events);//add new events whenever there is a change
                                                        recListAdapter.notifyDataSetChanged();
                                                    }


                                                }
                                            });
                                }

                                mProgress.dismiss();

                            }else{//3 items maximum for allOfA.size = 4 or greater
                                List<String> chosenOnesA = pickRandom(chosenOnesRec, 3);

                                for(int i=0;i<3;i++){
                                    db.collection("events")
                                            .document(chosenOnesA.get(i))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        String event_id = document.getId();
                                                        String event_doc_rec = event_id;
                                                        Event events = document.toObject(Event.class).withId(event_id);
                                                        recList.add(events);//add new events whenever there is a change
                                                        recListAdapter.notifyDataSetChanged();
                                                    }


                                                }
                                            });
                                }

                            }

                        }else{
                            recListAdapter.notifyDataSetChanged();
                        }


                    }

                });
        recList.clear();
        chosenOnesRec.clear();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0://Prof
                reportType = "Spam";
                break;
            case 1://Student
                reportType = "Nudity";
                break;
            case 2://Prof
                reportType = "Violence";
                break;
            case 3://Prof
                reportType = "Other";
                break;
        }

        //String selectedItemText = (String) adapterView.getItemAtPosition(i);
        //selectedItemText = (String) adapterView.getItemAtPosition(i);
        // If user change the default selection
        // First item is disable and it is used for hint
        /*if (i >= 0) {
            // Notify the selected item text
            Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
        }*/
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Don't allow it
    }

    // create an action bar button
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        db.collection("events").document(mPost_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());

                        if (!mUser_id.equals(eventHostId)) {
                            check = true;
                            inflater.inflate(R.menu.profile_menu_peek, menu);
                        }
                    }
                }
            }
        });

    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(check==true){//show these menu items only when NOT viewing as current user

            if(id == R.id.profileMenu_report){

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.dialog_custom_report_event, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.report_extra_event);

                final Spinner spinnerInput = promptsView.findViewById(R.id.report_type_event);

                //get the spinner from the xml.
                //create a list of items for the spinner.
                String[] items = new String[]{"Spam", "Nudity", "Violence", "Other"};
                //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                //There are multiple variations of this, but this is the basic variant.
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items){
                };
                //set the spinners adapter to the previously created one.
                spinnerInput.setAdapter(adapter);
                spinnerInput.setOnItemSelectedListener(new SingleEventFragment());

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Report",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //reportButt.setText(userInput.getText());
                                        //spinnerInput.getSelectedItem().toString();
                                        Log.d("report", userInput.getText()+spinnerInput.getSelectedItem().toString());

                                        // Create a new user with a first and last name
                                        Map<String, Object> report = new HashMap<>();
                                        report.put("report_type", spinnerInput.getSelectedItem().toString());
                                        report.put("report_extra", userInput.getText().toString());
                                        report.put("report_user", mUser_id);
                                        report.put("report_by", Profile.getCurrentProfile().getId());

                                        // Add a new document with a generated ID
                                        db.collection("report_users")
                                                .document()
                                                .set(report)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("acc", "DocumentSnapshot added");
                                                        Toast.makeText(getActivity().getApplicationContext(), "Thank you for your feedback",Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("acc", "Error adding document", e);
                                                    }
                                                });

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
            //bar.setDisplayHomeAsUpEnabled(false);
            //Toast.makeText(getActivity().getApplicationContext(), "You can't report your own event!", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage){
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
        }

    }

}
