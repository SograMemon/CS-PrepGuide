package ca.dal.cs.web.cs_prepguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link filterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link filterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class filterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String STREAM="STREAM";
    public static final String COMPANY="COMPANY";
    public static final String TYPE="TYPE";

    Activity parent = null;

    private Button btnApplyFilter;
    private Spinner spinnerCompany, spinnerType, spinnerStream;
    private ListView listView_jobs;
    private EditText editText_addJob;

    public DatabaseReference databaseJobs= FirebaseDatabase.getInstance().getReference("jobs");
//    ListView jobList;

//    //to store skills in arraylist variable
//    ArrayList<String> jobList = new ArrayList<String>();
//
//    //setting up the listview
//    ArrayAdapter<String> jobListAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public  filterFragment() {
    }


//        SuppressLint("ValidFragment")
    @SuppressLint("ValidFragment")
    public filterFragment(NavigationActivityCS parent) {
        // Required empty public constructor
        this.parent = parent;
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

        return inflater.inflate(R.layout.fragment_filter, container, false);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println(getView());
        final String jobsList[]= { "google", "Apple", "Android"};

        //listView_jobs= (ListView) parent.findViewById(R.id.list_job);
        jobList =new ArrayList<>();

        // Inflate the layout for this fragment
        btnApplyFilter =  parent.findViewById(R.id.btnApplyFilter);
        spinnerStream= parent.findViewById(R.id.spinner_stream);
        spinnerCompany= parent.findViewById(R.id.spinner_company);
        spinnerType= parent.findViewById(R.id.spinner_type);
        listView_jobs=(ListView)parent.findViewById(R.id.ListView_jobs);
        editText_addJob=(EditText)parent.findViewById(R.id.edittext_addJob);
//        jobList = parent.findViewById(R.id.jobList);


        ArrayAdapter<CharSequence> streamAdapter= ArrayAdapter.createFromResource(getActivity(),
                R.array.stream_array, R.layout.spinner_dropdown_item);
        spinnerStream.setAdapter(streamAdapter);

        ArrayAdapter<CharSequence> companyAdapter= ArrayAdapter.createFromResource(getActivity(),
                R.array.company_array, R.layout.spinner_dropdown_item);
        spinnerCompany.setAdapter(companyAdapter);

        ArrayAdapter<CharSequence> typeAdapter= ArrayAdapter.createFromResource(getActivity(),
                R.array.type_array, R.layout.spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        //ArrayAdapter<CharSequence> jobAdapter= ArrayAdapter.createFromResource(getActivity(),
            //    R.array.jobList, android.R.layout.simple_expandable_list_item_1);
        //listView_jobs.setAdapter(jobAdapter);



        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJob();

            }
        });

        databaseJobs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                jobList.clear();


                for(DataSnapshot jobSnapshot: dataSnapshot.getChildren()){
                    job job1 = jobSnapshot.getValue(job.class);

                    jobList.add(job1);
                }

                jobList adapter = new jobList(parent, jobList);
                listView_jobs.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addJob(){
        String title_job= editText_addJob.getText().toString().trim();
        String jobStream= spinnerStream.getSelectedItem().toString();
        String company=spinnerCompany.getSelectedItem().toString();
        String type=spinnerType.getSelectedItem().toString();

        if(!TextUtils.isEmpty(title_job)){
            String id= databaseJobs.push().getKey();
             job jobInstance= new job(id,title_job,jobStream, company, type);

             databaseJobs.child(id).setValue(jobInstance);
            Toast.makeText(getApplicationContext(), "Job added",
                    Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(), "Enter a job",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getJob(){
        String jobStream= spinnerStream.getSelectedItem().toString();
        String company=spinnerCompany.getSelectedItem().toString();
        String type=spinnerType.getSelectedItem().toString();


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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
