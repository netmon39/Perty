package com.example.netipol.perty.Profile;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.netipol.perty.Profile.currentUser.*;
import static com.facebook.FacebookSdk.getApplicationContext;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.EditProfileActivity;
import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Friend.FriendReq;
import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.R;
import com.example.netipol.perty.TutorialActivityAsGuide;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private String mHost_id, imageUrl;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private SectionPageAdaptor mSectionPageadapter;
    private static final String TAG = "ProfileFragment";
    private ViewPager mViewPager;
    private Button profButton;
    public TextView friendCount, profDesc, profType, hostedCount, joinedCount;
    public CircleImageView profPic;
    public String userName;
    public boolean check, alreadySent;
    public boolean pressToAccept = false;
    private int f,h,j;
    private RelativeLayout joinedCounter, hostedCounter, friendCounter;
    //public  NotificationFragment fragmentNoti;


    private List<Event> joinedList, hostedList;
    private List<FriendReq> friendreqList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    //public static final String TAG = "Profilefragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create: Starting");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setHasOptionsMenu(true);

        friendreqList = new ArrayList<>();
        hostedList = new ArrayList<>();
        joinedList = new ArrayList<>();

        //fragmentNoti = new NotificationFragment();

        //Send mHostid to viewpager fragment
        //HostFragment detail = (HostFragment) getActivity().getSupportFragmentManager().findFragmentByTag("TabHostFragment");  //get detail fragment instance by it's tag
        MainActivity activity = (MainActivity) getActivity();

        profButton = view.findViewById(R.id.my_profile_button);
        profPic = view.findViewById(R.id.my_profile_picture);
        hostedCount =view.findViewById(R.id.host_count);
        joinedCount =view.findViewById(R.id.join_count);
        friendCount = view.findViewById(R.id.friend_count);
        joinedCounter =view.findViewById(R.id.joined_counter);
        hostedCounter =view.findViewById(R.id.hosted_counter);
        friendCounter=view.findViewById(R.id.friends_counter);

        Bundle bundle = this.getArguments();
            if (bundle != null) {//Viewing another user
                mHost_id = bundle.getString("host_id");//id of Viewed-user
                Log.d("hello", "not null");
                activity.setHostId(mHost_id);
                check=true;

                profButton.setText("Add Friend");

                //check if you have sent a request to this person already?
                db.collection("users")
                        .document(mHost_id)
                        .collection("requests")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if(document.getId().equals(Profile.getCurrentProfile().getId())){//if friend request already sent...
                                            alreadySent=true;
                                            profButton.setText("Request Pending");
                                            profButton.setEnabled(false);
                                            Log.d("friendReq", "Found");
                                            break;
                                        }
                                    }


                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                //check if currently friends
                db.collection("users")
                        .document(mHost_id)
                        .collection("friends")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if(document.getId().equals(Profile.getCurrentProfile().getId())){//if friend request already sent...
                                            alreadySent=true;
                                            profButton.setText("Friends");
                                            profButton.setEnabled(false);
                                            Log.d("friendReq", "already friends");

                                            break;
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                //check if this person has already sent a friend request to you!
                db.collection("users")//check if currently friends
                        .document(Profile.getCurrentProfile().getId())
                        .collection("requests")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if(document.getId().equals(mHost_id)){//if friend request already sent...
                                            pressToAccept=true;
                                            profButton.setText("Accept Request");
                                            //profButton.setEnabled(false);
                                            Log.d("friendReq", "accept this person!");
                                            break;
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });



                Log.d("friendReq", "NotFound");
                profButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //true: Accept Friend request
                        if(pressToAccept==true){

                            Toast.makeText(getActivity().getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                            //Send add request to user NOTICE
                            Map<String, Object> friendreqAcc = new HashMap<>();
                            friendreqAcc.put("uid", mHost_id);//person to add

                            // Add a new friend for acceptor
                            db.collection("users")
                                    .document(Profile.getCurrentProfile().getId())//current user
                                    .collection("friends")
                                    .document(mHost_id)
                                    .set(friendreqAcc);

                            //Send add request to user NOTICE
                            Map<String, Object> friendreqReq = new HashMap<>();
                            friendreqReq.put("uid", Profile.getCurrentProfile().getId());//person to add

                            // Add a new friend for requestor
                            db.collection("users")
                                    .document(mHost_id)//current user
                                    .collection("friends")
                                    .document(Profile.getCurrentProfile().getId())
                                    .set(friendreqReq);

                            //Remove friend request
                            db.collection("users")
                                    .document(Profile.getCurrentProfile().getId())
                                    .collection("requests")
                                    .document(mHost_id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("friendreq", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("friendreq", "Error deleting document", e);
                                        }
                                    });

                            profButton.setText("Friends");
                            profButton.setEnabled(false);

                            //fragmentNoti.loadRecyclerViewData();

                        }else{
                            //false: Send Friend request
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getActivity().getApplicationContext(), "Request sent!",Toast.LENGTH_SHORT).show();

                                            //Check if request has already been sent before


                                            //Send add request to user NOTICE
                                            Map<String, Object> friendreq = new HashMap<>();
                                            friendreq.put("uid",Profile.getCurrentProfile().getId());//person to send request (Id of Viewing-user)

                                            // Add a new pending join request
                                            db.collection("users")
                                                    .document(mHost_id)//person to receive request
                                                    .collection("requests")
                                                    .document(Profile.getCurrentProfile().getId())
                                                    .set(friendreq);

                                            profButton.setText("Request Sent");
                                            profButton.setEnabled(false);

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            Toast.makeText(getActivity().getApplicationContext(), "Nevermind",Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Send friend request to "+userName+"?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }


                    }
                });


                mViewPager = view.findViewById(R.id.container);
                setupViewProfViewPager(mViewPager);

            } else {//Viewing as yourself

                Log.d("hello", "null");
                mHost_id = Profile.getCurrentProfile().getId();
                activity.setHostId(mHost_id);
                check=false;

                //setCounter();

                profButton.setText("Edit Profile");

                profButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));

                    }
                });

                friendCounter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendFragment mFragment = new FriendFragment();
                        Bundle mBundle = new Bundle();
                        mBundle.putString("uid",mHost_id);
                        mFragment.setArguments(mBundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame, mFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

                hostedCounter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame, new HostedFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

                joinedCounter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame, new JoinedFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

                mViewPager = view.findViewById(R.id.container);
                setupProfViewPager(mViewPager);
            }


        //imageUrl = "https://graph.facebook.com/"+mHost_id+"/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D";

        //mSectionPageadapter = new SectionPageAdaptor(getFragmentManager());

        mViewPager = view.findViewById(R.id.container);


        TabLayout tabLayout= view.findViewById(R.id.profile_tab);
        tabLayout.setupWithViewPager(mViewPager);

        ImageView mypropic = (ImageView) view.findViewById(R.id.my_profile_picture);
        mypropic.setImageDrawable(profilepicture);

        //String imageUrl = Profile.getCurrentProfile().getProfilePictureUri(200,200).toString();

        //Log.d(TAG, imageUrl);

        //final TextView profName = (TextView) view.findViewById(R.id.my_username_profile);
        profDesc = (TextView) view.findViewById(R.id.my_desc_profile);
        profType = (TextView) view.findViewById(R.id.my_type_profile);

        getUserInfo(mHost_id);

        return view;
    }

    public void getUserInfo(String id){
        //Get user info
        db.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());

                            userName = doc.get("username").toString();
                            setProfileActionBar();

                            //profName.setText(userName);
                            profDesc.setText(doc.get("accountdesc").toString());
                            profType.setText(doc.get("usertype").toString());

                            Glide.with(getApplicationContext()).load(doc.get("profimage").toString()).apply(new RequestOptions().fitCenter()).into(profPic);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        //setProfileActionBar();
        getUserInfo(mHost_id);
        setCounter(mHost_id);
    }

    public String getHostId(){
        return mHost_id;
    }

    public void setProfileActionBar(){
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle(userName);
        if(check==true){
            bar.setDisplayHomeAsUpEnabled(true);
        }else {
            bar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void setCounter(String x){

        friendreqList.clear();
        hostedList.clear();
        joinedList.clear();
        f=0;
        h=0;
        j=0;

        db.collection("users").document(x).collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FriendList", document.getId() + " => " + document.getData());
                                String friendreq_id = document.getId();
                                FriendReq events = document.toObject(FriendReq.class).withId(friendreq_id);
                                friendreqList.add(events);
                                f=f+1;
                            }
                            friendCount.setText(String.valueOf(f));

                        } else {
                        }
                    }
                });

        db.collection("users").document(x).collection("hosted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FriendList", document.getId() + " => " + document.getData());
                                String friendreq_id = document.getId();
                                Event events = document.toObject(Event.class).withId(friendreq_id);
                                hostedList.add(events);
                                h=h+1;
                            }
                            hostedCount.setText(String.valueOf(h));

                        } else {
                        }
                    }
                });

        db.collection("users").document(x).collection("joined")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FriendList", document.getId() + " => " + document.getData());
                                String friendreq_id = document.getId();
                                Event events = document.toObject(Event.class).withId(friendreq_id);
                                joinedList.add(events);
                                j=j+1;
                            }
                            joinedCount.setText(String.valueOf(j));
                        } else {
                        }
                    }
                });


    }

    // create an action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(check==false){
            inflater.inflate(R.menu.profile_menu, menu);
        }else {
            //bar.setDisplayHomeAsUpEnabled(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profileMenu_logout) {
            // do something here
            ((MainActivity)getActivity()).signOutFromAll();
            ((MainActivity)getActivity()).sendToLogin();
        }

        if(id == R.id.profileMenu_guide){
            startActivity(new Intent(getApplicationContext(), TutorialActivityAsGuide.class));

        }
        return super.onOptionsItemSelected(item);
    }

    public void setupProfViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getChildFragmentManager());//Don't use getFragmentManager!
        adapter.addFragment(new UpcomingFragment(), "Upcoming");//0
        adapter.addFragment(new HostFragment(), "Hosting");//1
        //adapter.addFragment(new NotificationFragment(), "Notice");//2
        adapter.addFragment(new FavoritesFragment(), "Favorites");//3
        viewPager.setAdapter(adapter);
    }

    public void setupViewProfViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getChildFragmentManager());//Don't use getFragmentManager!
        //adapter.addFragment(new UpcomingFragment(), "Upcoming");//0
        adapter.addFragment(new HostFragment(), "Hosting");//1
        //adapter.addFragment(new NotificationFragment(), "Notice");//2
        //adapter.addFragment(new FavoritesFragment(), "History");//3
        viewPager.setAdapter(adapter);
    }

}


