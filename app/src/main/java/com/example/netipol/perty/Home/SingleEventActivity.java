package com.example.netipol.perty.Home;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.netipol.perty.Login.LoginActivity;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Ref;
import java.util.HashMap;
import java.util.Map;

public class SingleEventActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SinglePostAct";

    private String mPost_id, mUser_id;
    private Button requestJoin;
    private String eventName, eventHost, eventHostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        mPost_id = getIntent().getExtras().getString("event_id");
        mUser_id = Profile.getCurrentProfile().getId();
        //Get user info

        /*db.collection("events")//get stuff to display in detail
                .document(mPost_key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                            eventName = doc.get("title").toString();
                            eventHostID = doc.get("host").toString();
                            Log.w(TAG, eventHostID, task.getException());

                            profName.setText(doc.get("username").toString());
                            profDesc.setText(doc.get("accountdesc").toString());
                            profType.setText(doc.get("usertype").toString());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });*/
        //check if user is host
        db.collection("events").document(mPost_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());

                        String eventHost = document.get("host").toString();

                        if(mUser_id.equals(eventHost)){

                            requestJoin.setText("You are the host!");
                            requestJoin.setEnabled(false);

                        }else{//if not host

                            //check if already requested once
                            db.collection("events")
                                    .document(mPost_id)
                                    .collection("requests")//join requests
                                    .document(mUser_id)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        requestJoin.setText("Join requested");
                                        requestJoin.setEnabled(false);
                                    } else {
                                        requestJoin.setEnabled(true);
                                    }
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

        requestJoin = (Button) findViewById(R.id.reqJoin);

        requestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> request = new HashMap<>();
                request.put("uid", mUser_id);

                Map<String, Object> record = new HashMap<>();
                record.put("eid", mPost_id);

                // Add a new pending join request
                db.collection("events")
                        .document(mPost_id)
                        .collection("requests")
                        .document(mUser_id)
                        .set(request);

                //Add to user's list of Upcoming events;
                db.collection("users")
                        .document(mUser_id)
                        .collection("joining")
                        .document(mPost_id)
                        .set(record);

                Toast.makeText(SingleEventActivity.this, "You requested to join " + eventName, Toast.LENGTH_LONG).show();

                requestJoin.setEnabled(false);
            }
        });

    }


}
