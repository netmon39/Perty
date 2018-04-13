package com.example.netipol.perty.Friend;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.Home.SearchFragment;
import com.example.netipol.perty.Home.SingleEventFragment;
import com.example.netipol.perty.Profile.FriendFragment;
import com.example.netipol.perty.Profile.NotificationFragment;
import com.example.netipol.perty.Profile.ProfileFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReqListAdapter extends RecyclerView.Adapter<FriendReqListAdapter.ViewHolder> {

    public List<FriendReq> friendReqList;
    public Context context;
    public String friendReqUser, eventHostId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationFragment fragmentNoti;
    private FriendFragment fragmentFrnd;
    public int key;
    private FragmentCommunication mCommunicator;
    private android.support.v4.app.Fragment mFragment;
    private Bundle mBundle;
    public android.support.v4.app.FragmentManager fManager;

    //int x = key
    public FriendReqListAdapter(Context context, List<FriendReq> friendReqList, NotificationFragment fragmentNoti, FriendFragment fragmentFrnd, int x, FragmentManager fManager){
        this.friendReqList = friendReqList;
        this.fragmentNoti = fragmentNoti;
        this.fragmentFrnd = fragmentFrnd;
        this.fManager = fManager;
        this.key = x;
        String uid = Profile.getCurrentProfile().getId();//10211002845724097\


    }

    public interface FragmentCommunication {
        void respond(int position,String name,String job);
    }

    @Override
    public FriendReqListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendreq_row, parent,false);

        return new FriendReqListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FriendReqListAdapter.ViewHolder holder, final int position) {

        db.collection("users")
                .document(friendReqList.get(position).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            friendReqUser = doc.get("username").toString();
                            Glide.with(getApplicationContext()).load(doc.get("profimage").toString()).apply(new RequestOptions().fitCenter()).into(holder.profPic);

                            switch (key){
                                case 0:
                                    holder.name.setText("You have a friend request from "+friendReqUser);//from NotiFragment.java
                                    break;
                                case 1:
                                    holder.name.setText(friendReqUser);//from FriendFragment.java
                                    break;
                                case 2:
                                    holder.name.setText(friendReqUser);//from SearchFragment.java
                                    break;
                            }
                        }
                    }
                });


        //final String event_doc_id = friendReqList.get(position).friendreqId;


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (key){
                    case 0:
                        setDialogNotice(holder, position, v);
                        break;
                    case 1:
                        setDialogFriend(holder, position, v);
                        break;
                    case 2:
                        db.collection("users")
                                .document(friendReqList.get(position).getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            eventHostId = doc.getId();
                                            mFragment = new ProfileFragment();
                                            mBundle = new Bundle();
                                            mBundle.putString("host_id",eventHostId);
                                            mFragment.setArguments(mBundle);
                                            fManager.beginTransaction().replace(R.id.main_frame, mFragment).addToBackStack(null).commit();
                                        }
                                    }
                                });

                        break;
                }


            }
        });


    }

    public void setDialogNotice(final FriendReqListAdapter.ViewHolder holder, final int position, View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        Toast.makeText(getApplicationContext(), "Request accepted.",Toast.LENGTH_LONG).show();

                        //Send add request to user NOTICE
                        Map<String, Object> friendreqAcc = new HashMap<>();
                        friendreqAcc.put("uid", friendReqList.get(position).getUid());//person to add

                        // Add a new friend for acceptor
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())//current user
                                .collection("friends")
                                .document(friendReqList.get(position).getUid())
                                .set(friendreqAcc);

                        //Send add request to user NOTICE
                        Map<String, Object> friendreqReq = new HashMap<>();
                        friendreqReq.put("uid", Profile.getCurrentProfile().getId());//person to add

                        // Add a new friend for requestor
                        db.collection("users")
                                .document(friendReqList.get(position).getUid())//current user
                                .collection("friends")
                                .document(Profile.getCurrentProfile().getId())
                                .set(friendreqReq);

                        //Remove friend request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())
                                .collection("requests")
                                .document(friendReqList.get(position).getUid())
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

                        fragmentNoti.loadRecyclerViewData();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        Toast.makeText(getApplicationContext(), "Request declined.",Toast.LENGTH_LONG).show();

                        //Remove friend request
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())
                                .collection("requests")
                                .document(friendReqList.get(position).getUid())
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
                        fragmentNoti.loadRecyclerViewData();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Accept "+friendReqUser+"'s friend request?").setPositiveButton("Accept", dialogClickListener)
                .setNegativeButton("Decline", dialogClickListener).show();
    }

    public void setDialogFriend(final FriendReqListAdapter.ViewHolder holder, final int position, View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE://Accept friend request
                        Toast.makeText(getApplicationContext(), "You unfriended "+friendReqUser,Toast.LENGTH_LONG).show();

                        //Remove friend for acceptor
                        db.collection("users")
                                .document(Profile.getCurrentProfile().getId())
                                .collection("friends")
                                .document(friendReqList.get(position).getUid())
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

                        //Remove friend for requestor
                        db.collection("users")
                                .document(friendReqList.get(position).getUid())
                                .collection("friends")
                                .document(Profile.getCurrentProfile().getId())
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

                        fragmentFrnd.loadRecyclerViewData();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Decline friend request
                        Toast.makeText(getApplicationContext(), "Cancelled",Toast.LENGTH_LONG).show();

                        fragmentFrnd.loadRecyclerViewData();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Unfriend "+friendReqUser+"?").setPositiveButton("Unfriend", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }


    @Override
    public int getItemCount() {
        return friendReqList.size();
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

        public TextView name;
        public CircleImageView profPic;

        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.friendreq_name);
            profPic = mView.findViewById(R.id.friendreq_profpic);


        }


    }

}
