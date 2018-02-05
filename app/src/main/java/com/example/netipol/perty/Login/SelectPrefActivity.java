package com.example.netipol.perty.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.netipol.perty.Home.BaseActivity;
import com.example.netipol.perty.Util.GridItemView;
import com.example.netipol.perty.Util.GridViewAdapter;
import com.example.netipol.perty.R;
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
            "SPORTS", "EDUCATION", "RECREATION"};


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
                    ((GridItemView) v).display(false);
                    selectedStrings.remove((String) parent.getItemAtPosition(position));
                } else {
                    adapter.selectedPositions.add(position);
                    ((GridItemView) v).display(true);
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
                        }
                    }
                }

                Map<String, Object> categ = new HashMap<>();
                categ.put("categ_key", categ_key);

                // Add a new document with a generated ID

                db.collection("users")
                        .document(MainActivity.fbUID)
                        .set(categ, SetOptions.merge());

                Intent intent = new Intent(SelectPrefActivity.this, BaseActivity.class);
                //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                //intent.putExtra("categ", categ_key);
                startActivity(intent);
            }
        });
    }
}
