package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.netipol.perty.R;

/**
 * Created by USER on 08/02/2018.
 */

public class UpcomingFragment extends Fragment {
    private static final String TAG = "TabUPFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_tab, container, false);
        return view;
    }
}
