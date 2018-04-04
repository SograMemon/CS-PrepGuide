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

    private String currentPostId = "post1";

    public String getTempUser() {
        return tempUser;
    }

    public void setTempUser(String tempUser) {
        this.tempUser = tempUser;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    private String tempUser = "";
    private String tempPassword = "";

    private boolean isUsingEmailAuthentication = false;

    private DatabaseReference mDatabase;

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

    public User getAppUser() {
        return AppUser;
    }

    public boolean isUsingEmailAuthentication() {
        return isUsingEmailAuthentication;
    }

    public void setUsingEmailAuthentication(boolean usingEmailAuthentication) {
        isUsingEmailAuthentication = usingEmailAuthentication;
    }

    public void addUserToFireBaseDB() {
        if (AppUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(AppUser.getId()).setValue(AppUser);
        }
    }

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }

}
