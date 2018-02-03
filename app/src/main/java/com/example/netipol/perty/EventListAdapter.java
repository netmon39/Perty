package com.example.netipol.perty;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by netipol on 3/2/2018 AD.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{

    public List<Event> eventList;

    public EventListAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(eventList.get(position).getTitle());
        holder.desc.setText(eventList.get(position).getDesc());
        holder.type.setText(eventList.get(position).getType());
        //holder.image.setText(eventList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ImageView image;

        public TextView title, desc, type;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            title = (TextView) mView.findViewById(R.id.post_title);
            desc = (TextView) mView.findViewById(R.id.post_desc);
            type = (TextView) mView.findViewById(R.id.post_type);
            image = (ImageView) mView.findViewById(R.id.post_image);
        }
    }
}
