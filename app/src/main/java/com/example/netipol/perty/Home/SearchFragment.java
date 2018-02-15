package com.example.netipol.perty.Home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.netipol.perty.Model.Event;
import com.example.netipol.perty.Util.EventListAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import com.example.netipol.perty.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    private RecyclerView mEventList;
    private List<Event> eventList;
    private EventListAdapter eventListAdapter;
    private FirebaseFirestore mFirestore;

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        eventList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventList);

        mEventList = v.findViewById(R.id.result_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        mSearchField = v.findViewById(R.id.search_field);
        mSearchBtn =  v.findViewById(R.id.search_btn);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                eventList.clear();

                CollectionReference eventsRef = mFirestore.collection("events");
                Query query = eventsRef.orderBy("title").startAt(searchText).endAt(searchText + "\uf8ff");
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if(e != null){
                            Log.d("FeedLog", "Error : " + e.getMessage());
                        }

                        for(DocumentChange change : documentSnapshots.getDocumentChanges()){

                            if(change.getType() == DocumentChange.Type.ADDED){ //MODIFIED, REMOVED ??

                                String event_id = change.getDocument().getId();
                                Log.d("GETID at SearchFrag", event_id);
                                Event events = change.getDocument().toObject(Event.class).withId(event_id);
                                eventList.add(events);

                                eventListAdapter.notifyDataSetChanged();

                                }
                            }

                        }
                 });


          }

        });

        return v;
    }
}