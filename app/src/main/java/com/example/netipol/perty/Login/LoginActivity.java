package com.example.netipol.perty.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.netipol.perty.R;
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

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;//Allow Perty to gain access with FB.
    private static final String TAG = "FACELOG";
    private FirebaseAuth mAuth;//Allow read/write to Firebase
    public static String fbUID;
    private Button mFacebookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

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
        mFacebookBtn = (Button) findViewById(R.id.facebookBtn);
        mFacebookBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                mFacebookBtn.setEnabled(false);//prevent user click during loading, Progress bar here?

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

                            Profile profile = Profile.getCurrentProfile();
                            fbUID = profile.getId();
                            sendToRegister(profile);
                            mFacebookBtn.setEnabled(true);

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

        Toast.makeText(LoginActivity.this, "You're logged in", Toast.LENGTH_LONG).show();
        Intent accountIntent = new Intent(LoginActivity.this, AccountActivity.class);
        accountIntent.putExtra("name", profile.getFirstName());
        accountIntent.putExtra("surname", profile.getLastName());
        accountIntent.putExtra("imageUri", profile.getProfilePictureUri(200,200)).toString();
        accountIntent.putExtra("fbUID", fbUID);
        startActivity(accountIntent);
        finish();
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
