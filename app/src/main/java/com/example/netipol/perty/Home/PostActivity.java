package com.example.netipol.perty.Home;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private String eventType, eventCateg, eventLoca;

    Button btnDatePickerStart, btnDatePickerEnd,  btnTimePickerStart, btnTimePickerEnd;
    EditText txtLocation, locaOther;

    private int mYear, mMonth, mDay, mHour, mMinute, hourOfDayStartCheck, minuteStartCheck, dayOfMonthStart, monthOfYearStart, yearStart;
    private String hostusername, loca_val, dateStartCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Spinner userLocaDropdown = findViewById(R.id.locationPresets);
        //create a list of items for the spinner.
        String[] itemsCateg = new String[]{"Select Category","SPORTS", "EDUCATION", "RECREATION","MUSIC","ART","THEATRE","TECHNOLOGY","OUTING","CAREER"};
        String[] itemsType = new String[]{"Select Type","PUBLIC", "PRIVATE"};
        String[] itemsLoca = new String[]{"Select Preset Location","Chulalongkorn University","Chamchuri Square", "I'm Park", "MBK", "Siam Paragon", "Central World", "Other"};
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
                    tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                    tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return view;
            }
        };
        ArrayAdapter<String> adapterLoca = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsLoca){@Override
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
                    tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                return view;
            }
        };
        //set the spinners adapter to the previously created one.
        userCategDropdown.setAdapter(adapterCateg);
        userCategDropdown.setOnItemSelectedListener(this);
        userTypeDropdown.setAdapter(adapterType);
        userTypeDropdown.setOnItemSelectedListener(this);
        userLocaDropdown.setAdapter(adapterLoca);
        userLocaDropdown.setOnItemSelectedListener(this);

        mUploadPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                startPosting();

            }

        });

        btnDatePickerStart=(Button)findViewById(R.id.btn_date_start);
        btnDatePickerEnd=(Button)findViewById(R.id.btn_date_end);
        btnDatePickerEnd.setEnabled(false);
        btnDatePickerEnd.getBackground().setAlpha(100);

        btnTimePickerStart=(Button)findViewById(R.id.btn_time_start);
        btnTimePickerStart.setEnabled(false);
        btnTimePickerStart.getBackground().setAlpha(100);
        btnTimePickerEnd=(Button)findViewById(R.id.btn_time_end);
        btnTimePickerEnd.setEnabled(false);
        btnTimePickerEnd.getBackground().setAlpha(100);
//        txtDate=(TextView)findViewById(R.id.in_date);
//        txtTime=(TextView)findViewById(R.id.in_time);
        txtLocation=(EditText)findViewById(R.id.in_location);
        locaOther = findViewById(R.id.locaOther);
        locaOther.setVisibility(View.GONE);

        btnDatePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnDatePickerStart.setText("START DATE");

                btnDatePickerEnd.setEnabled(false);
                btnDatePickerEnd.getBackground().setAlpha(100);
                btnDatePickerEnd.setText("END DATE");
                btnTimePickerStart.setEnabled(false);
                btnTimePickerStart.getBackground().setAlpha(100);
                btnTimePickerStart.setText("START TIME");
                btnTimePickerEnd.setEnabled(false);
                btnTimePickerEnd.getBackground().setAlpha(100);
                btnTimePickerEnd.setText("END TIME");
     //reset end date
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
                                String month = null;
                                switch (monthOfYear){
                                    case 0:
                                        month="JAN";
                                        break;
                                    case 1:
                                        month="FEB";
                                        break;
                                    case 2:
                                        month="MAR";
                                        break;
                                    case 3:
                                        month="APR";
                                        break;
                                    case 4:
                                        month="MAY";
                                        break;
                                    case 5:
                                        month="JUN";
                                        break;
                                    case 6:
                                        month="JUL";
                                        break;
                                    case 7:
                                        month="AUG";
                                        break;
                                    case 8:
                                        month="SEP";
                                        break;
                                    case 9:
                                        month="OCT";
                                        break;
                                    case 10:
                                        month="NOV";
                                        break;
                                    case 11:
                                        month="DEC";
                                        break;
                                }

                                dateStartCheck = dayOfMonth+"-"+monthOfYear+"-"+year;
                                final String dateStart = dayOfMonth + "-" + monthOfYear + "-" + year;//20 AUG 2016
                                dayOfMonthStart = dayOfMonth;
                                monthOfYearStart = monthOfYear;
                                yearStart = year;
                                final String dateStartNow = c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
                                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = null;
                                Date dateNow = null;
                                try {
                                    date = fmt.parse(dateStart);
                                    dateNow = fmt.parse(dateStartNow);
                                    if(date.before(dateNow)){
                                        Toast.makeText(PostActivity.this, "START DATE cannot be in the past!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        btnDatePickerStart.setText(dayOfMonth + " " + month + " " + year);
                                        btnDatePickerEnd.setEnabled(true);
                                        btnDatePickerEnd.getBackground().setAlpha(255);

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnDatePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnDatePickerEnd.setText("END DATE");

                btnTimePickerStart.setEnabled(false);
                btnTimePickerStart.getBackground().setAlpha(100);
                btnTimePickerStart.setText("START TIME");
                btnTimePickerEnd.setEnabled(false);
                btnTimePickerEnd.getBackground().setAlpha(100);
                btnTimePickerEnd.setText("END TIME");

                //reset end date
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
                                String month = null;
                                switch (monthOfYear){
                                    case 0:
                                        month="JAN";
                                        break;
                                    case 1:
                                        month="FEB";
                                        break;
                                    case 2:
                                        month="MAR";
                                        break;
                                    case 3:
                                        month="APR";
                                        break;
                                    case 4:
                                        month="MAY";
                                        break;
                                    case 5:
                                        month="JUN";
                                        break;
                                    case 6:
                                        month="JUL";
                                        break;
                                    case 7:
                                        month="AUG";
                                        break;
                                    case 8:
                                        month="SEP";
                                        break;
                                    case 9:
                                        month="OCT";
                                        break;
                                    case 10:
                                        month="NOV";
                                        break;
                                    case 11:
                                        month="DEC";
                                        break;
                                }

                                final String dateEnd = dayOfMonth + "-" + monthOfYear + "-" + year;//20-8-2016
                                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = null;
                                try {
                                    date = fmt.parse(dateEnd);
                                    if(date.before(fmt.parse(dateStartCheck))){
                                        Toast.makeText(PostActivity.this, "END DATE must be one the same day, or after START DATE!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        btnDatePickerEnd.setText(dayOfMonth + " " + month + " " + year);
                                        btnTimePickerStart.setEnabled(true);
                                        btnTimePickerStart.getBackground().setAlpha(255);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        btnTimePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTimePickerStart.setText("START TIME");

                btnTimePickerEnd.setEnabled(false);
                btnTimePickerEnd.getBackground().setAlpha(100);
                btnTimePickerEnd.setText("END TIME");
                //reset end date
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PostActivity.this, new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(minute >= 10) {
                                    if(dayOfMonthStart==c.get(Calendar.DAY_OF_MONTH) && monthOfYearStart==c.get(Calendar.MONTH)&& yearStart==c.get(Calendar.YEAR)) {//if set to present day...
                                        if ((hourOfDay < mHour)||(hourOfDay == mHour && minute < mMinute)) {
                                            Toast.makeText(PostActivity.this, "START TIME cannot be in the past!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            btnTimePickerStart.setText(hourOfDay + ":" + minute);
                                            btnTimePickerEnd.setEnabled(true);
                                            btnTimePickerEnd.getBackground().setAlpha(255);
                                            hourOfDayStartCheck = hourOfDay;
                                            minuteStartCheck = minute;
                                        }

                                    }else {
                                        btnTimePickerStart.setText(hourOfDay + ":" + minute);
                                        btnTimePickerEnd.setEnabled(true);
                                        btnTimePickerEnd.getBackground().setAlpha(255);
                                        hourOfDayStartCheck = hourOfDay;
                                        minuteStartCheck = minute;
                                    }

                                }else{
                                    if(dayOfMonthStart==c.get(Calendar.DAY_OF_MONTH) && monthOfYearStart==c.get(Calendar.MONTH)&& yearStart==c.get(Calendar.YEAR)) {//if set to present day...
                                        if ((hourOfDay < mHour)||(hourOfDay == mHour && minute < mMinute)) {
                                            Toast.makeText(PostActivity.this, "START TIME cannot be in the past!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            btnTimePickerStart.setText(hourOfDay + ":"+"0" + minute);
                                            btnTimePickerEnd.setEnabled(true);
                                            btnTimePickerEnd.getBackground().setAlpha(255);
                                            hourOfDayStartCheck = hourOfDay;
                                            minuteStartCheck = minute;
                                        }

                                    }else {
                                        btnTimePickerStart.setText(hourOfDay + ":"+"0" + minute);
                                        btnTimePickerEnd.setEnabled(true);
                                        btnTimePickerEnd.getBackground().setAlpha(255);
                                        hourOfDayStartCheck = hourOfDay;
                                        minuteStartCheck = minute;
                                    }
                                }
                            }

                        }, mHour, mMinute, true);

                timePickerDialog.show();
            }
        });

        btnTimePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTimePickerEnd.setText("END TIME");
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PostActivity.this, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(minute >= 10) {
                            if(btnDatePickerStart.getText().toString().equals(btnDatePickerEnd.getText().toString())){
                                if((hourOfDay<hourOfDayStartCheck) || (hourOfDay==hourOfDayStartCheck && minute<minuteStartCheck)){
                                    //not ok
                                    Toast.makeText(PostActivity.this, "END TIME must be after START TIME!", Toast.LENGTH_SHORT).show();
                                }else{
                                    btnTimePickerEnd.setText(hourOfDay + ":" + minute);
                                }
                            }else{
                                btnTimePickerEnd.setText(hourOfDay + ":" + minute);
                            }
                        }else{
                            if (btnDatePickerStart.getText().toString().equals(btnDatePickerEnd.getText().toString())){
                                if((hourOfDay<hourOfDayStartCheck) || (hourOfDay==hourOfDayStartCheck && minute<minuteStartCheck)){
                                    //not ok
                                    Toast.makeText(PostActivity.this, "END TIME must be after START TIME!", Toast.LENGTH_SHORT).show();
                                }else{
                                    btnTimePickerEnd.setText(hourOfDay + ":" +"0"+ minute);
                                }
                            }else{
                                btnTimePickerEnd.setText(hourOfDay + ":" + "0"+minute);
                            }
                        }

                    }

                }, mHour, mMinute, true);

                timePickerDialog.show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        else if(spinner.getId()==R.id.locationPresets)
        {
            switch (i) {
                case 1:
                    eventLoca = "Chulalongkorn University";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 2:
                    eventLoca = "Chamchuri Square";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 3:
                    eventLoca = "I'm Park";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 4:
                    eventLoca = "MBK";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 5:
                    eventLoca = "Siam Paragon";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 6:
                    eventLoca = "Central World";
                    locaOther.setVisibility(View.GONE);
                    break;
                case 7:
                    eventLoca = "Other";
                    locaOther.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Don't allow it
    }


    private void startPosting(){

        final String title_val = mPostTitle.getText().toString().trim();//ok
        final String desc_val = mPostDesc.getText().toString().trim();//ok

        final String categ_val = eventCateg;//ok
        final String type_val = eventType;//ok
        if(eventLoca!=null){
            if(eventLoca.equals("Other")){
                loca_val = locaOther.getText().toString().trim();//ok
            }else{
                loca_val = eventLoca;//ok
            }
        }
        final String location_val = txtLocation.getText().toString().trim();

        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();
        
        final String date_start_val = btnDatePickerStart.getText().toString().trim();//21 AUG 1996
        final String date_end_val = btnDatePickerEnd.getText().toString().trim();//21 AUG 1996
        final String time_start_val = btnTimePickerStart.getText().toString().trim();//24:00
        final String time_end_val = btnTimePickerEnd.getText().toString().trim();//24:00
        
        //final String posttime_val = date_val + ", " + time_val;


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

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(location_val) && !date_start_val.equals("DATE START") && !date_end_val.equals("DATE END") && !time_start_val.equals("START TIME") && !time_end_val.equals("END TIME") && resultUri != null  && !TextUtils.isEmpty(categ_val)  && !TextUtils.isEmpty(type_val)  && !TextUtils.isEmpty(loca_val)){//Add parameter check!!!

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
                    event.put("desc", desc_val);//make post compatible with html link
                    event.put("type", type_val);
                    event.put("categ", categ_val);
                    event.put("image", downloadUrl.toString());

                    event.put("timestamp", ts);//time of creation (post)

                    //event.put("host", hostusername);//need to be retrieved again at SingleEventFrage

                    event.put("time_start",time_start_val);
                    event.put("time_end", time_end_val);
                    event.put("date_start", date_start_val);
                    event.put("date_end", date_end_val);//new

                    event.put("loca_preset",loca_val);//loca main
                    event.put("loca_desc", location_val);//buildings, floors, rooms + extra desc.

                    event.put("hostid", Profile.getCurrentProfile().getId());

                    // Add a new document with a generated ID
                    db.collection("events")
                            .document()
                            .set(event);

                    mProgress.dismiss();

                    Toast.makeText(PostActivity.this, "Your event was successfully posted.", Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        }
        else {
            Toast.makeText(PostActivity.this, "Please complete all parameters.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Create Event");
        bar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {

            mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
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
