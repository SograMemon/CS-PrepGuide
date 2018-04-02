package ca.dal.cs.web.cs_prepguide;

/**
 * Created by bhanu on 4/1/2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




import java.util.zip.Inflater;



public class FirstFragment extends Fragment {
    private TextView mValueView;

    PostSingleTon PostSingletonInstance;

    public FirstFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PostSingletonInstance = PostSingleTon.getInstance(getContext());



    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        mValueView = view.findViewById(R.id.textView);


        String postdetails = PostSingletonInstance.getPost( ).getPostContent( );
        mValueView.setText(postdetails);
                return view;
    }


}

