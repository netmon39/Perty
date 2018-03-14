package com.example.netipol.perty.SelectPref;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectPrefActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    selectedStrings.remove((String) parent.getItemAtPosition(position));
                } else {
                    adapter.selectedPositions.add(position);
                    ((GridItemView) v).display(true, position);
                    selectedStrings.add((String) parent.getItemAtPosition(position));
                }
            }
        });

        /*Category Map
            SPORTS = 0
            EDUCATION = 1
            RECREATION = 2

            ...
         */

        //set listener for Button event
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert selectedStrings != null;
                if (selectedStrings.size() > 0) {
                    for (int i = 0; i < selectedStrings.size(); i++) {
                        if(selectedStrings.get(i).equals("SPORTS")){
                            categ_key = new StringBuilder().append(categ_key)
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

                // Add a new document with a generated ID

                db.collection("users")
                        .document(Profile.getCurrentProfile().getId())
                        .set(categ, SetOptions.merge());

                Intent intent = new Intent(SelectPrefActivity.this, MainActivity.class);
                //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                //intent.putExtra("categ", categ_key);
                startActivity(intent);
            }
        });
    }
}
