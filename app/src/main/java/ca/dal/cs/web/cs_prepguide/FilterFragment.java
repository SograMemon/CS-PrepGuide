package ca.dal.cs.web.cs_prepguide;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * This class will handle all the logic related to the filter jobs screen in the app
 * <p>
 * <p>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.filterFragmentInterface} interface
 * to handle interaction events.
 */
public class FilterFragment extends Fragment {

    private Button btnApplyFilter;
    private Spinner spinnerCompany, spinnerType, spinnerStream;
    private ListView listView_jobs;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST_CODE = 8888;
    /**
     * This EditText is used by the addJob function which has been commented.
     * This EditText and function can be used in the future to widen the scope of the project
     * private EditText editText_addJob;
     * */

    //Firebase instances for job database
    public DatabaseReference databaseJobs= FirebaseDatabase.getInstance().getReference("jobs");



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private filterFragmentInterface mListener;

    /**
     * Constructor
     */
    public FilterFragment() {

    }

    //job lists to populate the listView with a list of jobs that are a result of filter applications
    private List<Job> jobList;
    private List<Job> jobList1, jobListBasedOnLocation;
    //filterType defines the filter being used 
    private String filterType="spinner";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_filter, container, false);

        System.out.println(getView());
        jobList = new ArrayList<>();
        jobList1 = new ArrayList<>();

        //Stores the jobs based on location
        jobListBasedOnLocation = new ArrayList<>();


        // UI components
        // Inflate the layout for this fragment
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter);
        spinnerStream = view.findViewById(R.id.spinner_stream);
        spinnerCompany = view.findViewById(R.id.spinner_company);
        spinnerType = view.findViewById(R.id.spinner_type);
        listView_jobs = (ListView) view.findViewById(R.id.ListView_jobs);
        //editText_addJob=(EditText)view.findViewById(R.id.edittext_addJob);
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter);
        spinnerStream = view.findViewById(R.id.spinner_stream);
        spinnerCompany = view.findViewById(R.id.spinner_company);
        spinnerType = view.findViewById(R.id.spinner_type);

        //filter Button
        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if device is connected to the internet
                if(!Utilities.isNetworkAvailable(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
                }else {

                    /** Displays DialogInterface where users can select the type of filter they would like to use */
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case -1:
                                    // Based on Location executes when filter By location selected
                                    filterType = "location";
                                    getLocation();

                                    break;
                                case -2:
                                    // Based on suggestions executes when filter by skills selected
                                    filterType = "skills";
                                    suggestJob();

                                    break;
                                case -3:
                                    // General filter

                                    filterType = "spinner";
                                    getJob();

                                    /**funtion call for a funtion that can be added in the future
                                     *  addJob();
                                     */

                                    break;
                            }
                        }
                    };
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    /** Dispaly contents of Dialog box that gives uses the option to select filter type */
                    alertDialog.setTitle("Choose an option");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "By Location", dialogClickListener);
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "By Skills", dialogClickListener);
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "General", dialogClickListener);
                    alertDialog.show();
                }

            }
        });

        /** Make ListView items clickable to redirct to the post page*/
        listView_jobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Job job1 = jobList.get(i);
                CSPrepGuideSingleTon csPrepGuideSingleTonInstance = new CSPrepGuideSingleTon(getContext());
                //send the jobId of the job sellected
                csPrepGuideSingleTonInstance.setCurrentPostId(job1.jobPostId);
                mListener.onFilterClicked(job1.jobPostId, job1.getJobId());
            }
        });

        //updates the jobList if new jobs are added to the firebase job database
        databaseJobs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**clear existing data in the jobList to avoid duplication */
                jobList.clear();
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    Job job1 = jobSnapshot.getValue(Job.class);
                    job1.setFilter(filterType);
                    jobList.add(job1);


                }
                // Display list of jobs using listView
                JobList adapter = new JobList(getActivity(), jobList);
                listView_jobs.setAdapter(adapter);
                // Initialize string arrays for each spinner stream, company and type respectively
                String[] sArry, cArry, tArry;

                //call function to get the dynamic values of the stream spinner
                sArry = setStreamSpinners();
                //convert String array to ArrayList
                final List<String> streamList = new ArrayList<>(Arrays.asList(sArry));
                //set stream spinner with dynamic ArrayList
                final ArrayAdapter<String> stringArrayAdapter;
                stringArrayAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_dropdown_item, streamList);
                stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerStream.setAdapter(stringArrayAdapter);

                //call function to get the dynammic values of the company spinner
                cArry = setCompanySpinners();
                //convert String array to ArrayList
                final List<String> companyList = new ArrayList<>(Arrays.asList(cArry));
                //set company spinner with dynamic ArrayList
                final ArrayAdapter<String> stringArrayAdapterC;
                stringArrayAdapterC = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_dropdown_item, companyList);
                stringArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerCompany.setAdapter(stringArrayAdapterC);

                //call function to get the dynammic values of the type spinner
                tArry = setTypeSpinners();
                //convert String array to ArrayList
                final List<String> typeList = new ArrayList<>(Arrays.asList(tArry));
                //set type spinner with dynamic ArrayList
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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.d("test", String.valueOf(location.getLatitude()) + String.valueOf(location.getLongitude()));
                                filterJobsBasedOnLocation(location);
                            } else {
                                Toast.makeText(getApplicationContext(), "Could not get your location, please try later", Toast.LENGTH_LONG).show();
                            }
                        }

                    });

        }

    }

    private void filterJobsBasedOnLocation(Location location) {
        ArrayList<Float> temp = new ArrayList();
        jobListBasedOnLocation.clear();

        for (int i = 0; i < jobList.size(); i++) {
            Log.d(TAG, jobList.get(i).toString());
            Job currentJob = jobList.get(i);

            Location currentJobLocation = new Location("currentJobLocation");
            if (currentJob.getJobLatitude() != null && currentJob.getJobLongitude() != null && currentJob.jobId != null) {
                currentJobLocation.setLongitude(currentJob.getJobLatitude());
                currentJobLocation.setLatitude(currentJob.getJobLongitude());

                float distance = location.distanceTo(currentJobLocation);
                distance = distance / 1000;
                distance = Math.round(distance);
                currentJob.setDistance(distance);
                currentJob.setFilter(filterType);
                jobListBasedOnLocation.add(currentJob);

            } else {
                currentJob.setDistance(Float.MAX_VALUE);
                currentJob.setFilter(filterType);
                jobListBasedOnLocation.add(currentJob);
            }
        }

        Collections.sort(jobListBasedOnLocation, new Comparator<Job>() {
            @Override
            public int compare(Job o1, Job o2) {
                return Float.compare(o1.getDistance(), o2.getDistance());
            }
        });

        Log.d(TAG, jobListBasedOnLocation.toString());

        for (int i = 0; i < jobListBasedOnLocation.size(); i++) {
            Job tempJob = jobListBasedOnLocation.get(i);
            if (tempJob == null) {
                jobListBasedOnLocation.remove(i);
            }
        }


        JobList adapter = new JobList(getActivity(), jobListBasedOnLocation);
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
        getActivity().setTitle("Filter Jobs");
    }
/**
 * This function is used with an editText to add new jobs to the firebase database.
 * This function can be activated in the future when the app provides companies a platfor to upload new jobs
 * This funtion uses the values set on the spinners to set the attributes of the job being added
 *
 * private void addJob(){
 *      String title_job= editText_addJob.getText().toString().trim();
 *      String jobStream= spinnerStream.getSelectedItem().toString();
 *      String company=spinnerCompany.getSelectedItem().toString();
 *      String type=spinnerType.getSelectedItem().toString();
 *
 *      if(!TextUtils.isEmpty(title_job)){
 *          String id= databaseJobs.push().getKey();
 *          job jobInstance= new job(id,title_job,jobStream, company, type);
 *          databaseJobs.child(id).setValue(jobInstance);
 *          Toast.makeText(getApplicationContext(), "Job added",
 *          Toast.LENGTH_SHORT).show();
 *          }else{
 *          Toast.makeText(getApplicationContext(), "Enter a job",
 *          Toast.LENGTH_SHORT).show();
 *          }
 *        }
 *
 */


    private void getJob() {

        String jobStream = spinnerStream.getSelectedItem().toString();
        String company = spinnerCompany.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();

        jobList1.clear();
        for (int i = 0; i < jobList.size() - 1; i++) {
            Job job1 = jobList.get(i);
            //int l = job1.jobSkills.length;
            if ((jobStream.equalsIgnoreCase("all") && company.equalsIgnoreCase("all")) && type.equalsIgnoreCase("all")) {
                job1.setFilter(filterType);
                jobList1.add(job1);
            } else if ((!jobStream.equalsIgnoreCase("all") && company.equalsIgnoreCase("all")) && type.equalsIgnoreCase("all")) {
                job1.setFilter(filterType);
                if (job1.jobStream.equalsIgnoreCase(jobStream)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((jobStream.equalsIgnoreCase("all") && !company.equalsIgnoreCase("all")) && type.equalsIgnoreCase("all")) {
                if (job1.jobCompany.equalsIgnoreCase(company)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((jobStream.equalsIgnoreCase("all") && company.equalsIgnoreCase("all")) && !type.equalsIgnoreCase("all")) {
                if (job1.jobType.equalsIgnoreCase(type)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((!jobStream.equalsIgnoreCase("all") && !company.equalsIgnoreCase("all")) && type.equalsIgnoreCase("all")) {
                if (job1.jobStream.equalsIgnoreCase(jobStream) && job1.jobCompany.equalsIgnoreCase(company)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((jobStream.equalsIgnoreCase("all") && !company.equalsIgnoreCase("all")) && !type.equalsIgnoreCase("all")) {
                if (job1.jobType.equalsIgnoreCase(type) && job1.jobCompany.equalsIgnoreCase(company)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((!jobStream.equalsIgnoreCase("all") && company.equalsIgnoreCase("all")) && !type.equalsIgnoreCase("all")) {
                if (job1.jobStream.equalsIgnoreCase(jobStream) && job1.jobType.equalsIgnoreCase(type)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            } else if ((!jobStream.equalsIgnoreCase("all") && !company.equalsIgnoreCase("all")) && !type.equalsIgnoreCase("all")) {
                if ((job1.jobStream.equalsIgnoreCase(jobStream) && job1.jobCompany.equalsIgnoreCase(company)) && job1.jobType.equalsIgnoreCase(type)) {
                    job1.setFilter(filterType);
                    jobList1.add(job1);
                }
            }


        }
        JobList adapter = new JobList(getActivity(), jobList1);
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

    /**
     * Function returns dynamic String array for the Stream Spinner
     * Only the streams for which there is a job in the database are added to the spinner
     */
    public String[] setStreamSpinners() {

        String streamIntial;
        //first option to be added in spinner
        streamIntial = "All";

        //loop iterates through all jobs in the database
        for (int i = 0; i < jobList.size() - 1; i++) {
            Job job1 = jobList.get(i);
            //if the company value of the current job is not present in the streamInitial string then add a comma and this company to the string
            if (!streamIntial.contains(job1.jobStream)) {
                streamIntial = streamIntial + "," + job1.jobStream;
            }
        }
        String[] streamArry = null;
        //split the string by commas to get a string array that has each item that needs to be added in the spin
        streamArry = streamIntial.split(",");
        return streamArry;
    }

    /**
     * Function returns dynamic String array for the company Spinner
     * Only the companies for which there is a job in the database are added to the spinner
     */
    public String[] setCompanySpinners() {

        String companyIntial;
        //first option to be added in spinner
        companyIntial = "All";

        //loop iterates through all jobs in the database
        for (int i = 0; i < jobList.size() - 1; i++) {
            Job job1 = jobList.get(i);
            //if the company value of the current job is not present in the companyInitial string then add a comma and this company to the string
            if (!companyIntial.contains(job1.jobCompany)) {
                companyIntial = companyIntial + "," + job1.jobCompany;
            }
        }
        String[] Arry = null;
        //split the string by commas to get a string array that has each item that needs to be added in the spinner
        Arry = companyIntial.split(",");
        return Arry;
    }

    /**
     * Function returns dynamic String array for the Type Spinner
     * Only the position types for which there is a job in the database are added to the spinner
     */
    public String[] setTypeSpinners() {

        String typeIntial;
        //first option to be added in spinner
        typeIntial = "All";

        //loop iterates through all jobs in the database
        for (int i = 0; i < jobList.size() - 1; i++) {
            Job job1 = jobList.get(i);
            //if the company value of the current job is not present in the typeInitial string then add a comma and this company to the string
            if (!typeIntial.contains(job1.jobType)) {
                typeIntial = typeIntial + "," + job1.jobType;
            }
        }
        String[] Arry = null;
        //split the string by commas to get a string array that has each item that needs to be added in the spinner
        Arry = typeIntial.split(",");
        return Arry;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Function generates jobList based on no of skills matches with the user
     * The job with the highest number of user skills matching the required jobSkills are displayed on the top of the list
     * Called when the user selects filter by skill in the dialogInterface
     */
    public void suggestJob() {
        CSPrepGuideSingleTon userSingleTonInstance;
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());
        //get userSkills using singleton class
        ArrayList<String> userSkill = userSingleTonInstance.getAppUser().getSkills();
        Job job1;
        String userSkillStr;
        //initialize array to store the number of matching skills for each job in the database
        int[] matchSkills = new int[jobList.size()];
        //loop through all jobs in database
        for (int i = 0; i < jobList.size(); i++) {
            job1 = jobList.get(i);
            matchSkills[i] = 0;
            //loop through all user skills in database
            for (int j = 0; j < userSkill.size(); j++) {
                userSkillStr = userSkill.get(j);
                //check if JobSkill attribute in current job is not null
                if (job1.jobSkills != null) {
                    //convert all skills to lower case for ease of comparison
                    String jobSkillLower = job1.getJobSkills().toLowerCase();
                    //jobSkillLower is a string that has all the required skills for the job separated by commas
                    //each user skill is checked if it matches a skill in the string matchSkills is incremented
                    if (jobSkillLower.contains(userSkill.get(j).toLowerCase())) {
                        matchSkills[i]++;
                    }
                }
            }
        }

        jobList1.clear();
        String orderedSuggetion = "0";
        char c;
        int index, insertIndex = 0;
        int insertVal = 0;
        for (int i = 1; i < matchSkills.length; i++) {
            insertIndex = -1;
            for (int j = 0; j < orderedSuggetion.length(); j++) {
                index = Integer.parseInt(String.valueOf(orderedSuggetion.charAt(j)));
                insertVal = i;
                if (matchSkills[i] > matchSkills[index] || matchSkills[i] == matchSkills[index]) {
                    insertIndex = j;
                } else if (matchSkills[i] != 0) {
                    insertIndex = j + 1;
                }

            }
            if (insertIndex != -1) {
                orderedSuggetion = orderedSuggetion.substring(0, insertIndex) + insertVal + orderedSuggetion.substring(insertIndex, orderedSuggetion.length());
            }
        }

        for (int i = 0; i < orderedSuggetion.length(); i++) {
            index = Integer.parseInt(String.valueOf(orderedSuggetion.charAt(i)));
            Job job2 = jobList.get(index);
            job2.setMatchSkillNo(" " + matchSkills[index]);
            job2.setFilter(filterType);
            jobList1.add(job2);
        }

        JobList adapter = new JobList(getActivity(), jobList1);
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
