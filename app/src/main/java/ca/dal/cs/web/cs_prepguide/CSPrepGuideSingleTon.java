package ca.dal.cs.web.cs_prepguide;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-23.
 */

/**
 * SingleTon class to manage details about the user
 * also includes logic to update details about user to Firebase
 */
public class CSPrepGuideSingleTon {
    private static Context mCtx;
    private static CSPrepGuideSingleTon mInstance;

    private User AppUser;

    // Default post id
    // Used to navigate the user to this post if user navigates to post screen for the first time
    private String currentPostId = "post1";

    // Storing details for using fingerprint authentication
    // If user opts for fingerprint authentication,
    // these details will be stored on the device using shared preferences
    private String userEmailForFingerPrintAuthentication = "";
    private String userPasswordForFingerPrintAuthentication = "";

    private boolean isUsingEmailAuthentication = false;

    // Firebase database reference
    private DatabaseReference mDatabase;

    /**
     * Constructor
     */
    public CSPrepGuideSingleTon(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized CSPrepGuideSingleTon getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CSPrepGuideSingleTon(context.getApplicationContext());
        }
        return mInstance;
    }

    public void createUser(String name, String id, String email, String imageUrl, ArrayList<String> skills, ArrayList<String> bookmarks) {
        AppUser = new User(name, id, email, imageUrl, skills, bookmarks);
    }

    public void createUser(User user) {
        AppUser = user;
    }

    /**
     * Getters and Setters to get details about the current user
     */
    public User getAppUser() {
        return AppUser;
    }

    public boolean isUsingEmailAuthentication() {
        return isUsingEmailAuthentication;
    }

    public void setUsingEmailAuthentication(boolean usingEmailAuthentication) {
        isUsingEmailAuthentication = usingEmailAuthentication;
    }

    public String getUserEmailForFingerPrintAuthentication() {
        return userEmailForFingerPrintAuthentication;
    }

    public void setUserEmailForFingerPrintAuthentication(String userEmailForFingerPrintAuthentication) {
        this.userEmailForFingerPrintAuthentication = userEmailForFingerPrintAuthentication;
    }

    public String getUserPasswordForFingerPrintAuthentication() {
        return userPasswordForFingerPrintAuthentication;
    }

    public void setUserPasswordForFingerPrintAuthentication(String userPasswordForFingerPrintAuthentication) {
        this.userPasswordForFingerPrintAuthentication = userPasswordForFingerPrintAuthentication;
    }

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }


    /**
     * Method to upload user data to Firebase when changes are made
     */
    public void addUserToFireBaseDB() {
        if (AppUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(AppUser.getId()).setValue(AppUser);
        }
    }


}
