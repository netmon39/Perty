package com.teamperty.netipol.perty.SelectPref;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.teamperty.netipol.perty.R;
import com.teamperty.netipol.perty.Login.TutorialActivity;
import com.facebook.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectPrefActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private char[] cArray;
    private GridView gridView;
    private View btnGo;
    private ArrayList<String> selectedStrings;
    private String categ_key = "";
    private static final String[] numbers = new String[]{
            "SPORTS", "EDUCATION", "RECREATION","MUSIC","ART","THEATRE","TECHNOLOGY","OUTING","CAREER"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pref);

        gridView = (GridView) findViewById(R.id.grid);
        btnGo = findViewById(R.id.button);

        selectedStrings = new ArrayList<>();

        final GridViewAdapter adapter = new GridViewAdapter(numbers, this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int selectedIndex = adapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapter.selectedPositions.remove(selectedIndex);
                    ((GridItemView) v).display(false, position);
                    Log.d("categ", "deselected pos: "+position);
                    selectedStrings.remove((String) parent.getItemAtPosition(position));
                } else {
                    adapter.selectedPositions.add(position);
                    Log.d("categ", "selected pos: "+position);
                    ((GridItemView) v).display(true, position);
                    selectedStrings.add((String) parent.getItemAtPosition(position));
                }
            }
        });

        //gridView.setSelection(4);


        /*Category Map
            SPORTS = 0
            EDUCATION = 1
            RECREATION = 2

            ...
         */

        //From AccountAcitivity: First time
        // or EventFragment: load user's current categ_key and preselect the grid?


        /*/1. Get user's categ_key
        db.collection("users").document(Profile.getCurrentProfile().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("categkey", "DocumentSnapshot data: " + document.getData());

                        categ_key = document.get("categ_key").toString();

                        Log.d("categkey", categ_key);

                        //2. Decrypt key and store it into an array
                        cArray = categ_key.toCharArray();

                        //3. For loop the cArray, while querying "events" to look for corresponding categories
                        for(int i = 0; i < cArray.length; i++) {

                            Log.d("retrieved key", String.valueOf(cArray[i]));

                            switch (String.valueOf(cArray[i])) {
                                case "0":
                                    adapter.selectedPositions.add(0);
                                    selectedStrings.add(numbers[0]);
                                    break;
                                case "1":
                                    adapter.selectedPositions.add(1);
                                    selectedStrings.add(numbers[1]);
                                    break;
                                case "2":
                                    adapter.selectedPositions.add(2);
                                    selectedStrings.add(numbers[2]);
                                    break;
                                case "3":
                                    adapter.selectedPositions.add(3);
                                    selectedStrings.add(numbers[3]);
                                case "4":
                                    adapter.selectedPositions.add(4);
                                    selectedStrings.add(numbers[4]);
                                    break;
                                case "5":
                                    adapter.selectedPositions.add(5);
                                    selectedStrings.add(numbers[5]);
                                    break;
                                case "6":
                                    adapter.selectedPositions.add(6);
                                    selectedStrings.add(numbers[6]);
                                case "7":
                                    adapter.selectedPositions.add(7);
                                    selectedStrings.add(numbers[7]);
                                    break;
                                case "8":
                                    adapter.selectedPositions.add(8);
                                    selectedStrings.add(numbers[8]);
                                    break;

                            }

                        }

                        categ_key = "";

                    } else {
                        Log.d("olo", "No such document");
                    }
                } else {
                    Log.d("olo", "get failed with ", task.getException());
                }
            }
        });*/


        //set listener for Button event
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert selectedStrings != null;
                if (selectedStrings.size() > 0) {
                    for (int i = 0; i < selectedStrings.size(); i++) {
                        if(selectedStrings.get(i).equals("SPORTS")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("0")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("EDUCATION")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("1")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("RECREATION")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("2")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("MUSIC")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("3")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("ART")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("4")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("THEATRE")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("5")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("TECHNOLOGY")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("6")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("OUTING")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("7")
                                    .toString();
                            Log.d("categ", categ_key);
                        }else if(selectedStrings.get(i).equals("CAREER")){
                            categ_key = new StringBuilder()
                                    .append(categ_key)
                                    .append("8")
                                    .toString();
                            Log.d("categ", categ_key);                        }
                    }
                }

                Map<String, Object> categ = new HashMap<>();
                categ.put("categ_key", categ_key);
                categ.put("pp_key", "ab"); //a: public, b: private

                // Add a new document with a generated ID

                db.collection("users")
                        .document(Profile.getCurrentProfile().getId())
                        .set(categ, SetOptions.merge());

                Intent intent = new Intent(SelectPrefActivity.this, TutorialActivity.class);
                //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                //intent.putExtra("categ", categ_key);
                startActivity(intent);
                finish();
            }
        });
    }
}
