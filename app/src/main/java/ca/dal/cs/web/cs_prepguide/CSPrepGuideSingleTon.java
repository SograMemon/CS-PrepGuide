package ca.dal.cs.web.cs_prepguide;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-23.
 */

public class CSPrepGuideSingleTon {
    private static Context mCtx;
    private static CSPrepGuideSingleTon mInstance;
    private User AppUser;

    public CSPrepGuideSingleTon(Context context){
        mCtx = context.getApplicationContext();
    }

    public static synchronized CSPrepGuideSingleTon getInstance(Context context){
        if(mInstance == null){
            mInstance = new CSPrepGuideSingleTon(context.getApplicationContext());
        }
        return mInstance;
    }

    public void createUser(String name, String email, String imageUrl, ArrayList<String> skills, ArrayList<String> bookmarks){
        AppUser = new User(name, email, email, imageUrl, skills, bookmarks);
    }

    public User getAppUser() {
        return AppUser;
    }
}
