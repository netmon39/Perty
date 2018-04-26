package com.example.netipol.perty.Single;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netipol.perty.Announce.Announce;
import com.example.netipol.perty.Announce.AnnounceListAdapter;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementFragment extends Fragment {

    private String mPost_id, mUser_id;
    private List<Announce> announceList;
    private AnnounceListAdapter announceListAdapter;
    private RecyclerView mAnnounceList;
    private FirebaseFirestore mFirestore;
    private TextView noAnn;
    private Button annButt;
    private ProgressDialog mProgress;

    public AnnouncementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_announcement, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPost_id = bundle.getString("event_id");
        }

        mProgress = new ProgressDialog(getActivity());

        mFirestore = FirebaseFirestore.getInstance();
        mUser_id = Profile.getCurrentProfile().getId();

        noAnn = v.findViewById(R.id.no_ann);
        noAnn.setVisibility(View.GONE);

        announceList = new ArrayList<>();
        announceListAdapter = new AnnounceListAdapter(getApplicationContext(),announceList,getFragmentManager());
        //RecyclerView setup
        mAnnounceList = v.findViewById(R.id.result_list_announce_host);
        mAnnounceList.setHasFixedSize(true);
        mAnnounceList.setLayoutManager(new LinearLayoutManager(getActivity()));//Main Activity
        mAnnounceList.setAdapter(announceListAdapter);//to fill recycler view with Events

        annButt = v.findViewById(R.id.annButt);
        annButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.dialog_custom_announce, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = promptsView
                        .findViewById(R.id.dialog_announce_msg);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Announce",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //reportButt.setText(userInput.getText());
                                        //spinnerInput.getSelectedItem().toString();
                                        //Log.d("report", userInput.getText()+spinnerInput.getSelectedItem().toString());

                                        Long tsLong = System.currentTimeMillis()/1000;

                                        // Create a new user with a first and last name
                                        Map<String, Object> ann = new HashMap<>();
                                        ann.put("message", userInput.getText().toString());
                                        ann.put("eid", mPost_id);
                                        ann.put("uid", mUser_id);
                                        ann.put("timestamp",tsLong.toString());

                                        // Add a new document with a generated ID
                                        mFirestore.collection("events")
                                                .document(mPost_id)
                                                .collection("announcements")
                                                .document()
                                                .set(ann)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("acc", "DocumentSnapshot added");
                                                        Toast.makeText(getActivity().getApplicationContext(), "All joiners were alerted",Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("acc", "Error adding document", e);
                                                    }
                                                });

                                        populateAnnounce();

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
        });

        populateAnnounce();

        return v;
    }

    public void populateAnnounce(){

        mProgress.setMessage("Loading your announcements ...");
        mProgress.show();

        announceList.clear();

        mFirestore
                .collection("events")
                .document(mPost_id)
                .collection("announcements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("ann", document.getData().toString());
                                String announce_id = document.getId();
                                Announce announces = document.toObject(Announce.class).withId(announce_id);
                                announceList.add(announces);
                                announceListAdapter.notifyDataSetChanged();
                                Collections.sort(announceList);
                            }
                            noAnn.setVisibility(View.GONE);
                            mProgress.dismiss();
                        } else {
                            Log.d("olo", "Error getting documents: ", task.getException());
                        }

                        if(announceList.isEmpty()){
                            //no joiners
                            noAnn.setVisibility(View.VISIBLE);
                        }
                    }
                });



    }

}
