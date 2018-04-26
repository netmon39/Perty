package com.example.netipol.perty.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.netipol.perty.R;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "FIRESTORELOG";
    public Button nextToP, getFbProf;
    public EditText userNameF, accountD;
    public String userName, accountDescription;
    public String userType = "x";
    public Uri FBimageUri;
    public String selectedItemText;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private Uri resultUri = null;
    public CircleImageView mSelectImage;
    private StorageReference mStorage;
    private UploadTask uploadTask;
    private Bitmap fbProf;
    private Spinner userTypeDropdown;
    private ProgressDialog mProgress;
    private ProfileFragment profFrag;
    private TextView prefText;
    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FacebookSdk.sdkInitialize(this);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        /*Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        fbUID = inBundle.get("fbUID").toString();
        FBimageUri = Uri.parse(inBundle.get("imageUri").toString());*/

        db = FirebaseFirestore.getInstance();

        profFrag =new ProfileFragment();

        /*TextView nameView = (TextView) findViewById(R.id.fbName);
        nameView.setText("" + Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());*/

        mSelectImage = findViewById(R.id.profileImage);
        userNameF = findViewById(R.id.userNameField);
        accountD = findViewById(R.id.userBio);
        getFbProf = findViewById(R.id.getFbProf);

        grid = findViewById(R.id.grid);
        prefText = findViewById(R.id.pref_text);
        grid.setVisibility(View.GONE);
        prefText.setVisibility(View.GONE);


        mProgress = new ProgressDialog(this);

        //get the spinner from the xml.
        userTypeDropdown = findViewById(R.id.userType);
        //create a list of items for the spinner.
        String[] items = new String[]{"Student", "Professor", "Club", "Official"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items){
            /*@Override
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
            }*/
        };
        //set the spinners adapter to the previously created one.
        userTypeDropdown.setAdapter(adapter);
        userTypeDropdown.setOnItemSelectedListener(this);

        db.collection("users").document(Profile.getCurrentProfile().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("editprof", "DocumentSnapshot data: " + document.getData());
                        userNameF.setText(document.get("username").toString());
                        accountD.setText(document.get("accountdesc").toString());

                        int spinnerPosition = adapter.getPosition(document.get("usertype").toString());
                        Log.d("editprof", "pos: " + spinnerPosition);
                        userTypeDropdown.setSelection(spinnerPosition);


                        Glide.with(getApplicationContext()).load(document.get("profimage").toString()).apply(new RequestOptions().fitCenter()).into(mSelectImage);

                    } else {
                        Log.d("olo", "No such document");
                    }
                } else {
                    Log.d("olo", "get failed with ", task.getException());
                }
            }
        });


        //new EditProfileActivity.DownloadImage(mSelectImage).execute(Profile.getCurrentProfile().getProfilePictureUri(200,200).toString());

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        getFbProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Getting your profile pic ...");
                mProgress.show();
                new EditProfileActivity.DownloadImage(mSelectImage).execute(Profile.getCurrentProfile().getProfilePictureUri(200,200).toString());
            }
        });

        nextToP = findViewById(R.id.nextToPref);
        nextToP.setText("Update Account");

        /*/ Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReferenceFromUrl("https://perty-53386.firebaseio.com/Users/User_"+fbUID);*/

        nextToP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//write fields to firebase
                startNext();
            }
        });
    }

    private void startNext() {

        if(!TextUtils.isEmpty(userNameF.getText().toString().trim()) && !TextUtils.isEmpty(accountD.getText().toString().trim())) {//Add parameter check!!!

            userName = userNameF.getText().toString();
            accountDescription = accountD.getText().toString();

            if(userName.length()==0 || accountDescription.length()==0 || userType.equals("x")){
                Toast.makeText(getApplicationContext(), "Please fill every field :D", Toast.LENGTH_SHORT).show();
                return;
            }

            mProgress.setMessage("Updating your info ...");
            mProgress.show();

            //upload process
            if(resultUri==null){
                // Get the data from an ImageView as bytes
                mSelectImage.setDrawingCacheEnabled(true);
                mSelectImage.buildDrawingCache();
                Bitmap bitmap = mSelectImage.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                uploadTask = mStorage.child("Profile_Images").child(Profile.getCurrentProfile().getId()+".jpg").putBytes(data);
            }else {
                uploadTask = mStorage.child("Profile_Images").child(Profile.getCurrentProfile().getId()+".jpg").putFile(resultUri);
            }

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", userName);
                    user.put("accountdesc", accountDescription);
                    user.put("usertype", userType);
                    user.put("uid", Profile.getCurrentProfile().getId());
                    user.put("profimage", downloadUrl.toString());

                    // Add a new document with a generated ID
                    db.collection("users")
                            .document(Profile.getCurrentProfile().getId())
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("acc", "DocumentSnapshot added");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("acc", "Error adding document", e);
                                }
                            });

                    finish();
                }
            });


        }else{
            Toast.makeText(this, "Please fill all parameters",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        mProgress.dismiss();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Edit Profile");
        //bar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0://Student
                userType = "Student";
                break;
            case 1://Prof
                userType = "Professor";
                break;
            case 2://Club
                userType = "Club";
                break;
            case 3://Official
                userType = "Official";
                break;
        }

        //String selectedItemText = (String) adapterView.getItemAtPosition(i);
        //selectedItemText = (String) adapterView.getItemAtPosition(i);
        // If user change the default selection
        // First item is disable and it is used for hint
        /*if (i >= 0) {
            // Notify the selected item text
            Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Don't allow it
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {

            mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1,1)
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

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImage(ImageView bmImage){
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                fbProf = mIcon11;
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;//bitmap
        }

        protected void onPostExecute(Bitmap result){
            //RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),result);
            //roundedBitmapDrawable.setCircular(true);
            bmImage.setImageBitmap(result);
            mProgress.dismiss();
        }

    }
}
