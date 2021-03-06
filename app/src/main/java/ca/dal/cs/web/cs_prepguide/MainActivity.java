package ca.dal.cs.web.cs_prepguide;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * This Activity is used for handling user login using email and social media
 * this activity also contains fingerprint login logic
 */
public class MainActivity extends AppCompatActivity implements FingerPrintCallbacks {

    // Tag for logging
    private static final String TAG = "MainActivity";

    // Tag for Google sign in result
    private static final int RC_SIGN_IN = 9001;

    // Callback for facebook login
    CallbackManager mCallbackManager;

    //Firebase instances for database and authentication
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, myRef1;

    // Declaring UI components
    private Button btnLogin, btnRegister, btnForgotPassword;
    private TextView txtEmail, txtPassword;
    private ImageButton btnFingerPrint;
    public ProgressDialog mProgress;

    // To keep track of user pressing the register button first time to show a specific toast
    private boolean isRegisterFirstTime = true;

    // Single Instance for managing user data (login, profile, bookmarks)
    CSPrepGuideSingleTon singleTonInstance;

    /**
     * FINGERPRINT REFERENCES
     * 1. https://www.youtube.com/watch?v=zYA5SJgWrLk
     * 2. https://developer.android.com/about/versions/marshmallow/android-6.0.html
     **/
    private static final String KEY_NAME = "FARAAZ";
    private Cipher cipher;
    private TextView textView;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Hiding the default title bar in login screen
        // Reference: https://stackoverflow.com/questions/14475109/remove-android-app-title-bar
        getSupportActionBar().hide();
        setContentView(R.layout.login_new_layout);

        // Single Instance for managing user data
        singleTonInstance = CSPrepGuideSingleTon.getInstance(getApplicationContext());

        // Checking for network connection
        if (!Utilities.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Please check your network connection and try again",
                    Toast.LENGTH_SHORT).show();
        }

        // initializing Firebase instance for database and authentication
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Initializing UI components
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnFingerPrint = findViewById(R.id.btnFingerPrint);

        // Text for progress loader
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");

        // using shared preferences in android to store userid, password and
        // flag about whether user is using fingerprint or not
        MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());

        // If user is using fingerprint for login listening for fingerprint sensor
        if (mySharedPreferences.getIsUsingFingerPrint()) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            //Checking whether permission is granted, else ask for permission
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.USE_FINGERPRINT)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!fingerprintManager.isHardwareDetected())
                Toast.makeText(getApplicationContext(),
                        "Fingerprint authentication permission not enabled",
                        Toast.LENGTH_SHORT).show();
            else {
                // The line below prevents the false positive inspection from Android Studio noinspection ResourceType
                if (!fingerprintManager.hasEnrolledFingerprints()) {

                    // This happens when no fingerprints are registered.
                    Toast.makeText(getApplicationContext(),
                            "Register at least one fingerprint: 'Settings=>Security->Fingerprint'",
                            Toast.LENGTH_LONG).show();

                } else {

                    if (!keyguardManager.isKeyguardSecure()) {
                        // Show a message that the user hasn't set up a fingerprint or lock screen.
                        Toast.makeText(getApplicationContext(),
                                "Secure lock screen hasn't set up.\n"
                                        + "Set up a fingerprint: 'Settings=>Security=>Fingerprint'",
                                Toast.LENGTH_LONG).show();
                        return;
                    } else
                        createKey();

                    if (initializeCipher()) {
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintAuthenticationHandler helper = new FingerprintAuthenticationHandler(MainActivity.this);
                        helper.startAuthentication(fingerprintManager, cryptoObject);
                    }
                }
            }
        }


        // Logic for showing or hiding the fingerprint image on login screen
        showHideFingerPrintButton();

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id_for_google))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getApplicationContext(),
                        "Problem with facebook login, Please try later",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(),
                        "Problem with facebook login, Please try later",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Signin using email and password
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for network connection
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            "Please check your network connection and try again",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Checking if fields are empty
                    if (!txtEmail.getText().toString().equals("") && !txtPassword.getText().toString().equals("")) {
                        mProgress.show();
                        signIn(txtEmail.getText().toString(), txtPassword.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Fields cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Registering using email and password
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for network connection
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            "Please check your network connection and try again",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (isRegisterFirstTime) {
                        isRegisterFirstTime = false;
                        txtEmail.setText("");
                        txtPassword.setText("");
                        Toast.makeText(getApplicationContext(),
                                "Enter your details and press register to create an account",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtEmail.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),
                                    "Email and Password cannot be empty",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.show();
                            registerUser(txtEmail.getText().toString(), txtPassword.getText().toString());
                        }

                    }
                }
            }
        });


        // Google sign in button logic
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for network connection
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            "Please check your network connection and try again",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });


        // Forgot password logic
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for network connection
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            "Please check your network connection and try again",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String emailAddress = txtEmail.getText().toString();

                    //Empty check
                    if (emailAddress.equals("")) {
                        Toast.makeText(getApplicationContext(), "Email address can not be empty",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress.show();
                        mAuth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mProgress.dismiss();
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");
                                            txtEmail.setText("");
                                            txtPassword.setText("");
                                            Toast.makeText(getApplicationContext(),
                                                    R.string.forgot_password_toast_text,
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Please check if your email is valid. If the problem persists, Please try again later.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // Checking for network connection
        if (!Utilities.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    "Please check your network connection and try again",
                    Toast.LENGTH_SHORT).show();
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, currentUser.getUid());
                mProgress.show();

                // To get user details from firebase if user already has data
                getUserDetailsFromFirebase(currentUser.getUid(), "", "", "");
            }
        }
    }


    /**
     * Method to register user with email and password
     */
    private void registerUser(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.w(TAG, user.toString());
                            Log.w(TAG, user.getUid());
                            isRegisterFirstTime = true;

                            // Creating an user object which contains all user data
                            singleTonInstance.createUser("",
                                    user.getUid(),
                                    email, "",
                                    new ArrayList<String>(),
                                    new ArrayList<String>());

                            Log.d(TAG, "user details after registering" + singleTonInstance.getAppUser().toString());
                            Toast.makeText(getApplicationContext(), "Registration Success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    /**
     * Method to sign in user with email and password
     */
    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            singleTonInstance.setUsingEmailAuthentication(true);
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.w(TAG, user.getUid());
                            Log.w(TAG, user.toString());
                            singleTonInstance.setUserEmailForFingerPrintAuthentication(email);
                            singleTonInstance.setUserPasswordForFingerPrintAuthentication(password);
                            getUserDetailsFromFirebase(user.getUid(), user.getEmail(), "", "");
                        } else {
                            mProgress.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    /**
     * Callback for google and facebook login after intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        // For facebook login
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Success handler for google sign in
     */
    private void handleSignInResult(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // Passing the google sign in result to firebase backend user authentication
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, user.getUid() + user.getEmail());
                            String personPhoto;

                            // If photo returned by google is null, making it to empty string
                            if (user.getPhotoUrl() == null) {
                                personPhoto = "";
                            } else {
                                personPhoto = user.getPhotoUrl().toString();
                            }
                            mProgress.show();

                            // To track whether user is using email or social media for login
                            singleTonInstance.setUsingEmailAuthentication(false);

                            // To get user details from firebase if user already has data
                            getUserDetailsFromFirebase(user.getUid(), user.getEmail(), user.getDisplayName(), personPhoto);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });


    }

    /**
     * Success handler for facebook sign in
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.w(TAG, user.toString());
                            Log.w(TAG, user.getDisplayName() + user.getEmail() + user.getUid() + user.getPhotoUrl());

                            // If photo returned by google is null, making it to empty string
                            String personPhoto;
                            if (user.getPhotoUrl() == null) {
                                personPhoto = "";
                            } else {
                                personPhoto = user.getPhotoUrl().toString();
                            }

                            // To track whether user is using email or social media for login
                            singleTonInstance.setUsingEmailAuthentication(false);
                            mProgress.show();

                            // To get user details from firebase if user already has data
                            getUserDetailsFromFirebase(user.getUid(), user.getEmail(), user.getDisplayName(), personPhoto);
                            Log.d(TAG, "user after facebook login" + singleTonInstance.getAppUser().toString());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * This method is used to get details of user from firebase using userID,
     * If firebase returns the data, User object will be created based on the data retrieved,
     * else, new User Object will be created based on the passed parameters(name, email, imageUrl)
     */
    private void getUserDetailsFromFirebase(final String userID, final String userEmail, final String name, final String imageUrl) {
        final User[] user = new User[1];
        String currentUser = "users/".concat(userID);
        Log.d(TAG, "reference" + currentUser);
        myRef1 = FirebaseDatabase.getInstance().getReference(currentUser);
        // Read from the database
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user[0] = dataSnapshot.getValue(User.class);
                if (user[0] != null) {
                    Log.d(TAG, "User Value is: " + user[0].toString());
                    if (user[0].getSkills() == null) {
                        user[0].setSkills(new ArrayList<String>());
                    }
                    if (user[0].getBookmarks() == null) {
                        user[0].setBookmarks(new ArrayList<String>());
                    }
                    if (user[0].getImageUrl() == null) {
                        user[0].setImageUrl("");
                    }
                    singleTonInstance.createUser(user[0]);
                    Log.d(TAG, "User Value After creating singleton instance is: " +
                            singleTonInstance.getAppUser().toString());
                } else {
                    singleTonInstance.createUser(
                            name,
                            userID,
                            userEmail,
                            imageUrl,
                            new ArrayList<String>(),
                            new ArrayList<String>()
                    );
                    singleTonInstance.addUserToFireBaseDB();
                }
                mProgress.dismiss();
                navigateToApplication();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                mProgress.dismiss();
                // Failed to read value
                Toast.makeText(getApplicationContext(),
                        "Error with Firebase database. please try later",
                        Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }

    /**
     * Method for Navigating the user after successful login
     */
    private void navigateToApplication() {
        Intent intentFromLogin = new Intent(getApplicationContext(), NavigationActivityCS.class);
        txtEmail.setText("");
        txtPassword.setText("");
        startActivity(intentFromLogin);
        finish();
    }

    /**
     * Method for Showing or hiding the fingerprint button in login screen based on whether user is
     * using fingerprint for login or not
     */
    private void showHideFingerPrintButton() {
        MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
        if (mySharedPreferences.getIsUsingFingerPrint()) {
            btnFingerPrint.setVisibility(View.VISIBLE);
        } else {
            btnFingerPrint.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey()} method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initializeCipher() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //throw new RuntimeException("Failed to get an instance of Cipher", e);
            Toast.makeText(getApplicationContext(), "Failed to get an instance of Cipher", Toast.LENGTH_SHORT).show();
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new RuntimeException("Initialize Cipher Failed", ex);
        }

    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.*
     * KEY_NAME the name of the key to be created
     */
    private void createKey() {

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }

        KeyGenerator mKeyGenerator = null;

        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * This Method is used for handling result from fingerprint Authentication
     * This is an implementation method of FingerPrintCallbacks Interface which will be invoked on
     * success or failure of fingerprint sensor after user places the finger
     */

    @Override
    public void onFingerPrintResult(String result) {
        Log.d(TAG, result);
        if (result.equals("Success")) {
            mProgress.show();
            MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
            signIn(mySharedPreferences.getEmailUsingSharedPreference(), mySharedPreferences.getPasswordUsingSharedPreference());
        } else {
            Toast.makeText(getApplicationContext(), "Fingerprint Authentication failed!", Toast.LENGTH_SHORT).show();
        }
    }
}

