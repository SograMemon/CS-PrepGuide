package ca.dal.cs.web.cs_prepguide;

/**
 * Created by bhanu on 4/1/2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;

import android.text.method.LinkMovementMethod;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * This class handles all the logic related to the Guides section in the application
 */
public class PostFragment extends Fragment {

    //UI components
    private TextView mValueView;
    private Button btnBookmark;
    private ImageButton mLikeBtn;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabase, myRef1;
    public ProgressDialog mProgress;
    private TextView nvalueView;

    // singleton instance to handle post details
    PostSingleTon PostSingletonInstance;

    // singleton instance to handle user details
    CSPrepGuideSingleTon userSingleTonInstance;

    public PostFragment() {
        // Default empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PostSingletonInstance = PostSingleTon.getInstance(getContext());
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Guide View");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PostSingletonInstance = PostSingleTon.getInstance(getContext());
        userSingleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());

        mProgress = new ProgressDialog(getActivity());
        //  mProgress.setMessage("Loading...");
        mProgress.show();


        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mValueView = view.findViewById(R.id.textView);
        btnBookmark = view.findViewById(R.id.btnBookmark);
        nvalueView = view.findViewById(R.id.textView10);

        // Method to add current guide to the bookmarks of user
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPost = PostSingletonInstance.getPost().getPostId();
                if (!userSingleTonInstance.getAppUser().getBookmarks().contains(currentPost)) {

                    // Adding details to singleton and pushing it to Firebase
                    userSingleTonInstance.getAppUser().addBookmarksToUser(currentPost);
                    userSingleTonInstance.addUserToFireBaseDB();
                    Toast.makeText(getContext(), "Successfully added to bookmarks", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "This job is already present in your bookmarks", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Getting details of the current post from Firebase
        getPostDetailsFromFirebase(userSingleTonInstance.getCurrentPostId());
        return view;
    }


    /**
     * Getting the post details from firebase based on the postId
     */
    public void getPostDetailsFromFirebase(String postId) {
        final PostSingleTon postSingleToninstance = PostSingleTon.getInstance(getContext());

        // Forming the reference
        String currentPostId = "Posts/".concat(postId);
        Log.d(TAG, "reference" + currentPostId);

        final Post[] currentPost = new Post[1];

        myRef1 = FirebaseDatabase.getInstance().getReference(currentPostId);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentPost[0] = dataSnapshot.getValue(Post.class);
                if (currentPost[0] != null) {
                    Log.d(TAG, "Current Post is: " + currentPost[0].toString());
                    if (currentPost[0].getComments() == null) {
                        currentPost[0].setComments(new ArrayList<Comment>());
                    }

                    postSingleToninstance.setPost(currentPost[0]);
                    String postdetails = currentPost[0].getPostContent();
                    ;
                    String postlink = currentPost[0].getPostLink();

                    // Setting the retrieved details in UI
                    mValueView.setText(Html.fromHtml(postdetails));
                    nvalueView.setText(Html.fromHtml(postlink));
                    nvalueView.setMovementMethod(LinkMovementMethod.getInstance());
                    Log.d(TAG, "Post Value After creating singleton instance is: " + postSingleToninstance.getPost().toString());
                }
                mProgress.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                mProgress.dismiss();
                // Failed to read value
                Toast.makeText(getContext(), "Error with Firebase database. please try later", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }


}

