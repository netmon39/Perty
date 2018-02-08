package com.example.netipol.perty.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.netipol.perty.Login.MainActivity;
import com.example.netipol.perty.Login.SelectPrefActivity;
import com.example.netipol.perty.R;
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

        db.collection("users")
                .document(MainActivity.fbUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                                Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());
                                StringBuilder fields = new StringBuilder("");
                                fields.append("User: ").append(doc.get("username"));
                                fields.append("\nType: ").append(doc.get("accountdesc"));
                                fields.append("\nDesc: ").append(doc.get("usertype"));
                                profView.setText(fields.toString());

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
//
//
//
    }

    private void setupToolbar(){
        Toolbar tb = findViewById(R.id.profileToolBar);
        setSupportActionBar(tb);

        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick (MenuItem item){
                Log.d(TAG,"omMenuItemClick: clicked" +item);

                switch (item.getItemId()){
                    case R.id.profileMenu:
                        Log.d(TAG,"onMenuItemClick: Navigating to Profile Menu");
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    private void init(){
        Log.d(TAG,"init:inflating"+getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        android.support.v4.app.FragmentTransaction transaction = UserProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }

}
