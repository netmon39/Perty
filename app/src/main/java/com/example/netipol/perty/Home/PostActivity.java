package com.example.netipol.perty.Home;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.netipol.perty.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageButton mSelectImage;
    private EditText mPostTitle, mPostDesc;
    private Button mUploadPost;

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private Uri resultUri = null;

    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventType, eventCateg;

    Button btnDatePicker, btnTimePicker;
    TextView txtDate, txtTime;
    EditText txtLocation;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String hostusername;

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
        Spinner userCategDropdown = findViewById(R.id.eventCategories);
        Spinner userTypeDropdown = findViewById(R.id.eventType);
        //create a list of items for the spinner.
        String[] itemsCateg = new String[]{"Select Category","SPORTS", "EDUCATION", "RECREATION","MUSIC","ART","THEATRE","TECHNOLOGY","OUTING","CAREER"};
        String[] itemsType = new String[]{"Select Type","PUBLIC", "PRIVATE"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterCateg = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsCateg){@Override
        public boolean isEnabled(int position) {
            if (position == 0) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            } else {
                return true;
            }
        }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsType){@Override
        public boolean isEnabled(int position) {
            if (position == 0) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            } else {
                return true;
            }
        }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        //set the spinners adapter to the previously created one.
        userCategDropdown.setAdapter(adapterCateg);
        userCategDropdown.setOnItemSelectedListener(this);
        userTypeDropdown.setAdapter(adapterType);
        userTypeDropdown.setOnItemSelectedListener(this);

        mUploadPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                startPosting();

            }

        });

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
//        txtDate=(TextView)findViewById(R.id.in_date);
//        txtTime=(TextView)findViewById(R.id.in_time);
        txtLocation=(EditText)findViewById(R.id.in_location);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                btnDatePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PostActivity.this, new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(minute >= 10) {
                                    btnTimePicker.setText(hourOfDay + ":" + minute);
                                }else{
                                    btnTimePicker.setText(hourOfDay + ":" + "0"+minute);
                                }
                            }

                        }, mHour, mMinute, true);

                timePickerDialog.show();
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.eventCategories)
        {
            switch (i) {
                case 1:
                    eventCateg = "SPORTS";
                    break;
                case 2:
                    eventCateg = "EDUCATION";
                    break;
                case 3:
                    eventCateg = "RECREATION";
                    break;
                case 4:
                    eventCateg = "MUSIC";
                    break;
                case 5:
                    eventCateg = "ART";
                    break;
                case 6:
                    eventCateg = "THEATRE";
                    break;
                case 7:
                    eventCateg = "TECHNOLOGY";
                    break;
                case 8:
                    eventCateg = "OUTING";
                    break;
                case 9:
                    eventCateg = "CAREER";
                    break;
            }
        }
        else if(spinner.getId() == R.id.eventType)
        {
            switch (i) {
                case 1:
                    eventType = "Public";
                    break;
                case 2:
                    eventType = "Private";
                    break;
            }
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
        final String categ_val = eventCateg;
        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();

        final String date_val = btnDatePicker.getText().toString().trim();
        final String time_val = btnTimePicker.getText().toString().trim();
        final String posttime_val = date_val + ", " + time_val;
        final String location_val = txtLocation.getText().toString().trim();

        DocumentReference docRef = db.collection("users").document(Profile.getCurrentProfile().getId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        hostusername = document.get("username").toString();
                    } else {
                    }
                } else {
                }
            }
        });

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && resultUri != null){//Add parameter check!!!

            mProgress.setMessage("Posting your Perty event ...");
            mProgress.show();

            //upload process
            StorageReference filepath = mStorage.child("Event_Images").child(resultUri.getLastPathSegment());

            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Create a new event
                    Map<String, Object> event = new HashMap<>();
                    event.put("title", title_val);
                    event.put("desc", desc_val);
                    event.put("type", type_val);
                    event.put("categ", categ_val);
                    event.put("image", downloadUrl.toString());
                    event.put("timestamp", ts);
                    event.put("host", hostusername);
                    event.put("time", posttime_val);
                    event.put("location", location_val);
                    event.put("hostid", Profile.getCurrentProfile().getId());

                    // Add a new document with a generated ID
                    db.collection("events")
                            .document()
                            .set(event);

                    mProgress.dismiss();

                    Toast.makeText(PostActivity.this, "Your post was successfully uploaded.", Toast.LENGTH_SHORT).show();

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

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4,3)
                    .setCropMenuCropButtonTitle("NEXT")
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();

                mSelectImage.setImageURI(resultUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
