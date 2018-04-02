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
import android.widget.ListView;

import java.util.ArrayList;

public class SecondFragment extends Fragment {

    ListView lvComments;
    Button btnComment;
    EditText edtTxtForComment;
//    ArrayList<Comment> commentsArrayList = new ArrayList<>();
    CommentsAdapter commentsAdapter;
    PostSingleTon postSingleTonInstance;
    CSPrepGuideSingleTon userSingleTonInstance;

    public SecondFragment() {
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
        // Inflate the layout for this fragment_second
        View view =  inflater.inflate(R.layout.fragment_second, container, false);

        lvComments = view.findViewById(R.id.lvComments);
        btnComment = view.findViewById(R.id.btnComment);
        edtTxtForComment = view.findViewById(R.id.edtTxtForComment);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtTxtForComment.getText().toString();
                if(!text.isEmpty()){
                    postSingleTonInstance.getPost().getComments().add(new Comment(text, userSingleTonInstance.getAppUser().getId(), userSingleTonInstance.getAppUser().getName(), postSingleTonInstance.getPost().getPostId()));
                    edtTxtForComment.setText("");
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        });

        commentsAdapter = new CommentsAdapter(getActivity(), R.layout.comments_layout_others, postSingleTonInstance.getPost().getComments());
        lvComments.setAdapter(commentsAdapter);

        return view;
    }

}