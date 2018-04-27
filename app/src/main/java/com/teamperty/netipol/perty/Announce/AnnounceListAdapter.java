package com.teamperty.netipol.perty.Announce;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.teamperty.netipol.perty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by netipol on 15/4/2018 AD.
 */

public class AnnounceListAdapter extends RecyclerView.Adapter<AnnounceListAdapter.ViewHolder> {

    public List<Announce> announceList;
    public Context mContext;
    public Fragment mFragment;
    public Bundle mBundle;
    public FragmentManager fManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AnnounceListAdapter(Context context, List<Announce> announceList, FragmentManager fManager){
        this.mContext = context;
        this.announceList = announceList;
        this.fManager = fManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announce_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        //1. db->events: set event image & name from eid
        //2. db->events->announcements: set message from announceId

        db.collection("events")
                .document(announceList.get(position).getEid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            holder.eventName.setText(doc.get("title").toString());
                            Glide.with(getApplicationContext()).load(doc.get("image").toString()).apply(new RequestOptions().fitCenter()).into(holder.eventPic);
                        }
                    }
                });

        holder.eventMsg.setText(announceList.get(position).getMessage());//from Event.java
    }

    @Override
    public int getItemCount() {
        return announceList.size();
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

        public TextView eventName, eventMsg;
        public CircleImageView eventPic;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            eventName = mView.findViewById(R.id.announce_eventname);
            eventMsg = mView.findViewById(R.id.announce_message);
            eventPic = mView.findViewById(R.id.announce_eventpic);

        }
    }
}
