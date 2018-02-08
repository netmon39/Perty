package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.R;

/**
 * Created by USER on 09/02/2018.
 */

public class HistoryFragment extends Fragment {
    private static final String TAG = "TabHistFragment";
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_tab,container,false);
        return view;
    }
}
