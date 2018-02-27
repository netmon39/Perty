package com.example.netipol.perty.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.netipol.perty.Home.PostActivity;
import com.example.netipol.perty.Login.LoginActivity;
import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.R;
import com.example.netipol.perty.Util.EventListAdapter;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by USER on 08/02/2018.
 */

public class UpcomingFragment extends Fragment {
    private static final String TAG = "TabUPFragment";

    private RecyclerView mEventList;
    private List<Event> eventList;
    private List<String> joinList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;
    private String mUser_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upcoming_tab, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mUser_id = Profile.getCurrentProfile().getId();

        eventList = new ArrayList<>();
        joinList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList);

        mEventList = v.findViewById(R.id.upcoming_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        //get list of joined events and store it into a arraylist or somethingbbkbjb
        mFirestore.collection("users").document(mUser_id).collection("joining")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                joinList.add(document.get("eid").toString());
                                Toast.makeText(getApplicationContext(), joinList.get(0),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        CollectionReference eventsRef = mFirestore.collection("events");
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d("FeedLog", "Error : " + e.getMessage());
                }

                for (DocumentChange change : documentSnapshots.getDocumentChanges()) {

                    for (int i = 0; i < joinList.size(); i++) {

                        if (change.getDocument().getId().equals(joinList.get(i).toString()) && change.getType() == DocumentChange.Type.ADDED) {

                                String event_id = change.getDocument().getId();
                                Log.d("GETID at SearchFrag", event_id);
                                Event events = change.getDocument().toObject(Event.class).withId(event_id);
                                eventList.add(events);
                                eventListAdapter.notifyDataSetChanged();//"joining" collection needs corresponding recycler view adaptor

                            }
                        }

                    }
                }

        });

        return v;
    }
}
