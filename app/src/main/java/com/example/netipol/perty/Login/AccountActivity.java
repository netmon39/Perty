package com.example.netipol.perty.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.netipol.perty.Home.MainActivity;
import com.example.netipol.perty.R;
import com.example.netipol.perty.SelectPref.GridItemView;
import com.example.netipol.perty.SelectPref.GridViewAdapter;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import static com.example.netipol.perty.Login.LoginActivity.fbUID;

public class AccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

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
    private char[] cArray;
    private GridView gridView;
    private View btnGo;
    private ArrayList<String> selectedStrings;
    private String categ_key = "";
    private String fbUID;
    private ProgressDialog mProgress;
    private static final String[] numbers = new String[]{
            "SPORTS", "EDUCATION", "RECREATION","MUSIC","ART","THEATRE","TECHNOLOGY","OUTING","CAREER"};

    private boolean createAccPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FacebookSdk.sdkInitialize(this);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        final Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        fbUID = inBundle.get("fbUID").toString();
        FBimageUri = Uri.parse(inBundle.get("imageUri").toString());
        Log.d("lgntest", name);
        Log.d("lgntest", surname);
        Log.d("lgntest", fbUID);
        Log.d("lgntest", inBundle.get("imageUri").toString());//https://graph.facebook.com/1705561496162393/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D

        db = FirebaseFirestore.getInstance();

        /*TextView nameView = (TextView) findViewById(R.id.fbName);
        nameView.setText("" + name + " " + surname);*/

        mSelectImage = findViewById(R.id.profileImage);

        mProgress = new ProgressDialog(this);

        createAccPressed = false;

        new AccountActivity.DownloadImage(mSelectImage).execute(inBundle.get("imageUri").toString());

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        //get the spinner from the xml.
        Spinner userTypeDropdown = findViewById(R.id.userType);
        //create a list of items for the spinner.
        String[] items = new String[]{"Student", "Professor", "Club", "Official"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items){
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

        userNameF = (EditText) findViewById(R.id.userNameField);
        accountD = (EditText) findViewById(R.id.userBio);
        nextToP = (Button) findViewById(R.id.nextToPref);

        getFbProf = findViewById(R.id.getFbProf);
        getFbProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AccountActivity.DownloadImage(mSelectImage).execute(inBundle.get("imageUri").toString());
            }
        });
        //getFbProf.setVisibility(View.GONE);

        /*/ Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReferenceFromUrl("https://perty-53386.firebaseio.com/Users/User_"+fbUID);*/

        //SELECT PREF

        gridView = (GridView) findViewById(R.id.grid);
        btnGo = findViewById(R.id.button);

        selectedStrings = new ArrayList<>();

        final GridViewAdapter adapterpref = new GridViewAdapter(numbers, this);
        gridView.setAdapter(adapterpref);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int selectedIndex = adapterpref.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    adapterpref.selectedPositions.remove(selectedIndex);
                    ((GridItemView) v).display(false, position);
                    Log.d("categ", "deselected pos: "+position);
                    selectedStrings.remove((String) parent.getItemAtPosition(position));
                } else {
                    adapterpref.selectedPositions.add(position);
                    Log.d("categ", "selected pos: "+position);
                    ((GridItemView) v).display(true, position);
                    selectedStrings.add((String) parent.getItemAtPosition(position));
                }
            }
        });

        nextToP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//write fields to firebase

                startNext();
                //Log.d("categnum",String.valueOf(selectedStrings.size()));
            }
        });

    }

    private void startNext() {

        if(!TextUtils.isEmpty(userNameF.getText().toString().trim()) && !TextUtils.isEmpty(accountD.getText().toString().trim()) && selectedStrings.size() > 2) {//Add parameter check!!!

            createAccPressed=true;//prevent "sign out from all" on onDestroy if account creation is successful

            userName = userNameF.getText().toString();
            accountDescription = accountD.getText().toString();

            //if(userName.length()==0 || accountDescription.length()==0 || userType.equals("x")){
              //  Toast.makeText(getApplicationContext(), "Please fill every field :D", Toast.LENGTH_SHORT).show();
                //return;
            //}

            mProgress.setMessage("Creating your Perty account ...");
            mProgress.show();

            assert selectedStrings != null;

            if (selectedStrings.size() > 2) {//3 or more
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

            //upload process
            if(resultUri==null){
                // Get the data from an ImageView as bytes
                //mSelectImage.setDrawingCacheEnabled(true);
                //mSelectImage.buildDrawingCache();
                Bitmap bitmap = fbProf;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                uploadTask = mStorage.child("Profile_Images").child(fbUID+".jpg").putBytes(data);
            }else {
                uploadTask = mStorage.child("Profile_Images").child(fbUID+".jpg").putFile(resultUri);
            }

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Create a new user with a first and last name

                    //Don't upload until select pref READY!!!!

                    Map<String, Object> user = new HashMap<>();
                    user.put("username", userName);
                    user.put("accountdesc", accountDescription);
                    user.put("usertype", userType);
                    user.put("uid", fbUID);
                    user.put("profimage", downloadUrl.toString());
                    user.put("categ_key", categ_key);
                    user.put("pp_key", "ab");

                    // Add a new document with a generated ID
                    db.collection("users")
                            .document(fbUID)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("acc", "DocumentSnapshot added");

                                    Log.d("lgn_accact", "setup complete, send to tutorial");
                                    Intent intent = new Intent(AccountActivity.this, TutorialActivity.class);
                                    //intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                                    //intent.putExtra("categ", categ_key);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("acc", "Error adding document", e);
                                }
                            });

                }
            });

            //Intent intent = new Intent(AccountActivity.this, SelectPrefActivity.class);
            //startActivity(intent);
            //finish();


        }else{
            if(selectedStrings.size() < 3){
                Toast.makeText(this, "Please select at least 3 categories.",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please complete all parameters.",Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onBackPressed() {//prevent upload of account if backpressed
        super.onBackPressed();
        Log.d("lgn","onBackpressed");
        MainActivity mainActivity = new MainActivity();
        mainActivity.signOutFromAll();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgress.dismiss();
        Log.d("lgn","onDestroy");
        if(createAccPressed==false){
            MainActivity mainActivity = new MainActivity();
            mainActivity.signOutFromAll();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lgn","onStop");
        if(createAccPressed==false){
            MainActivity mainActivity = new MainActivity();
            mainActivity.signOutFromAll();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Create Profile");
        //bar.setDisplayHomeAsUpEnabled(true);

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
        }

    }
}
