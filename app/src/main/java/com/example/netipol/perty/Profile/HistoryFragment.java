package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.netipol.perty.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 09/02/2018.
 */

public class HistoryFragment extends Fragment {
    private static final String TAG = "TabHistFragment";

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_tab,container,false);


        Log.d("GET ID at HisFrag", "HisFrag");

        return view;
    }
}
