package com.example.netipol.perty.Login.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.netipol.perty.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageButton mSelectImage;
    private EditText mPostTitle, mPostDesc;
    private Button mUploadPost;

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;

    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage = FirebaseStorage.getInstance().getReference();

        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        mUploadPost = (Button) findViewById(R.id.eventUpload);

        mPostTitle = (EditText) findViewById(R.id.eventTitle);
        mPostDesc = (EditText) findViewById(R.id.eventDesc);

        mProgress = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        //get the spinner from the xml.
        Spinner userTypeDropdown = findViewById(R.id.eventType);
        //create a list of items for the spinner.
        String[] items = new String[]{"Public", "Private"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        userTypeDropdown.setAdapter(adapter);
        userTypeDropdown.setOnItemSelectedListener(this);

        mUploadPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                startPosting();

            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                eventType = "Public";
                break;
            case 1:
                eventType = "Private";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Don't allow it
    }


    private void startPosting(){

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        final String type_val = eventType;

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){

            mProgress.setMessage("Posting your Perty event ...");
            mProgress.show();

            //upload process
            StorageReference filepath = mStorage.child("Event_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Create a new user with a first and last name
                    Map<String, Object> event = new HashMap<>();
                    event.put("title", title_val);
                    event.put("desc", desc_val);
                    event.put("type", type_val);
                    event.put("image", downloadUrl.toString());

                    // Add a new document with a generated ID
                    db.collection("events")
                            .document()
                            .set(event);

                    mProgress.dismiss();

                    Toast.makeText(PostActivity.this, "Your post was successfully uploaded.",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {

            mImageUri = data.getData();

            mSelectImage.setImageURI(mImageUri);
        }
    }
}
