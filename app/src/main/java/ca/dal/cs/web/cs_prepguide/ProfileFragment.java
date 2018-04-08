package ca.dal.cs.web.cs_prepguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 *
 * This class handles all the logic related to the Profile section in the application
 *
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.profileFragmentInterface} interface
 * to handle interaction events.
 */
public class ProfileFragment extends Fragment {
    //UI Components
    public static final String SKILLSEDIT = "SKILLSEDIT";
    EditText skillsEdit;
    Button btnProfileSend;
    Button addSkill;
    ListView lvSkillList;
    private Button btnUpload;
    private StorageReference mStorage;
    public ProgressDialog mProgress;
    private ImageView ProfilePic;
    private TextView userEmail;
    private EditText userName;
    private Button btnSave;
    Switch fingerPrintSwitch;

    //Request Codes
    private static final int CAMERA_REQUEST_CODE = 1, GALLERY_REQUEST_CODE = 2, READ_EXTERNAL_REQUEST_CODE = 4;

    //Storage variables for Camera and Gallery
    String mCurrentPhotoPath;
    Uri photoURI, uri;

    //to maintain user profile using a singleton Class
    CSPrepGuideSingleTon singleTon = CSPrepGuideSingleTon.getInstance(getApplicationContext());

    // Tag for logging
    private static final String TAG = "Profile Fragment";

    // variable to store whether edit button is clicked or not
    private boolean clicked;

    CSPrepGuideSingleTon csPrepGuideSingleTonInstance;

    //to store skills in arraylist variable
    ArrayList<String> skillsList = singleTon.getAppUser().getSkills();
    //setting up the listview
    ArrayAdapter<String> adapter;
    Intent intent;

    // Interface object used to invoke methods
    private profileFragmentInterface mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        clicked = false;

        // Reference for Firebase Database
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(getApplicationContext());
        ProfilePic = view.findViewById(R.id.ProfilePic);
        btnUpload = view.findViewById(R.id.btnUpload);
        fingerPrintSwitch = view.findViewById(R.id.fingerPrintSwitch);
        fingerPrintSwitch.setChecked(false);

        // SingleTonInstance for accessing and modifying user details
        csPrepGuideSingleTonInstance = new CSPrepGuideSingleTon(getContext());

        // Setting whether the switch for fingerprint is on based on which login provider user is using
        if (csPrepGuideSingleTonInstance.isUsingEmailAuthentication()) {
            setFingerPrintSwitchState();
        }

        //Switch provided to give access to the user to login using his fingerprint
        fingerPrintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * Fingerprint Login is available only for the users that are registered in the app
                 * (Excluding Social Media Login)
                 * If the user switches ON, only then he is allowed to access login using his fingerprint
                 */
                if (singleTon.isUsingEmailAuthentication()) {
                    MySharedPreferences cs_prepguide_preferences = new MySharedPreferences(getContext());
                    if (isChecked) {
                        cs_prepguide_preferences.setEmailUsingSharedPreference(singleTon.getUserEmailForFingerPrintAuthentication());
                        cs_prepguide_preferences.setPasswordUsingSharedPreference(singleTon.getUserPasswordForFingerPrintAuthentication());
                        cs_prepguide_preferences.setIsUsingFingerPrint(true);
                    } else {
                        //Fingerprint login is unavailable for users signed up with Facebook or Google
                        cs_prepguide_preferences.setIsUsingFingerPrint(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Fingerprint Authentication is available for registered users only",
                            Toast.LENGTH_SHORT).show();
                    fingerPrintSwitch.setChecked(false);
                }
            }
        });


        // Method handles logic for uploadimg image using camera or gallery
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //Handles the user's response in choosing between Gallery or Camera
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(getApplicationContext(), String.valueOf(which), Toast.LENGTH_SHORT).show();
                            switch (which) {
                                case -1:
                                    //This case handles Camera option
//                                    Toast.makeText(getApplicationContext(), "Camera", Toast.LENGTH_SHORT).show();
                                    try {
                                        //Getting Permissions for our app: Camera
                                        runtimePermission();
                                        dispatchTakePhotoIntent();
                                    } catch (Exception ex) {
                                        Toast.makeText(getApplicationContext(),
                                                "Camera permissions turned off",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case -2:
                                    //This case handle Gallery option
                                    //Toast.makeText(getApplicationContext(), "Gallery", Toast.LENGTH_SHORT).show();
                                    pickFromGallery();
                                    break;
                                case -3:
                                    //To cancel the dialog interface
                                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    };

                    //Dialog to pick options between Camera or Gallery
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Choose an Option!");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Camera", dialogClickListener);
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Gallery", dialogClickListener);
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", dialogClickListener);
                    alertDialog.show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            "Make sure Camera and Storage Permission is Granted in Settings for this App",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Setting the image initially if present
        if (!singleTon.getAppUser().getImageUrl().isEmpty()) {
            // PICASSO REFERENCE: http://square.github.io/picasso/
            Picasso.with(getApplicationContext())
                    .load(Uri.parse(singleTon.getAppUser().getImageUrl())).fit().centerCrop().into(ProfilePic);
        }

        // UI components
        skillsEdit = view.findViewById(R.id.skillsEdit);
        lvSkillList = view.findViewById(R.id.skillsList1);
        userEmail = view.findViewById(R.id.userEmail);
        userName = view.findViewById(R.id.userName);
        btnSave = view.findViewById(R.id.btnSave);

        String emailID = singleTon.getAppUser().getEmail();
        //Setting the retrieved Email ID of the user
        userEmail.setText(emailID);
        userName.setEnabled(false);
        userName.setText(singleTon.getAppUser().getName());


        addSkill = view.findViewById(R.id.addSkill);

        // Setting the adapter to a custom
        adapter = new SkillsAdapter(getActivity(), android.R.layout.simple_list_item_1, skillsList);


        lvSkillList.setAdapter(adapter);

        //Button to add the user's skills
        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!skillsEdit.getText().toString().isEmpty()) {
                    skillsList.add(skillsEdit.getText().toString());

                    //making the edittext field blank
                    skillsEdit.setText("");
                    singleTon.getAppUser().setSkills(skillsList);
                    singleTon.addUserToFireBaseDB();
                    Log.d(TAG, "after adding skills" + singleTon.getAppUser().getSkills().toString());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Please add a Skill", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Button to save the Username which he edits
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!clicked) {
                    singleTon.getAppUser().setName(userName.getText().toString());
                    mListener.onUserDetailsChanged();
                    userName.setEnabled(false);
                    btnSave.setText(getResources().getText(R.string.edit));
                    singleTon.addUserToFireBaseDB();
                    userName.setEnabled(true);
                    btnSave.setText(getResources().getText(R.string.save));
                    clicked = true;
                } else {

                    singleTon.getAppUser().setName(userName.getText().toString());
                    mListener.onUserDetailsChanged();
                    userName.setEnabled(true);
                    btnSave.setText(getResources().getText(R.string.save));
                    singleTon.addUserToFireBaseDB();
                    userName.setEnabled(false);
                    btnSave.setText(getResources().getText(R.string.edit));
                    clicked = false;

                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof profileFragmentInterface) {
            mListener = (profileFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface profileFragmentInterface {
        void onUserDetailsChanged();
    }

    //CAMERA UPLOAD PHOTO REFERENCE:
    // https://stackoverflow.com/questions/40710599/image-capture-with-camera-upload-to-firebase-uri-in-onactivityresult-is-nul
    //This method creates a name for the camera image that will be captured
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        //Give image file a storage directory for saving it
        File storageDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File pic = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Save a path for the file and use with ACTION_VIEW intents
        mCurrentPhotoPath = pic.getAbsolutePath();
        return pic;
    }

    //Handles image capturing of camera app
    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create the File for the location of the photo
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

                // Error occurred while creating the File...
                Toast.makeText(getApplicationContext(),
                        "Error occurred while creating the File...",
                        Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    //Method to handle Gallery image
    public void pickFromGallery() {
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }


    //This method handles the result of the image captured by the Camera or uploaded from Gallery
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checks for the request code if it is Camera or Gallery for uploading an image
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            //Shows progress while uploading an image
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            StorageReference filepath = mStorage.child("Photos").child(photoURI.getLastPathSegment());

            //Storing the uploaded image in the firebase
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Displaying the profile picture
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(ProfilePic);

                    //Saving profile picture to the firebase of the user
                    singleTon.getAppUser().setImageUrl(downloadUri.toString());
                    singleTon.addUserToFireBaseDB();
                    mListener.onUserDetailsChanged();

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "Uploading Done!! .... ", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Failure while uploading!", Toast.LENGTH_SHORT).show();
                }
            });
            /*} else {
                Log.v(TAG, "File URI is null");
            }*/
        }


        //Gallery Request to upload an image
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            //Shows progress while uploading an image
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Uploading Image...");
            mProgress.show();
            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());

            uri = data.getData();

            //Storing the uploaded image in the firebase
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(ProfilePic);

                    //Saving profile picture to the firebase of the user
                    singleTon.getAppUser().setImageUrl(downloadUri.toString());
                    singleTon.addUserToFireBaseDB();
                    mListener.onUserDetailsChanged();

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "Uploading Done!! .... ", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Failure while uploading!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //REFERENCE for Runtime Permissions: https://developer.android.com/training/permissions/requesting.html
    /**This method asks for the permission of using Camera by our app
    *App asks for permission only if it isn't provided at first
    */
     public void runtimePermission() {

        //Checks if the Camera permission is given or not
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)

            //Requests runtime Camera permission to be granted from the user
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
    }

    /**
     * This method handles App's Permission request
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] permissionResult) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (permissionResult.length > 0 && permissionResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission is Granted....Now your app can access Camera",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Permission Canceled, Now your application cannot access CAMERA.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Profile");
        if (singleTon.getAppUser().getName().isEmpty()) {
            Toast.makeText(getContext(), "Please add a name to your account", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used to display the fingerprint switch state initially based on whether it is enabled or not
     */
    private void setFingerPrintSwitchState() {
        MySharedPreferences MySharedPreferences = new MySharedPreferences(getApplicationContext());
        if (MySharedPreferences.getIsUsingFingerPrint()) {
            fingerPrintSwitch.setChecked(true);
        } else {
            fingerPrintSwitch.setChecked(false);
        }
    }
}
