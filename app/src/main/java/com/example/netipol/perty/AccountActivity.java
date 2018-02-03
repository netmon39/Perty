package com.example.netipol.perty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "FIRESTORELOG";
    public Button nextToP;
    public EditText userNameF, accountD;
    public String userType, userName, accountDescription;

    /*
      1. "Welcome Screen/Login Page": Facebook Login button, Perty logo, etc. - MainActivity
      2. "Basic Account Info Fill-up Page": Show basic FB info, fill in desired Username, UserType, Account Desc., etc. - AccountActivity
      3. "User Preference/Event Category" Selection Page: Multi-select Grid View already made, logos or stock photos of each category - SelectPrefActivity
      4. "User Profile/My Profile": User profile info and stats(followers, rating, ...), [My Created events, My Joined events] - UserProfile
      5. "Create Perty Event": Includes fill-up form of event details(name, type, etc.) beforing posting event - CreateEvent
      6. "Event Feed": Query of events based on Preferred-Categ. and Followed-Users, [Includes Main Search Bar] - EventFeed
      7. "View Event": Clicking on an event will prompt this. Displays the event's important details & photo if included. Join Event Button. - EventView
      *8. Bottom Control Tab: News Feed, Explore, My Profile, Settings?

        Prototype 1.0 Test Flow (2 Users Signs-in, One Creates an Event as another Joins it)
        1. START > User A joins Perty via Facebook > Fill-up account details > Choose Pref. > Arrive at his/her Profile (can edit) > Creates a PRIVATE Perty event > 2.
        2. User B joins Perty via Facebook > Fill-up account details > Choose Pref. > Arrive at his/her Profile (can edit) > Goes to News Feed to search for Perty event by exact name for now >Arrive at Event View and Joins event> 3.
        3. User A gets prompted of User B's join request > User A accepts and sees joiner in My Event tab of User Profile  > END
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FacebookSdk.sdkInitialize(this);
        mAuth = FirebaseAuth.getInstance();

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        final String fbUID = inBundle.get("fbUID").toString();
        String imageUrl = inBundle.get("imageUrl").toString();

        TextView nameView = (TextView) findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        new AccountActivity.DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(imageUrl);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent login = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(login);
                finish();
            }
        });

        //get the spinner from the xml.
        Spinner userTypeDropdown = findViewById(R.id.userType);
        //create a list of items for the spinner.
        String[] items = new String[]{"Student", "Professor", "Club", "Official"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        userTypeDropdown.setAdapter(adapter);
        userTypeDropdown.setOnItemSelectedListener(this);

        userNameF = (EditText) findViewById(R.id.userNameField);
        accountD = (EditText) findViewById(R.id.accountDesc);
        nextToP = (Button) findViewById(R.id.nextToPref);



        /*/ Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReferenceFromUrl("https://perty-53386.firebaseio.com/Users/User_"+fbUID);*/

        nextToP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//write fields to firebase

                userName = userNameF.getText().toString();
                accountDescription = accountD.getText().toString();

                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("username", userName);
                user.put("accountdesc", accountDescription);
                user.put("usertype", userType);

                // Add a new document with a generated ID
                db.collection("users")
                        .document(fbUID)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });


                /*userRef.child("UserName").setValue(userName);
                userRef.child("AccountDesc").setValue(accountDescription);
                userRef.child("UserType").setValue(userType);*/

                Intent intent = new Intent(AccountActivity.this, SelectPrefActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0://Student
                userType = "Student";
                break;
            case 1://Prof
                userType = "Professor";
                break;
            case 2://Club
                userType = "Club";
                break;
            case 3://Official
                userType = "Official";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Don't allow it
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
            bmImage.setImageBitmap(result);
        }

    }
}
