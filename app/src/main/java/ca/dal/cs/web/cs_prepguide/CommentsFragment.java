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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
/**
 *
 *  This class handles all the logic related to the Comments section in the guide view
 *
 */
public class CommentsFragment extends Fragment {

    //UI Components
    ListView lvComments;
    ImageButton btnComment;
    EditText edtTxtForComment;
    CommentsAdapter commentsAdapter;

    //Singleton instance for storing post details
    PostSingleTon postSingleTonInstance;

    //Singleton instance for storing user details
    CSPrepGuideSingleTon userSingleTonInstance;

    public CommentsFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postSingleTonInstance = PostSingleTon.getInstance(getContext());
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_comments
        View view =  inflater.inflate(R.layout.fragment_comments, container, false);
        lvComments = view.findViewById(R.id.lvComments);

        //https://stackoverflow.com/questions/8162457/transparent-divider-in-a-listview
        lvComments.setDivider(this.getResources().getDrawable(R.drawable.transparent_color));
        lvComments.setDividerHeight(10);
        edtTxtForComment = view.findViewById(R.id.edtTxtForComment);
        btnComment = view.findViewById(R.id.btnComment);

        // Listener to handle adding new comments
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtTxtForComment.getText().toString();
                if(!text.isEmpty()){
                    String userName = userSingleTonInstance.getAppUser().getName();
                    if(userName.isEmpty()){
                        userName = userSingleTonInstance.getAppUser().getEmail();
                    }
                    ArrayList<Comment> tempCommentsList = new ArrayList<>();
                    tempCommentsList = postSingleTonInstance.getPost().getComments();
                    tempCommentsList.add(new Comment(text, userSingleTonInstance.getAppUser().getId(), userName, postSingleTonInstance.getPost().getPostId()));

                    //pushing the additions to firebase
                    postSingleTonInstance.getPost().setComments(tempCommentsList);
                    postSingleTonInstance.addCommentsToFireBaseDB();
                    edtTxtForComment.setText("");

                    //refreshing the list view
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        });

        commentsAdapter = new CommentsAdapter(getActivity(), R.layout.comments_layout_others, postSingleTonInstance.getPost().getComments());
        lvComments.setAdapter(commentsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Guide View");
    }
}