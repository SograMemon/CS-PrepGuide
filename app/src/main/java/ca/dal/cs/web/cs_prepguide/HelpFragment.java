package ca.dal.cs.web.cs_prepguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;


/**
 * Help Fragment which shows the help section in the App
 */
public class HelpFragment extends Fragment {

    private TextView introApp;


    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Help");
    }
}
