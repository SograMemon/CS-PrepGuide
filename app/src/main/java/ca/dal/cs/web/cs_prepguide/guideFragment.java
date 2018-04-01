package ca.dal.cs.web.cs_prepguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Activity parent = null;
    FragmentManager fmg = null;

    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;



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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

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
}
