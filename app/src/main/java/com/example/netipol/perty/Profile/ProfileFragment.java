package com.example.netipol.perty.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static com.example.netipol.perty.Profile.currentUser.*;

import com.example.netipol.perty.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private SectionPageAdaptor mSectionPageadapter;
    private static final String TAG = "ProfileFragment";
    private ViewPager mViewPager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    //public static final String TAG = "Profilefragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create: Starting");
        super.onCreateView(inflater, container, savedInstanceState);
        mSectionPageadapter = new SectionPageAdaptor(getFragmentManager());
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TabLayout tabLayout= (TabLayout) view.findViewById(R.id.profile_tab);
        tabLayout.setupWithViewPager(mViewPager);


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView mypropic = (ImageView) view.findViewById(R.id.my_profile_picture);
        mypropic.setImageDrawable(profilepicture);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdaptor adapter = new SectionPageAdaptor(getFragmentManager());
        adapter.addFragment(new UpcomingFragment(), "TabUPFragment");
        adapter.addFragment(new NotificationFragment(), "TabNotiFragment");
        adapter.addFragment(new HostFragment(), "TabHostFragment");
        adapter.addFragment(new HistoryFragment(), "TabHistFragment");
        ViewPager.setAdapter(adapter);
    }
}
