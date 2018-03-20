package com.example.netipol.perty.Home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.netipol.perty.Event.Event;
import com.example.netipol.perty.Event.EventListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private List<String> titleList;
    private String searchText;


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
        titleList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getApplicationContext(), eventList,getFragmentManager());

        mEventList = v.findViewById(R.id.result_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventList.setAdapter(eventListAdapter);

        mSearchField = v.findViewById(R.id.search_field);
        mSearchBtn = v.findViewById(R.id.search_btn);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchText = mSearchField.getText().toString();
                //Check null
                if(searchText.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid search",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    return;
                }

                eventList.clear();
                titleList.clear();


                  /*self.posts.removeAll()
    guard let searchText = self.searchBar.text else { return }
        Database.database().reference().child("Posts").queryOrdered(byChild: "createdAt").observeSingleEvent(of: .value) { (snap) in
        guard let value = snap.value as? [String:Any] else {
            self.tableView.reloadData()
            return
        }
        for post in value {
            let post = MapperManager<Post>.mapObject(dictionary: post.value as! [String:Any])
            if post.title!.lowercased().contains(searchText.lowercased()) {
                self.posts.append(post)
            }
        }
        self.tableView.reloadData()
        self.refreshControl.endRefreshing()
        self.typeCount = 0
        return
    }*/

                /*CollectionReference eventsRef = mFirestore.collection("events");
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

        });*/

                //get list of joined events and store it into a arraylist
                mFirestore.collection("events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (DocumentSnapshot document : task.getResult()) {
                                        //Convert to lowercase
                                        titleList.add(document.get("title").toString().toLowerCase());
                                        Log.d("ddog", "List size: "+Integer.toString(titleList.size()));
                                    }

                                    CollectionReference eventsRef = mFirestore.collection("events");
                                    eventsRef.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                            if (e != null) {
                                                Log.d("ddog", "Error : " + e.getMessage());
                                            }

                                            for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {//getDocuments) {

                                                //for (int i = 0; i < titleList.size(); i++) {

                                                    if (doc.get("title").toString().toLowerCase().contains(searchText.toLowerCase())) {

                                                        String event_id = doc.getId();
                                                        Log.d("GETID at SearchFrag", event_id);
                                                        Event events = doc.toObject(Event.class).withId(event_id);
                                                        //eventList.add(0,events);//add new events whenever there is a change
                                                        //eventListAdapter.notifyItemInserted(0);
                                                        eventList.add(events);
                                                        eventListAdapter.notifyDataSetChanged();

                                                    }
                                                //}
                                            }

                                            if(eventList.size()==0){
                                                Toast toast = Toast.makeText(getApplicationContext(),"No matches",Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.show();
                                            }
                                        }

                                    });
                                }
                            }
                        });

                eventList.clear();
                titleList.clear();
            }

        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        bar.setTitle("Search");
        bar.setDisplayHomeAsUpEnabled(false);
    }
}