package com.example.netipol.perty.Util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.example.netipol.perty.Home.SingleEventActivity;
import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.Model.FriendReq;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReqListAdapter extends RecyclerView.Adapter<FriendReqListAdapter.ViewHolder> {

    public List<FriendReq> friendReqList;
    public Context context;
    public String friendReqUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FriendReqListAdapter(Context context, List<FriendReq> friendReqList){

        this.friendReqList = friendReqList;
    }

    @Override
    public FriendReqListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendreq_row, parent,false);
        return new FriendReqListAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final FriendReqListAdapter.ViewHolder holder, final int position) {


        db.collection("users")
                .document(friendReqList.get(position).getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            friendReqUser = doc.get("username").toString();
                            holder.name.setText("You have a friend request from "+friendReqUser);//from Event.java
                        }
                    }
                });


        //final String event_doc_id = friendReqList.get(position).friendreqId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE://Accept friend request
                                Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_SHORT).show();

                                //Send add request to user NOTICE
                                Map<String, Object> friendreq = new HashMap<>();
                                friendreq.put("uid", friendReqList.get(position).getName());//person to add

                                // Add a new friend
                                db.collection("users")
                                        .document(Profile.getCurrentProfile().getId())//current user
                                        .collection("friends")
                                        .document(friendReqList.get(position).getName())
                                        .set(friendreq);

                                //Remove friend request
                                db.collection("users")
                                        .document(Profile.getCurrentProfile().getId())
                                        .collection("requests")
                                        .document(friendReqList.get(position).getName())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("friendreq", "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("friendreq", "Error deleting document", e);
                                            }
                                        });


                                break;

                            case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                                Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_SHORT).show();

                                //Remove friend request
                                db.collection("users")
                                        .document(Profile.getCurrentProfile().getId())
                                        .collection("requests")
                                        .document(friendReqList.get(position).getName())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("friendreq", "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("friendreq", "Error deleting document", e);
                                            }
                                        });

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Accept "+friendReqUser+"'s friend request?").setPositiveButton("Accept", dialogClickListener)
                        .setNegativeButton("Decline", dialogClickListener).show();
            }
        });


    }



    @Override
    public int getItemCount() {
        return friendReqList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView name;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.friendreq_name);

        }
    }

}
