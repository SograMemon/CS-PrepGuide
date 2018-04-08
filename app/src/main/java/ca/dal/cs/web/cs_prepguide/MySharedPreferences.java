package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vamshikrishnamoogala on 2018-04-01.
 */

/**
 * Class to manage details that are needed to be stored in device
 * <p>
 * Fingerprint logic needs userID, password, and user preference of whether to use/don't use fingerprint details
 * need to be stored on device
 */
public class MySharedPreferences {
    private SharedPreferences myData;

    /**
     * Constructor
     */
    public MySharedPreferences(Context context) {
        //This is the instance that is used to read or write data to shared preferences
        myData = context.getSharedPreferences("cs_prepguide_preferences", Context.MODE_PRIVATE);
    }

    /**
     * Getters and Setters for userId(email in our case), password and user preference to use fingerprint
     */
    public void setEmailUsingSharedPreference(String email) {
        SharedPreferences.Editor editor = myData.edit();
        editor.putString("cs_prepguide_preferences_email", email);
        editor.commit();
    }

    public void setPasswordUsingSharedPreference(String password) {
        SharedPreferences.Editor editor = myData.edit();
        editor.putString("cs_prepguide_preferences_password", password);
        editor.commit();
    }

    public String getEmailUsingSharedPreference() {
        return myData.getString("cs_prepguide_preferences_email", "no_data_present");
    }

    public String getPasswordUsingSharedPreference() {
        return myData.getString("cs_prepguide_preferences_password", "no_data_present");
    }

    public void setIsUsingFingerPrint(boolean isTrue) {
        SharedPreferences.Editor editor = myData.edit();
        editor.putBoolean("cs_prepguide_preferences_isUsingFingerPrint", isTrue);
        editor.commit();
    }

    public boolean getIsUsingFingerPrint() {
        return myData.getBoolean("cs_prepguide_preferences_isUsingFingerPrint", false);
    }


}
