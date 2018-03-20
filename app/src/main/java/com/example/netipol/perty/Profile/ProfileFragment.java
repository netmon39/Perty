package com.example.netipol.perty.Profile;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.netipol.perty.Profile.currentUser.*;

import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private String mHost_id, imageUrl;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private SectionPageAdaptor mSectionPageadapter;
    private static final String TAG = "ProfileFragment";
    private ViewPager mViewPager;
    private Button logout, profButton;
    public TextView friendCount;
    public String userName;
    public boolean check;

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

        //Send mHostid to viewpager fragment
        //HostFragment detail = (HostFragment) getActivity().getSupportFragmentManager().findFragmentByTag("TabHostFragment");  //get detail fragment instance by it's tag
        MainActivity activity = (MainActivity) getActivity();

        profButton = view.findViewById(R.id.my_profile_button);

        logout = (Button) view.findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ((MainActivity)getActivity()).signOutFromAll();
                ((MainActivity)getActivity()).sendToLogin();
            }
        });

        friendCount = view.findViewById(R.id.friend_count);

        //The switch
        Bundle bundle = this.getArguments();
        if (bundle != null) {//Viewing another user
            Log.d("hello", "not null");
            mHost_id = bundle.getString("host_id");
            activity.setHostId(mHost_id);
            check=true;

            logout.setVisibility(View.GONE);

            profButton.setText("Add Friend");
            profButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Toast.makeText(getActivity().getApplicationContext(), "Request sent!",Toast.LENGTH_SHORT).show();

                                                    //Check if request has already been sent before


                                                    //Send add request to user NOTICE
                                                    Map<String, Object> friendreq = new HashMap<>();
                                                    friendreq.put("name",Profile.getCurrentProfile().getId());//person to send request (current user)

                                                    // Add a new pending join request
                                                    db.collection("users")
                                                            .document(mHost_id)//person to receive request
                                                            .collection("requests")
                                                            .document(mHost_id)
                                                            .set(friendreq);

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
            });


            mViewPager = view.findViewById(R.id.container);
            setupViewProfViewPager(mViewPager);
        } else {//Viewing as yourself
            Log.d("hello", "null");
            mHost_id = Profile.getCurrentProfile().getId();
            activity.setHostId(mHost_id);
            check=false;

            profButton.setText("Edit Profile");

            friendCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame, new FriendFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            mViewPager = view.findViewById(R.id.container);
            setupProfViewPager(mViewPager);
        }

        imageUrl = "https://graph.facebook.com/"+mHost_id+"/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D";


        //mSectionPageadapter = new SectionPageAdaptor(getFragmentManager());

        mViewPager = view.findViewById(R.id.container);


        TabLayout tabLayout= view.findViewById(R.id.profile_tab);
        tabLayout.setupWithViewPager(mViewPager);

        ImageView mypropic = (ImageView) view.findViewById(R.id.my_profile_picture);
        mypropic.setImageDrawable(profilepicture);

        //String imageUrl = Profile.getCurrentProfile().getProfilePictureUri(200,200).toString();

        Log.d(TAG, imageUrl);

        new ProfileFragment.DownloadImage((ImageView)view.findViewById(R.id.my_profile_picture)).execute(imageUrl);
        //final TextView profName = (TextView) view.findViewById(R.id.my_username_profile);
        final TextView profDesc = (TextView) view.findViewById(R.id.my_desc_profile);
        final TextView profType = (TextView) view.findViewById(R.id.my_type_profile);

        //Get user info
        db.collection("users")
                .document(mHost_id)
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

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileActionBar();
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

    public void setupProfViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getChildFragmentManager());//Don't use getFragmentManager!
        adapter.addFragment(new UpcomingFragment(), "Upcoming");//0
        adapter.addFragment(new HostFragment(), "Hosting");//1
        adapter.addFragment(new NotificationFragment(), "Notice");//2
        //adapter.addFragment(new HistoryFragment(), "History");//3
        viewPager.setAdapter(adapter);
    }

    public void setupViewProfViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getChildFragmentManager());//Don't use getFragmentManager!
        //adapter.addFragment(new UpcomingFragment(), "Upcoming");//0
        adapter.addFragment(new HostFragment(), "Hosting");//1
        //adapter.addFragment(new NotificationFragment(), "Notice");//2
        //adapter.addFragment(new HistoryFragment(), "History");//3
        viewPager.setAdapter(adapter);
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


