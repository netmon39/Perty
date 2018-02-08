package com.example.netipol.perty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.netipol.perty.Login.MainActivity;
import com.example.netipol.perty.Login.SelectPrefActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FIRESTORELOG";
    public TextView profView;
    public String x, y, z;
    public Button nextToSelectPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profView = (TextView) findViewById(R.id.profileInfo);

        nextToSelectPref = (Button) findViewById(R.id.nextToSelectPref);

        //get data from a user doc in database
        db.collection("users")
                .document(MainActivity.fbUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                                Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());
                                String usern = (String) doc.get("username"); //get username, usertype. etc. (corresponding to fields avail. in doc)
                                profView.setText(usern); //where ever you want to display it
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        nextToSelectPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, SelectPrefActivity.class);
                startActivity(intent);
            }
        });

 /*StringBuilder fields = new StringBuilder("");
                                fields.append("User: ").append(doc.get("username"));
                                fields.append("\nType: ").append(doc.get("accountdesc"));
                                fields.append("\nDesc: ").append(doc.get("usertype"));
                                profView.setText(fields.toString());*/


    }
}
