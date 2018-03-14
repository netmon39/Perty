package com.example.netipol.perty.Profile;


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
import android.graphics.Color;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private SectionPageAdaptor mSectionPageadapter;
    private static final String TAG = "ProfileFragment";
    private ViewPager mViewPager;
    private Button logout;
    public TextView friendCount;

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

        logout = (Button) view.findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ((MainActivity)getActivity()).signOutFromAll();
                ((MainActivity)getActivity()).sendToLogin();
            }
        });

        //mSectionPageadapter = new SectionPageAdaptor(getFragmentManager());

        mViewPager = view.findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout= view.findViewById(R.id.profile_tab);
        tabLayout.setupWithViewPager(mViewPager);

        ImageView mypropic = (ImageView) view.findViewById(R.id.my_profile_picture);
        mypropic.setImageDrawable(profilepicture);

        String imageUrl = Profile.getCurrentProfile().getProfilePictureUri(200,200).toString();

        new ProfileFragment.DownloadImage((ImageView)view.findViewById(R.id.my_profile_picture)).execute(imageUrl);
        final TextView profName = (TextView) view.findViewById(R.id.my_username_profile);
        final TextView profDesc = (TextView) view.findViewById(R.id.my_desc_profile);
        final TextView profType = (TextView) view.findViewById(R.id.my_type_profile);

        friendCount = view.findViewById(R.id.friend_count);
        friendCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new FriendFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Get user info
        db.collection("users")
                .document(Profile.getCurrentProfile().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "DocumentSnapshot data:"+ " => " + doc.getData());
                            profName.setText(doc.get("username").toString());
                            profDesc.setText(doc.get("accountdesc").toString());
                            profType.setText(doc.get("usertype").toString());

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

    public void setupViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getChildFragmentManager());//Don't use getFragmentManager!
        adapter.addFragment(new UpcomingFragment(), "Upcoming");//0
        adapter.addFragment(new HostFragment(), "Hosting");//1
        adapter.addFragment(new NotificationFragment(), "Notice");//2
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


