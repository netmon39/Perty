package com.example.netipol.perty.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netipol.perty.R;
import com.example.netipol.perty.SelectPref.SelectPrefActivity;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.netipol.perty.Login.LoginActivity.fbUID;

public class AccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "FIRESTORELOG";
    public Button nextToP;
    public EditText userNameF, accountD;
    public String userName, accountDescription;
    public String userType = "x";
    public Uri FBimageUri;
    public String selectedItemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FacebookSdk.sdkInitialize(this);
        mAuth = FirebaseAuth.getInstance();

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        fbUID = inBundle.get("fbUID").toString();
        FBimageUri = Uri.parse(inBundle.get("imageUri").toString());

        db = FirebaseFirestore.getInstance();

        TextView nameView = (TextView) findViewById(R.id.fbName);
        nameView.setText("" + name + " " + surname);

        new AccountActivity.DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(inBundle.get("imageUri").toString());

        //get the spinner from the xml.
        Spinner userTypeDropdown = findViewById(R.id.userType);
        //create a list of items for the spinner.
        String[] items = new String[]{"What type of user are you?","Student", "Professor", "Club", "Official"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        //set the spinners adapter to the previously created one.
        userTypeDropdown.setAdapter(adapter);
        userTypeDropdown.setOnItemSelectedListener(this);

        userNameF = (EditText) findViewById(R.id.userNameField);
        accountD = (EditText) findViewById(R.id.userBio);
        nextToP = (Button) findViewById(R.id.nextToPref);


        /*/ Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReferenceFromUrl("https://perty-53386.firebaseio.com/Users/User_"+fbUID);*/

        nextToP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//write fields to firebase

                startNext();
            }
        });
    }

    private void startNext() {

        if(!TextUtils.isEmpty(userNameF.getText().toString().trim()) && !TextUtils.isEmpty(accountD.getText().toString().trim())) {//Add parameter check!!!

                userName = userNameF.getText().toString();
                accountDescription = accountD.getText().toString();

            if(userName.length()==0 || accountDescription.length()==0 || userType.equals("x")){
                Toast.makeText(getApplicationContext(), "Please fill every field :D", Toast.LENGTH_SHORT).show();
                return;
            }

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
                                Log.d("acc", "DocumentSnapshot added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("acc", "Error adding document", e);
                            }
                        });


                /*userRef.child("UserName").setValue(userName);
                userRef.child("AccountDesc").setValue(accountDescription);
                userRef.child("UserType").setValue(userType);*/

            Intent intent = new Intent(AccountActivity.this, SelectPrefActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(this, "Please fill all parameters",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 1://Student
                userType = "Student";
                break;
            case 2://Prof
                userType = "Professor";
                break;
            case 3://Club
                userType = "Club";
                break;
            case 4://Official
                userType = "Official";
                break;
        }

        //String selectedItemText = (String) adapterView.getItemAtPosition(i);
        selectedItemText = (String) adapterView.getItemAtPosition(i);
        // If user change the default selection
        // First item is disable and it is used for hint
        if (i > 0) {
            // Notify the selected item text
            Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
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
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
        }

    }
}
