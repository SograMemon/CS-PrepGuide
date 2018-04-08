package ca.dal.cs.web.cs_prepguide;

/**
 * Created by faraa on 2018-04-01.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * FINGERPRINT REFERENCES:
 * <p>
 * 1.https://www.youtube.com/watch?v=zYA5SJgWrLk
 * 2.https://developer.android.com/about/versions/marshmallow/android-6.0.html
 */

class FingerprintAuthenticationHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    // To store the result ("Success"/"Failure")
    static private String fingerprint_result_code;

    // Interface object to invoke onFingerPrintResult method in MainActivity
    // to handle success/failure of fingerprint authentication
    private FingerPrintCallbacks mListener;

    FingerprintAuthenticationHandler(Context context) {
        this.context = context;
        if (context instanceof FingerPrintCallbacks) {
            mListener = (FingerPrintCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FingerPrintCallbacks");
        }
    }

    /**
     * Check for Authentication of the user
     */
    void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cenCancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED)

            return;

        fingerprintManager.authenticate(cryptoObject, cenCancellationSignal, 0, this, null);

    }

    /**
     * Handles Successful Authentication of the user
     */
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Log.d("FingerPrint", "success");
        fingerprint_result_code = "Success";
        mListener.onFingerPrintResult(fingerprint_result_code);
    }


    /**
     * Handles Failed Authentication of the user
     */
    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Log.d("FingerPrint", "failure");
        fingerprint_result_code = "Failure";
        mListener.onFingerPrintResult(fingerprint_result_code);
    }

}

