package ca.dal.cs.web.cs_prepguide;

/**
 * Created by vamshikrishnamoogala on 2018-04-02.
 */

/**
 * Interface to handle the success and failure events of fingerprint sensor
 * when user tries to login using fingerprint
 * <p>
 * Method implementation in MainActivity
 */

public interface FingerPrintCallbacks {
    /**
     * This method will handle success/Failure of response from fingerprint sensor
     */
    public void onFingerPrintResult(String result);
}
