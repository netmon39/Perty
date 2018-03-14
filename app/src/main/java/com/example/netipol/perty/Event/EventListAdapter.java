package com.example.netipol.perty.Event;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.Home.SingleEventActivity;
import com.example.netipol.perty.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by netipol on 3/2/2018 AD.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{

    public List<Event> eventList;
    public Context context;

    public EventListAdapter(Context context, List<Event> eventList){

        this.eventList = eventList;
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

                Intent singleEventIntent = new Intent(getApplicationContext(), SingleEventActivity.class);
                singleEventIntent.putExtra("event_id", event_doc_id);
                singleEventIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(singleEventIntent);
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
