package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-04-01.
 */

public class PostSingleTon {
    private static Context mCtx;
    private static PostSingleTon mInstance;
    private DatabaseReference mDatabase;
    private static final String TAG = "PostSingleTon";

    private Post post;

    public PostSingleTon(Context context){
        mCtx = context.getApplicationContext();
    }

    public static synchronized PostSingleTon getInstance(Context context){
        if(mInstance == null){
            mInstance = new PostSingleTon(context.getApplicationContext());
        }
        return mInstance;
    }


    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ArrayList<Comment> getComments(){
        return post.getComments();
    }

    public void addCommentsToFireBaseDB(){
        if (post.getComments() != null) {
//            String currentPostReference = "Posts/".concat(post.getPostId());
//            Log.d(TAG, currentPostReference);
            mDatabase = FirebaseDatabase.getInstance().getReference("Posts");
            mDatabase.child(post.getPostId()).child("postComments").setValue(post.getComments());
        }
    }

}
