package ca.dal.cs.web.cs_prepguide;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-04-01.
 */

/**
 * SingleTon class to manage details about the guide that is showed to the user
 */
public class PostSingleTon {
    private static Context mCtx;
    private static PostSingleTon mInstance;

    // Firebase Database Reference
    private DatabaseReference mDatabase;
    private static final String TAG = "PostSingleTon";

    private Post post;

    /**
     * Constructor
     */
    public PostSingleTon(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized PostSingleTon getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PostSingleTon(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Getters and Setters to get details about the current post
     */
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ArrayList<Comment> getComments() {
        if (post.getComments() != null) {
            return post.getComments();
        } else {
            // returning empty list as it can be null and lead to crash
            // when setting items in a listview
            return new ArrayList<Comment>();
        }

    }

    /**
     * Method to upload comments about a post to firebase when users add/modify/delete comments
     */
    public void addCommentsToFireBaseDB() {
        if (post.getComments() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Posts");
            mDatabase.child(post.getPostId()).child("postComments").setValue(post.getComments());
        }
    }

}
