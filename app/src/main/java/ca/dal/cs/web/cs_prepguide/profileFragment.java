package ca.dal.cs.web.cs_prepguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link profileFragment.profileFragmentInterface} interface
 * to handle interaction events.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String SKILLSEDIT = "SKILLSEDIT";
    EditText skillsEdit;
    Button btnProfileSend;
    Button addSkill;
    ListView lvSkillList;
    private Button btnUpload;
    private  static  final int CAMERA_REQUEST_CODE=1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private ImageView ProfilePic;
    String mCurrentPhotoPath;
    Uri photoURI;
    CSPrepGuideSingleTon singleTon = CSPrepGuideSingleTon.getInstance(getApplicationContext());
    private static final String TAG = "Profile Fragment";

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        File storageDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File pic = File.createTempFile(imageFileName,".jpg",storageDirectory);

        // Save a path for the file and use with ACTION_VIEW intents
        mCurrentPhotoPath = pic.getAbsolutePath();
        return pic;
    }

    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create the File for the location of the photo
            File photoFile=null;
            try{
                photoFile=createImageFile();
            }
            catch (IOException ex){

                // Error occurred while creating the File...
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile!=null){
                photoURI= FileProvider.getUriForFile(getApplicationContext(),"com.example.android.fileprovider",photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    //to store skills in arraylist variable
//    ArrayList<String> skillsList = new ArrayList<String>();
    ArrayList<String> skillsList = singleTon.getAppUser().getSkills();

    //setting up the listview
    ArrayAdapter<String> adapter;


    Intent intent;
//    Activity parent = null;

    private profileFragmentInterface mListener;

    public profileFragment() {
        // Required empty public constructor
    }
//    @SuppressLint("ValidFragment")
//    public profileFragment(Activity parent) {
//        // Required empty public constructor
//        this.parent = parent;
//    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(getApplicationContext());

        ProfilePic= view.findViewById(R.id.ProfilePic);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePhotoIntent();
                //Intent intent1 =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent1,CAMERA_REQUEST_CODE);

            }
        });

        if(!singleTon.getAppUser().getImageUrl().isEmpty()){
//           ProfilePic.setImageURI(Uri.parse(singleTon.getAppUser().getImageUrl()));
            // TO DO Add Reference
            Picasso.with(getApplicationContext()).load(Uri.parse(singleTon.getAppUser().getImageUrl())).fit().centerCrop().into(ProfilePic);
        }


        skillsEdit = view.findViewById(R.id.skillsEdit);
//        btnProfileSend = parent.findViewById(R.id.btnProfileSend);
        lvSkillList= view.findViewById(R.id.skillsList1);

        addSkill= view.findViewById(R.id.addSkill);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,skillsList);


        lvSkillList.setAdapter(adapter);

        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skillsList.add(skillsEdit.getText().toString());

                //making the edittext field blank
                skillsEdit.setText("");
                singleTon.getAppUser().setSkills(skillsList);
                singleTon.addUserToFireBaseDB();
                Log.d(TAG, "after adding skills" + singleTon.getAppUser().getSkills().toString());
                adapter.notifyDataSetChanged();

                //toast to make user aware of the what's going on
//                Toast.makeText(getApplicationContext(),"Skill Added Successfully",Toast.LENGTH_SHORT).show();
            }
        });

        //

//        btnProfileSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                intent = new Intent(getApplicationContext(),SkillsSelection.class);
//                intent.putExtra(SKILLSEDIT, skillsList.toString());
//                startActivity(intent);
//                Toast.makeText(getApplicationContext(),"Saving",Toast.LENGTH_SHORT).show();
//            }
//        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode ==CAMERA_REQUEST_CODE && resultCode==RESULT_OK ){

//            mProgress.setMessage("Uploading Image...");
//            mProgress.show();

            //Uri uri =  data.getData();

           /* if (uri != null) {*/
            StorageReference filepath = mStorage.child("Photos").child(photoURI.getLastPathSegment());


            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(ProfilePic);

                    singleTon.getAppUser().setImageUrl(downloadUri.toString());
                    singleTon.addUserToFireBaseDB();

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

    }


    @Override
    public void onStart(){
        super.onStart();
    }
}
