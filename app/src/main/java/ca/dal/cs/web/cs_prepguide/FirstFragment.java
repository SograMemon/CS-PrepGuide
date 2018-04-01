package ca.dal.cs.web.cs_prepguide;

/**
 * Created by bhanu on 4/1/2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;

import java.util.zip.Inflater;

public class FirstFragment extends Fragment {
    private TextView mValueView;
//    private Firebase mRef;
    // private Firebase nRef;

    public FirstFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_first);
//        mValueView = mValueView.findViewById(R.id.textView);

//        mRef = new Firebase("https://tablayout-44c34.firebaseio.com/Bhanu");
//        //nRef = new Firebase("https://myblog-c30a2.firebaseio.com/Vamsi/Comments/Comment");
//
//
//        mRef.addValueEventListener(new ValueEventListener( ) {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String value = dataSnapshot.getValue(String.class);
//                mValueView.setText(value);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


}

