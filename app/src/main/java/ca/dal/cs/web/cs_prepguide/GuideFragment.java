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
 * Fragment that contains guide view
 */

public class GuideFragment extends Fragment {

    // Tag for logging
    private static final String TAG = "GuideFragment";

    // Using to get the fragment support manager from parent activity
    Activity parent = null;
    FragmentManager fmg = null;

    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    public ProgressDialog mProgress;

    private DatabaseReference mDatabase, myRef1;

    public GuideFragment() {
        // Required empty public constructor
    }

    // Using to get the fragment support manager from parent activity
    @SuppressLint("ValidFragment")
    public GuideFragment(Activity parent, FragmentManager fmg) {
        // Required empty public constructor
        this.parent = parent;
        this.fmg = fmg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        // mProgress.show();


        simpleFrameLayout = (FrameLayout) view.findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) view.findViewById(R.id.simpleTabLayout);

        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("Post");
        tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Comments");
        tabLayout.addTab(secondTab);

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

        // DELETE
        FragmentManager fm = fmg;
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.simpleFrameLayout, new FirstFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void onStart() {
        super.onStart();
        getActivity().setTitle("Guide View");
    }
}
