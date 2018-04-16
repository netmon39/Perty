package com.example.netipol.perty.Event;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.Friend.FriendReqListAdapter;
import com.example.netipol.perty.Home.SingleEventFragment;
import com.example.netipol.perty.Profile.FavoritesFragment;
import com.example.netipol.perty.Profile.ProfileFragment;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public boolean favboo = false;
    public boolean favboo2 = false;
    private View view;
    public int key=0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventListAdapter(Context context, List<Event> eventList, FragmentManager fManager, int key){
        this.mContext = context;
        this.eventList = eventList;
        this.fManager = fManager;
        this.key=key;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (key==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_explore, parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        db.collection("users").document(eventList.get(position).getHostId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("olo", "DocumentSnapshot data: " + document.getData());
                        holder.host.setText(document.get("username").toString());
                        Glide.with(getApplicationContext()).load(document.get("profimage").toString()).apply(new RequestOptions().fitCenter()).into(holder.eventprof);

                    } else {
                        Log.d("olo", "No such document");
                    }
                } else {
                    Log.d("olo", "get failed with ", task.getException());
                }
            }
        });


        if(eventList.get(position).getType().toString().equals("Private")){
            holder.eventlayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selector));
            holder.eventPrivate.setText("Private");
            holder.eventPrivate.setVisibility(View.VISIBLE);
        }else{//Public
            //Dont display Private Tag
            holder.eventlayout.setBackgroundDrawable(null);
            holder.eventPrivate.setVisibility(View.GONE);
        }

        /*db.collection("users").document(Profile.getCurrentProfile().getId()).collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("uid").equals(eventList.get(position).getHostId())){
                                }
                            }
                        } else {
                            Log.d("olo", "Error getting documents: ", task.getException());
                        }
                    }
                });*/
        //holder.eventprof.setBorderWidth(4);
        //holder.eventprof.setBorderColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        holder.title.setText(eventList.get(position).getTitle());//from Event.java
        holder.time.setText(eventList.get(position).getDate());
        holder.location.setText(eventList.get(position).getLoca_preset());
        //holder.image.setText(eventList.get(position).getTitle());
        Glide.with(getApplicationContext()).load(eventList.get(position).getImage()).apply(new RequestOptions().fitCenter()).into(holder.image);

        final String event_doc_id = eventList.get(position).eventId;
        final String event_doc_type = eventList.get(position).getType();
        final String imageUrl = "https://graph.facebook.com/" + eventList.get(position).getHostId() + "/picture?height=80&width=80&migration_overrides=%7Boctober_2012%3Atrue%7D";

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("GETID", event_doc_id + position);

                //Create a bundle to pass data, add data, set the bundle to your fragment and:
                mFragment = new SingleEventFragment();
                mBundle = new Bundle();
                mBundle.putString("event_id",event_doc_id);
                mBundle.putString("event_type",event_doc_type);
                mFragment.setArguments(mBundle);
                fManager.beginTransaction().replace(R.id.main_frame, mFragment).addToBackStack(null).commit();

            }
        });


        if(event_doc_type.equals("Archived") || eventList.get(position).getHostId().equals(Profile.getCurrentProfile().getId())){

            holder.favbutt.setVisibility(View.GONE);

        }else{

            holder.favbutt.setVisibility(View.VISIBLE);

            holder.favbutt.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View v) {

                    favboo=false;
                    Log.d("lol", "fav");

                    db.collection("users")//check friend status
                            .document(Profile.getCurrentProfile().getId())
                            .collection("favorites")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            Log.d("lol", document.getId() + " => " + document.getData());
                                            if(document.getId().equals(event_doc_id)){//if event already favorited before...

                                                favboo = true;
                                                setDialogRemoveFav(holder, position, v, event_doc_id);

                                                break;
                                            }

                                        }

                                        if(favboo==false){
                                            //add to favorite
                                            setDialogAddFav(holder, position, v, event_doc_id);

                                        }

                                    } else {
                                        Log.d("lol", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            });

        }

    }

    public void setDialogAddFav(final EventListAdapter.ViewHolder holder, final int position, View v, final String event_doc_id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        //Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                        //another one here
                        Map<String, Object> favtouser = new HashMap<>();
                        favtouser.put("eid", event_doc_id);//person to send request (Id of Viewing-user)

                        //holder.favbutt.setBackgroundResource(R.drawable.ic_favorite_white_24dp);

                        // Add a new pending join request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())//person to receive request
                                .collection("favorites")
                                .document(event_doc_id)
                                .set(favtouser);

                        //another one here
                        Map<String, Object> favtoevent = new HashMap<>();
                        favtoevent.put("uid", Profile.getCurrentProfile().getId());//person to send request (Id of Viewing-user)

                        //Add to event's "favoriters" collection
                        db.collection("events")
                                .document(event_doc_id)//person to receive request
                                .collection("favoriters")
                                .document(Profile.getCurrentProfile().getId())
                                .set(favtoevent);

                        Toast toast = Toast.makeText(getApplicationContext(),"Favorited "+eventList.get(position).getTitle(),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        //Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Add "+eventList.get(position).getTitle()+" to Favourites?").setPositiveButton("Add", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    public void setDialogRemoveFav(final EventListAdapter.ViewHolder holder, final int position, View v, final String event_doc_id){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        //Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                        //unfavorite
                        Map<String, Object> friendreq = new HashMap<>();
                        friendreq.put("eid", event_doc_id);//person to send request (Id of Viewing-user)

                        // Add a new pending join request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())//person to receive request
                                .collection("favorites")
                                .document(event_doc_id)
                                .delete();



                        Toast toast = Toast.makeText(getApplicationContext(),"Unfavorited "+eventList.get(position).getTitle(),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        //Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Remove "+eventList.get(position).getTitle()+" from Favourites?").setPositiveButton("Remove", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
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

        public CircleImageView eventprof;

        public TextView title, host, time, location, eventPrivate;

        public Button favbutt;

        public LinearLayout eventlayout;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            title = (TextView) mView.findViewById(R.id.post_title);
            host = (TextView) mView.findViewById(R.id.post_hostname);
            time = (TextView) mView.findViewById(R.id.post_time);
            location = (TextView) mView.findViewById(R.id.post_location);
            image = (ImageView) mView.findViewById(R.id.post_image);
            favbutt = mView.findViewById(R.id.addtofav);
            eventprof = mView.findViewById(R.id.event_profpic);
            eventlayout = mView.findViewById(R.id.event_linlayout);
            eventPrivate = mView.findViewById(R.id.event_private);

            //Add to Fav / Remove from Fav Toggle Switch
            //check if already in fav
            //if yes...
            //if no...


        }
    }
}
