package ca.dal.cs.web.cs_prepguide;

/**
 * Created by faraa on 2018-04-01.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by faraa on 2018-03-31.
 */

//FINGERPRINT REFERENCES:
//        1. https://www.youtube.com/watch?v=zYA5SJgWrLk
//        2.https://developer.android.com/about/versions/marshmallow/android-6.0.html


class FingerprintAuthenticationHandler extends FingerprintManager.AuthenticationCallback{

    private Context context;

    FingerprintAuthenticationHandler(Context context) {
        this.context = context;
    }

    //Check for Authentication of the user
    void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cenCancellationSignal = new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                !=PackageManager.PERMISSION_GRANTED)

            return;

        fingerprintManager.authenticate(cryptoObject,cenCancellationSignal,0,this,null);

    }
    //Handles Successful Authentication of the user
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result){

        super.onAuthenticationSucceeded(result);

        Log.d("FingerPrint", "success");
    }


    //Handles Failed Authentication of the user
    @Override
    public void onAuthenticationFailed(){
        super.onAuthenticationFailed();
        //Toast.makeText(context, "Fingerprint Authentication failed!", Toast.LENGTH_SHORT).show();
        Log.d("FingerPrint", "failure");
    }

}

