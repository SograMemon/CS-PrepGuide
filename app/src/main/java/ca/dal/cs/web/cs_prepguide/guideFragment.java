package ca.dal.cs.web.cs_prepguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link guideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link guideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class guideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "GuideFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Activity parent = null;
    FragmentManager fmg = null;
    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    public ProgressDialog mProgress;


    private DatabaseReference mDatabase, myRef1;



    private OnFragmentInteractionListener mListener;

    public guideFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public guideFragment(Activity parent, FragmentManager fmg) {
        // Required empty public constructor
        this.parent = parent;
        this.fmg = fmg;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment guideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static guideFragment newInstance(String param1, String param2) {
        guideFragment fragment = new guideFragment();
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
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        mProgress.show();
        getPostDetailsFromFirebase("post1");

        simpleFrameLayout = (FrameLayout) view.findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) view.findViewById(R.id.simpleTabLayout);

        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("First");
        tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Second");
        tabLayout.addTab(secondTab);
//        TabLayout.Tab thirdTab = tabLayout.newTab();
//        thirdTab.setText("Third");
//        tabLayout.addTab(thirdTab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new FirstFragment();
                        break;
                    case 1:
                        fragment = new SecondFragment();
                        break;
                }
                FragmentManager fm = fmg;
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void onStart(){
        super.onStart();
//        System.out.println(getView());
//        ViewPager viewPager =  parent.findViewById(R.id.pager);
//        NavigationActivityCS.ViewPagerAdapter adapter = new NavigationActivityCS.ViewPagerAdapter(fmg);
//
//
//        adapter.addFragment(new NavigationActivityCS.FragmentOne(), "SKILLS");
//        adapter.addFragment(new NavigationActivityCS.FragmentTwo(), "PREPARATION");
//        adapter.addFragment(new NavigationActivityCS.FragmentThree(), "LINKS");
//        adapter.addFragment(new NavigationActivityCS.FragmentFour(), "COMMENTS");
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = parent.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
    }

    public void getPostDetailsFromFirebase(String postId){
        final PostSingleTon postSingleToninstance = PostSingleTon.getInstance(getContext());

        String currentPostId = "Posts/".concat(postId);
        Log.d(TAG, "reference" + currentPostId);

        final Post[] currentPost = new Post[1];

        myRef1 = FirebaseDatabase.getInstance().getReference(currentPostId);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currentPost[0] = dataSnapshot.getValue(Post.class);
                if (currentPost[0] != null) {
                    Log.d(TAG, "Current Post is: " + currentPost[0].toString());
                    if (currentPost[0].getComments() == null) {
                        currentPost[0].setComments(new ArrayList<Comment>());
                    }
                    postSingleToninstance.setPost(currentPost[0]);
                    Log.d(TAG, "Post Value After creating singleton instance is: " + postSingleToninstance.getPost().toString());
                }
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
//                mProgress.dismiss();
                // Failed to read value
                Toast.makeText(getContext(), "Error with Firebase database. please try later", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }
}
