package ca.dal.cs.web.cs_prepguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.database.ChildEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static ca.dal.cs.web.cs_prepguide.R.layout.spinner_dropdown_item;
import static ca.dal.cs.web.cs_prepguide.R.layout.spinner_item;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link filterFragment.filterFragmentInterface} interface
 * to handle interaction events.
 * Use the {@link filterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class filterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String job_Title="jobtitle";
    public static final String job_ID="jobid";
    public static final String v="";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String STREAM="STREAM";
    public static final String COMPANY="COMPANY";
    public static final String TYPE="TYPE";

//    Activity parent = null;

    private Button btnApplyFilter;
//    private ImageButton imgBtnLocationFilter;
    private Spinner spinnerCompany, spinnerType, spinnerStream;
    private ListView listView_jobs;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST_CODE = 8888;
    //private EditText editText_addJob;

    public DatabaseReference databaseJobs= FirebaseDatabase.getInstance().getReference("jobs");



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean flag=false;
    private filterFragmentInterface mListener;

    public filterFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment filterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static filterFragment newInstance(String param1, String param2) {
        filterFragment fragment = new filterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private List<job> jobList;
    private List<job> jobListSetSpinner;
    private List<job> jobList1, jobListBasedOnLocation;
    private String filterType="spinner";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_filter, container, false);
//        System.out.println(getView());

        System.out.println(getView());


        //listView_jobs= (ListView) parent.findViewById(R.id.list_job);
        jobList =new ArrayList<>();
        jobList1= new ArrayList<>();
        jobListBasedOnLocation = new ArrayList<>();


        // Inflate the layout for this fragment
        btnApplyFilter =  view.findViewById(R.id.btnApplyFilter);
        spinnerStream= view.findViewById(R.id.spinner_stream);
        spinnerCompany= view.findViewById(R.id.spinner_company);
        spinnerType= view.findViewById(R.id.spinner_type);
        listView_jobs=(ListView)view.findViewById(R.id.ListView_jobs);
        //editText_addJob=(EditText)view.findViewById(R.id.edittext_addJob);
        btnApplyFilter =  view.findViewById(R.id.btnApplyFilter);
        spinnerStream= view.findViewById(R.id.spinner_stream);
        spinnerCompany= view.findViewById(R.id.spinner_company);
        spinnerType= view.findViewById(R.id.spinner_type);
//        imgBtnLocationFilter = view.findViewById(R.id.imgBtnLocationFilter);

        String typeInitial;
        String companyInitial;

        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(getApplicationContext(), String.valueOf(which), Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case -1:
                                // Based on Location
                                filterType="location";
                                getLocation();

                                break;
                            case -2:
                                // Based on suggestions
                                filterType="skills";
                                suggestJob();

                                break;
                            case -3:
                                // General filter
                                String edt = null;
                                //edt= editText_addJob.getText().toString();
                                //if(edt.equalsIgnoreCase("")){
                                filterType="spinner";
                                getJob();

                                //}else {
                                //  addJob();
                                //}
                                break;
                        }
                    }
                };
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Title");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "By Location", dialogClickListener);
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "By Skills", dialogClickListener);
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "General", dialogClickListener);
                alertDialog.show();

            }
        });

//        imgBtnLocationFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getLocation();
//            }
//        });

        listView_jobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                job job1= jobList.get(i);
                //job1.setFilter(filterType);
                CSPrepGuideSingleTon csPrepGuideSingleTonInstance = new CSPrepGuideSingleTon(getContext());
                csPrepGuideSingleTonInstance.setCurrentPostId(job1.jobPostId);
                mListener.onFilterClicked(job1.jobPostId,job1.getJobId());
            }
        });


        databaseJobs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                jobList.clear();
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    job job1 = jobSnapshot.getValue(job.class);
                    job1.setFilter(filterType);
                    jobList.add(job1);
                    //streamIntial = streamIntial+","+job1.jobStream;

                }

                jobList adapter = new jobList(getActivity(), jobList);
                listView_jobs.setAdapter(adapter);
                String[] sArry, cArry, tArry;

                sArry=setStreamSpinners();
                final List<String> streamList= new ArrayList<>(Arrays.asList(sArry));
                final ArrayAdapter<String> stringArrayAdapter;
                stringArrayAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_dropdown_item, streamList);
                stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerStream.setAdapter(stringArrayAdapter);

                cArry=setCompanySpinners();
                final List<String> companyList= new ArrayList<>(Arrays.asList(cArry));
                final ArrayAdapter<String> stringArrayAdapterC;
                stringArrayAdapterC = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_dropdown_item, companyList);
                stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerCompany.setAdapter(stringArrayAdapterC);

                tArry=setTypeSpinners();
                final List<String> typeList= new ArrayList<>(Arrays.asList(tArry));
                final ArrayAdapter<String> stringArrayAdapterT;
                stringArrayAdapterT = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_dropdown_item, typeList);
                stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerType.setAdapter(stringArrayAdapterT);





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {

//            Location test = new Location("Test");
//            test.setLongitude(-122.0840);
//            test.setLatitude(37.4220);
//
//            Location test1 = new Location("Test1");
//            test.setLongitude(-121.0840);
//            test.setLatitude(36.4220);

//            Log.d("test location", String.valueOf(Math.round(test.distanceTo(test1))));

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.d("test", String.valueOf(location.getLatitude()) + String.valueOf(location.getLongitude()));
//                                Toast.makeText(getApplicationContext(), String.valueOf(location.getLatitude()) + "" + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                                filterJobsBasedOnLocation(location);
                            }else{
                                Toast.makeText(getApplicationContext(), "Could not get your location, please try later", Toast.LENGTH_LONG).show();
                            }
                        }

                    });

        }

    }

    private void filterJobsBasedOnLocation(Location location) {
        ArrayList<Float> temp = new ArrayList();
        jobListBasedOnLocation.clear();

        for(int i=0; i<jobList.size(); i++){
            Log.d(TAG, jobList.get(i).toString());
            job currentJob = jobList.get(i);

            Location currentJobLocation = new Location("currentJobLocation");
            if(currentJob.getJobLatitude() != null && currentJob.getJobLongitude() != null && currentJob.jobId != null){
                currentJobLocation.setLongitude(currentJob.getJobLatitude());
                currentJobLocation.setLatitude(currentJob.getJobLongitude());

                float distance =location.distanceTo(currentJobLocation);
                distance=distance/1000;
                distance=Math.round(distance);
                currentJob.setDistance(distance);
                currentJob.setFilter(filterType);
                jobListBasedOnLocation.add(currentJob);

            }else{
                currentJob.setDistance(Float.MAX_VALUE);
                currentJob.setFilter(filterType);
                jobListBasedOnLocation.add(currentJob);
            }
        }

        Collections.sort(jobListBasedOnLocation, new Comparator<job>() {
            @Override
            public int compare(job o1, job o2) {
                return Float.compare(o1.getDistance(), o2.getDistance());
            }
        });

        Log.d(TAG, jobListBasedOnLocation.toString());

        for(int i=0; i< jobListBasedOnLocation.size(); i++){
            job tempJob = jobListBasedOnLocation.get(i);
            if(tempJob == null){
                jobListBasedOnLocation.remove(i);
            }
        }


        jobList adapter = new jobList(getActivity(), jobListBasedOnLocation);
        listView_jobs.setAdapter(adapter);
    }

//    private void formLocationFilteredJobArray(job currentJob, float distance){
//        for(int i=0; i< jobListBasedOnLocation.length)
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 8888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Please give permission to access location",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    private void addJob(){
//        //String title_job= editText_addJob.getText().toString().trim();
//        String jobStream= spinnerStream.getSelectedItem().toString();
//        String company=spinnerCompany.getSelectedItem().toString();
//        String type=spinnerType.getSelectedItem().toString();
//
//       if(!TextUtils.isEmpty(title_job)){
//           String id= databaseJobs.push().getKey();
//            job jobInstance= new job(id,title_job,jobStream, company, type);
//
//           databaseJobs.child(id).setValue(jobInstance);
//            Toast.makeText(getApplicationContext(), "Job added",
//                    Toast.LENGTH_SHORT).show();
//
//        }else{
//            Toast.makeText(getApplicationContext(), "Enter a job",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }


    private void getJob() {

        String jobStream = spinnerStream.getSelectedItem().toString();
        String company = spinnerCompany.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();

        jobList1.clear();
       for(int i=0;i<jobList.size()-1;i++){
           job job1=jobList.get(i);
           //int l = job1.jobSkills.length;
           if((jobStream.equalsIgnoreCase("all")&& company.equalsIgnoreCase("all"))&& type.equalsIgnoreCase("all")){
               job1.setFilter(filterType);
               jobList1.add(job1);
           }else if((!jobStream.equalsIgnoreCase("all")&& company.equalsIgnoreCase("all"))&& type.equalsIgnoreCase("all")){
               job1.setFilter(filterType);
               if(job1.jobStream.equalsIgnoreCase(jobStream)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((jobStream.equalsIgnoreCase("all")&& !company.equalsIgnoreCase("all"))&& type.equalsIgnoreCase("all")){
               if(job1.jobCompany.equalsIgnoreCase(company)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((jobStream.equalsIgnoreCase("all")&& company.equalsIgnoreCase("all"))&& !type.equalsIgnoreCase("all")){
               if(job1.jobType.equalsIgnoreCase(type)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((!jobStream.equalsIgnoreCase("all")&& !company.equalsIgnoreCase("all"))&& type.equalsIgnoreCase("all")){
               if(job1.jobStream.equalsIgnoreCase(jobStream)&& job1.jobCompany.equalsIgnoreCase(company)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((jobStream.equalsIgnoreCase("all")&& !company.equalsIgnoreCase("all"))&& !type.equalsIgnoreCase("all")){
               if(job1.jobType.equalsIgnoreCase(type)&& job1.jobCompany.equalsIgnoreCase(company)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((!jobStream.equalsIgnoreCase("all")&& company.equalsIgnoreCase("all"))&& !type.equalsIgnoreCase("all")){
               if(job1.jobStream.equalsIgnoreCase(jobStream)&& job1.jobType.equalsIgnoreCase(type)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }else if((!jobStream.equalsIgnoreCase("all")&& !company.equalsIgnoreCase("all"))&& !type.equalsIgnoreCase("all")){
               if((job1.jobStream.equalsIgnoreCase(jobStream)&& job1.jobCompany.equalsIgnoreCase(company))&& job1.jobType.equalsIgnoreCase(type)){
                   job1.setFilter(filterType);
                   jobList1.add(job1);
               }
           }




       }
        jobList adapter = new jobList(getActivity(), jobList1);
        listView_jobs.setAdapter(adapter);



   }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof filterFragmentInterface) {
            mListener = (filterFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public String[] setStreamSpinners() {

        String streamIntial;
        streamIntial ="All";

        for (int i = 0; i < jobList.size() - 1; i++) {
            job job1 = jobList.get(i);
            if (!streamIntial.contains(job1.jobStream)) {
                streamIntial = streamIntial + "," + job1.jobStream;
            }
        }
        String[] streamArry = null;
        streamArry = streamIntial.split(",");
        return streamArry;
    }

    public String[] setCompanySpinners() {

        String companyIntial;
        companyIntial ="All";

        for (int i = 0; i < jobList.size() - 1; i++) {
            job job1 = jobList.get(i);
            if (!companyIntial.contains(job1.jobCompany)) {
                companyIntial = companyIntial + "," + job1.jobCompany;
            }
        }
        String[] Arry = null;
        Arry = companyIntial.split(",");
        return Arry;
    }
    public String[] setTypeSpinners() {

        String typeIntial;
        typeIntial ="All";

        for (int i = 0; i < jobList.size() - 1; i++) {
            job job1 = jobList.get(i);
            if (!typeIntial.contains(job1.jobType)) {
                typeIntial = typeIntial + "," + job1.jobType;
            }
        }
        String[] Arry = null;
        Arry = typeIntial.split(",");
        return Arry;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void suggestJob() {
        CSPrepGuideSingleTon userSingleTonInstance;
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());
        ArrayList<String> userSkill = userSingleTonInstance.getAppUser().getSkills();
        job job1;
        String userSkillStr;
        int[] matchSkills = new int[jobList.size()];
        for (int i = 0; i < jobList.size()-1; i++) {
            job1 = jobList.get(i);
            matchSkills[i] = 0;
            for (int j = 0; j < userSkill.size(); j++) {
                userSkillStr = userSkill.get(j);
                if (job1.jobSkills != null) {
                    String jobSkillLower=job1.getJobSkills().toLowerCase();
                    if (jobSkillLower.contains(userSkill.get(j).toLowerCase())) {
                        matchSkills[i]++;
                    }
                }
            }
        }

        jobList1.clear();
        String orderedSuggetion = "0";
        String head,tail;
        char c;
        int index, incertIndex = 0; 
        int incertVal = 0;
        for (int i = 1; i < matchSkills.length; i++) {
            incertIndex=-1;
            for (int j = 0; j < orderedSuggetion.length(); j++) {
                index = Integer.parseInt(String.valueOf(orderedSuggetion.charAt(j)));
                incertVal=i;
                if (matchSkills[i] > matchSkills[index] || matchSkills[i]==matchSkills[index]) {
                    incertIndex = j;
                } else if (matchSkills[i] != 0) {
                    incertIndex = j+1;
                }

            }
            if(incertIndex!=-1) {
                orderedSuggetion = orderedSuggetion.substring(0, incertIndex) + incertVal + orderedSuggetion.substring(incertIndex, orderedSuggetion.length());
            }
        }

        for(int i=0;i<orderedSuggetion.length();i++){
            index=Integer.parseInt(String.valueOf(orderedSuggetion.charAt(i)));
            job job2=jobList.get(index);
            job2.setMatchSkillNo(" "+matchSkills[index]);
            job2.setFilter(filterType);
            jobList1.add(job2);
        }

        jobList adapter = new jobList(getActivity(), jobList1);
        listView_jobs.setAdapter(adapter);
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
    public interface filterFragmentInterface {
        // TODO: Update argument type and name
        void onFilterClicked(String JobId, String JobTitle);
    }
}
