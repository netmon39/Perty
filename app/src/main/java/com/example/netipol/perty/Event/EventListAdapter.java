package com.example.netipol.perty.Event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.Home.SingleEventActivity;
import com.example.netipol.perty.Home.SingleEventFragment;
import com.example.netipol.perty.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by netipol on 3/2/2018 AD.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{

    public List<Event> eventList;
    public Context mContext;
    public Fragment mFragment;
    public Bundle mBundle;
    public FragmentManager fManager;

    public EventListAdapter(Context context, List<Event> eventList, FragmentManager fManager){
        this.mContext = context;
        this.eventList = eventList;
        this.fManager = fManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(eventList.get(position).getTitle());//from Event.java
        holder.host.setText(eventList.get(position).getHost());
        holder.time.setText(eventList.get(position).getTime());
        holder.location.setText(eventList.get(position).getLocation());
        //holder.image.setText(eventList.get(position).getTitle());
        Glide.with(getApplicationContext()).load(eventList.get(position).getImage()).apply(new RequestOptions().fitCenter()).into(holder.image);

        final String event_doc_id = eventList.get(position).eventId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("GETID", event_doc_id + position);

                mFragment = new SingleEventFragment();
                //Create a bundle to pass data, add data, set the bundle to your fragment and:
                mFragment = new SingleEventFragment();
                mBundle = new Bundle();
                mBundle.putString("event_id",event_doc_id);
                mFragment.setArguments(mBundle);
                fManager.beginTransaction().replace(R.id.main_frame, mFragment).addToBackStack(null).commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public long getItemId(int postion){
        return postion;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ImageView image;

        public TextView title, host, time, location;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            title = (TextView) mView.findViewById(R.id.post_title);
            host = (TextView) mView.findViewById(R.id.post_hostname);
            time = (TextView) mView.findViewById(R.id.post_time);
            location = (TextView) mView.findViewById(R.id.post_location);
            image = (ImageView) mView.findViewById(R.id.post_image);
        }
    }
}
