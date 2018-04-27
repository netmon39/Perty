package com.teamperty.netipol.perty.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.teamperty.netipol.perty.Home.MainActivity;
import com.teamperty.netipol.perty.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;//Allow Perty to gain access with FB.
    private static final String TAG = "FACELOG";
    private FirebaseAuth mAuth;//Allow read/write to Firebase
    public static String fbUID;
    private Button mFacebookBtn;
    private ProgressDialog mProgress;
    private FirebaseFirestore mFirestore;
    private boolean alreadyExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mFirestore = FirebaseFirestore.getInstance();
        alreadyExist = false;


        //mAuth.getCurrentUser().getEmail();\\\\

        /*PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.netipol.perty.Login", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }*/

        //Facebook Login
        mCallbackManager = CallbackManager.Factory.create();

        //Custom FB login button
        mFacebookBtn = (Button) findViewById(R.id.fb_icon);
        mFacebookBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                mFacebookBtn.setEnabled(false);//prevent user click during loading, Progress bar here?
                mFacebookBtn.setVisibility(View.INVISIBLE);

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());//Get FB access token and pass it to Firebase mAuth
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });

            }
        });


    }

    //Facebook Login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //Facebook Login

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)//Sign into Firebase with Facebook creds
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            /*
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            Toast.makeText(MainActivity.this, currentUser.getDisplayName().toString(),Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, currentUser.getEmail().toString(),Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, currentUser.getUid().toString(),Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, currentUser.getPhotoUrl().toString(),Toast.LENGTH_SHORT).show();
                            */

                            //User has successfully logged in, save this information
                            // We need an Editor object to make preference changes.

                            CollectionReference eventsRef = mFirestore.collection("users");
                            eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {

                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d("ddog", "Error : " + e.getMessage());
                                    }

                                    Profile profile = Profile.getCurrentProfile();

                                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {//getDocuments) {

                                        //for (int i = 0; i < titleList.size(); i++) {

                                        if (doc.getId().equals(profile.getId())) {//compare userlist with cached FB profile
                                            alreadyExist = true;
                                            Log.d("lgn_handleFB", "has logged in b4");
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                                            //intent.putExtra("categ", categ_key);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        }
                                        //}
                                    }

                                    if(alreadyExist==false){//Go create Perty account first
                                        Log.d("lgn_handleFB", "has not logged in b4, send to acc");
                                        fbUID = profile.getId();
                                        sendToRegister(profile);
                                        mFacebookBtn.setEnabled(true);
                                    }

                                }

                            });

                            /*SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                            //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
                            boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

                            if(hasLoggedIn)
                            {
                                Log.d("lgntest", "has logged in");
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();

                            }else {
                                Log.d("lgntest", "has not logged in before");
                                Profile profile = Profile.getCurrentProfile();
                                fbUID = profile.getId();
                                sendToRegister(profile);
                                mFacebookBtn.setEnabled(true);
                            }*/

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            mFacebookBtn.setEnabled(true);
                        }

                        // ...
                    }
                });
    }

    /*
    @Override
    public void onStart() {//is user logged in?
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Profile profile = Profile.getCurrentProfile();//FB

        if(currentUser == null) {//logged in
            sendToRegister(profile);
        }
    }*/

    private void sendToRegister(Profile profile){

        //Toast.makeText(LoginActivity.this, "You're logged in", Toast.LENGTH_LONG).show();
        Intent accountIntent = new Intent(LoginActivity.this, AccountActivity.class);
        accountIntent.putExtra("name", profile.getFirstName());
        accountIntent.putExtra("surname", profile.getLastName());
        accountIntent.putExtra("imageUri", profile.getProfilePictureUri(200,200)).toString();
        accountIntent.putExtra("fbUID", fbUID);
        //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
        //intent.putExtra("categ", categ_key);
        accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity mainActivity = new MainActivity();
        mainActivity.signOutFromAll();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.hide();
        //bar.setTitle("");
        //bar.setDisplayHomeAsUpEnabled(true);

    }


    /*
    @Override// *************** for demo purposes, don't forget to remove this **************
    public void onStop() {
        super.onStop();

        signOutFromAll();
    }

    public void signOutFromAll(){
        mAuth.getInstance().signOut();//of Firebase
        LoginManager.getInstance().logOut();//of Facebook
    }
    */
}
