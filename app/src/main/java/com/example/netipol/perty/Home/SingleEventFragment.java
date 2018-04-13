package com.example.netipol.perty.Home;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleEventFragment extends Fragment {

    public Event model;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SinglePostAct";

    private String mPost_id, mUser_id;
    private Button requestJoin, whiteBg;
    private String eventName, eventHostId, eventHost;

    private ProgressDialog mProgress;

    private ImageView singleImage;
    private TextView singleTitle, singleDesc, singleHost, singleTime, singleLoca, singleCateg;

    private Fragment mFragment;
    private Bundle mBundle;
    public FragmentManager fManager;

    public SingleEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_event, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPost_id = bundle.getString("event_id");
        }

        Log.d("eventid", mPost_id);

        mUser_id = Profile.getCurrentProfile().getId();
        //Get user info

        singleImage = (ImageView) v.findViewById(R.id.single_image);
        //singleTitle = (TextView) v.findViewById(R.id.single_title);
        singleDesc = (TextView) v.findViewById(R.id.single_desc);
        singleHost = (TextView) v.findViewById(R.id.single_hostname);
        singleTime = (TextView) v.findViewById(R.id.single_time);
        singleLoca = (TextView) v.findViewById(R.id.single_location);
        singleCateg = (TextView) v.findViewById(R.id.single_categ);
        whiteBg = (Button) v.findViewById(R.id.single_cheat);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading...");
        mProgress.show();

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
                            MainActivity activity = (MainActivity) getActivity();
                            ActionBar bar = activity.getSupportActionBar();
                            bar.setTitle(eventName);
                            bar.setDisplayHomeAsUpEnabled(true);

                            eventHost = doc.get("host").toString();
                            eventHostId = doc.get("hostid").toString();
                            Log.w(TAG, eventHostId, task.getException());

                            //singleTitle.setText(doc.get("title").toString());
                            Glide.with(getActivity().getApplicationContext()).load(doc.get("image").toString()).into(singleImage);
                            singleDesc.setText(doc.get("desc").toString());
                            singleHost.setText(doc.get("host").toString());
                            singleTime.setText(doc.get("time").toString());
                            singleLoca.setText(doc.get("location").toString());
                            singleCateg.setText(doc.get("categ").toString());

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

                                    /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Toast.makeText(getActivity().getApplicationContext(), "Request sent!",Toast.LENGTH_SHORT).show();

                                                    //Check if request has already been sent before


                                                    //Send add request to user NOTICE
                                                    Map<String, Object> friendreq = new HashMap<>();
                                                    friendreq.put("name", mUser_id);//person to send request (current user)

                                                    // Add a new pending join request
                                                    db.collection("users")
                                                            .document(eventHostId)//person to receive request
                                                            .collection("requests")
                                                            .document(mUser_id)
                                                            .set(friendreq);

                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    Toast.makeText(getActivity().getApplicationContext(), "Nevermind",Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Send friend request to "+eventHost+"?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();*/
                                }//END
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

        requestJoin = (Button) v.findViewById(R.id.reqJoin);

        requestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> joinrequest = new HashMap<>();
                joinrequest.put("uid", mUser_id);

                Map<String, Object> record = new HashMap<>();
                record.put("eid", mPost_id);

                // Add a new pending join request
                db.collection("events")
                        .document(mPost_id)
                        .collection("requests")
                        .document(mUser_id)
                        .set(joinrequest);

                //Add to user's list of Upcoming events;
                db.collection("users")
                        .document(mUser_id)
                        .collection("joining")
                        .document(mPost_id)
                        .set(record);

                Toast.makeText(getActivity().getApplicationContext(), "You requested to join " + eventName, Toast.LENGTH_LONG).show();

                requestJoin.setEnabled(false);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle(eventName);
        bar.setDisplayHomeAsUpEnabled(true);

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
