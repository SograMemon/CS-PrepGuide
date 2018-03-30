package ca.dal.cs.web.cs_prepguide;
import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-23.
 */

public class CSPrepGuideSingleTon {
    private static Context mCtx;
    private static CSPrepGuideSingleTon mInstance;
    private User AppUser;

    private DatabaseReference mDatabase;

    public CSPrepGuideSingleTon(Context context){
        mCtx = context.getApplicationContext();
    }

    public static synchronized CSPrepGuideSingleTon getInstance(Context context){
        if(mInstance == null){
            mInstance = new CSPrepGuideSingleTon(context.getApplicationContext());
        }
        return mInstance;
    }

    public void createUser(String name, String id, String email, String imageUrl, ArrayList<String> skills, ArrayList<String> bookmarks){
        AppUser = new User(name, id, email, imageUrl, skills, bookmarks);
    }

    public void createUser(User user){
        AppUser = user;
    }
    
    public User getAppUser() {
        return AppUser;
    }

    public void addUserToFireBaseDB(){
        if (AppUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(AppUser.getId()).setValue(AppUser);
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("users");
//            myRef.push(singleTonInstance.getAppUser());
//            mDatabase.child("users").child("0").setValue(singleTonInstance.getAppUser());


//            mDatabase.child("message").setValue("Hello");
//            Log.d(TAG, "ADDED USER");

            // Write a message to the database
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("message");
//
//            myRef.setValue("Hello, World!");
        }
    }

}
