package com.example.netipol.perty.Home;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.netipol.perty.Login.MainActivity;
import com.example.netipol.perty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingleEventActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SinglePostAct";

    private String mPost_key;
    private Button requestJoin;
    private String eventName, eventHost, eventHostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        mPost_key = getIntent().getExtras().getString("event_id");
        //Get user info
        db.collection("events")
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

                            /*profName.setText(doc.get("username").toString());
                            profDesc.setText(doc.get("accountdesc").toString());
                            profType.setText(doc.get("usertype").toString());*/

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



        requestJoin = (Button) findViewById(R.id.reqJoin);

        requestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*db.collection("users")
                        .document(eventHostID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                                    eventHost = doc.get("username").toString();

                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });*/
                Toast.makeText(SingleEventActivity.this, "You joined " + eventName, Toast.LENGTH_LONG).show();

            }
        });

    }
}
