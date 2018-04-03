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
import android.widget.TextView;
import android.widget.Toast;


import java.util.zip.Inflater;



public class FirstFragment extends Fragment {
    private TextView mValueView;
    private Button btnBookmark;

    PostSingleTon PostSingletonInstance;
    CSPrepGuideSingleTon userSingleTonInstance;

    public FirstFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PostSingletonInstance = PostSingleTon.getInstance(getContext());
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        mValueView = view.findViewById(R.id.textView);
        btnBookmark = view.findViewById(R.id.btnBookmark);

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPost = PostSingletonInstance.getPost().getPostId();
                if(!userSingleTonInstance.getAppUser().getBookmarks().contains(currentPost)){
                    userSingleTonInstance.getAppUser().addBookmarksToUser(currentPost);
                    userSingleTonInstance.addUserToFireBaseDB();
                    Toast.makeText(getContext(), "Successfully added to bookmarks", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "This job is already present in your bookmarks", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String postdetails = PostSingletonInstance.getPost().getPostContent().replace("/n","\n"); ;
        mValueView.setText(postdetails);
                return view;
    }


}

