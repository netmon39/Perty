package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.netipol.perty.R;

/**
 * Created by USER on 08/02/2018.
 */

public class HostFragment  extends android.support.v4.app.Fragment{
    private static final String TAG = "TabHostFragment";
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_tab,container,false);
        return view;
    }
}
