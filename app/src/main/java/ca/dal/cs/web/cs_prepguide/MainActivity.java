package ca.dal.cs.web.cs_prepguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_FROM_LOGIN = "Message from login";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, myRef1;

    private Button btnLogin, btnRegister, btnForgotPassword;
    private TextView txtEmail, txtPassword;
    private boolean isRegisterFirstTime = true;
    CSPrepGuideSingleTon singleTonInstance;
    public ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //https://stackoverflow.com/questions/14475109/remove-android-app-title-bar
        getSupportActionBar().hide();
        setContentView(R.layout.login_new_layout);

        singleTonInstance = CSPrepGuideSingleTon.getInstance(getApplicationContext());

        if (!Utilities.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

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
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    if (!txtEmail.getText().toString().equals("") && !txtPassword.getText().toString().equals("")) {
                        mProgress.show();
                        signIn(txtEmail.getText().toString(), txtPassword.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    if (isRegisterFirstTime) {
                        isRegisterFirstTime = false;
//                    btnLogin.setVisibility(View.INVISIBLE);
                        txtEmail.setText("");
                        txtPassword.setText("");
                        Toast.makeText(getApplicationContext(), "Enter your details and press register to create an account", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtEmail.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.show();
                            registerUser(txtEmail.getText().toString(), txtPassword.getText().toString());
                        }

                    }
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
                }else{
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
                }else{
                    String emailAddress = txtEmail.getText().toString();
                    if (emailAddress.equals("")) {
                        Toast.makeText(getApplicationContext(), "Email address can not be empty", Toast.LENGTH_SHORT).show();
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
//                                        btnLogin.setVisibility(View.VISIBLE);
                                            Toast.makeText(getApplicationContext(), R.string.forgot_password_toast_text, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please check if your email is valid. If the problem persists, Please try again later.", Toast.LENGTH_SHORT).show();
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
        if(!Utilities.isNetworkAvailable(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Please check your network connection and try again", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, currentUser.getUid());
                mProgress.show();
                getUserDetailsFromFirebase(currentUser.getUid(), "", "", "");
            }
        }
//        updateUI(currentUser);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

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
//                            updateUI(user);
                            isRegisterFirstTime = true;
                            singleTonInstance.createUser("", user.getUid(), email, "", new ArrayList<String>(), new ArrayList<String>());
                            Log.d(TAG, "user details after registering" + singleTonInstance.getAppUser().toString());
                            Toast.makeText(getApplicationContext(), "Registration Success",
                                    Toast.LENGTH_SHORT).show();
//                            btnLogin.setVisibility(View.VISIBLE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.w(TAG, user.getUid());
                            Log.w(TAG, user.toString());
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

    private void handleSignInResult(final GoogleSignInAccount acct) {

        // Signed in successfully, show authenticated UI.
//            updateUI(account);
//            Log.w(TAG, account.toString());
//            if (account != null) {
//                FirebaseUser user = mAuth.getCurrentUser();

//                String personName = account.getDisplayName();
//                String personGivenName = account.getGivenName();
//                String personFamilyName = account.getFamilyName();
//                String personEmail = account.getEmail();
//                String personId = account.getId();
//                Uri personPhoto = account.getPhotoUrl();
//                Log.w(TAG, personName + personEmail + personGivenName + personFamilyName + personId + personPhoto.toString());

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

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
                            if (user.getPhotoUrl() == null) {
                                personPhoto = "";
                            } else {
                                personPhoto = user.getPhotoUrl().toString();
                            }
                            mProgress.show();
                            getUserDetailsFromFirebase(user.getUid(), user.getEmail(), user.getDisplayName(), personPhoto);

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });


    }


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
//                            updateUI(user);
                            String personPhoto;
                            if(user.getPhotoUrl() == null){
                                personPhoto = "";
                            }else{
                                personPhoto = user.getPhotoUrl().toString();
                            }
                            mProgress.show();
                            getUserDetailsFromFirebase(user.getUid(), user.getEmail(), user.getDisplayName(), personPhoto);
//                            Log.d(TAG, "user after facebook login" + singleTonInstance.getAppUser().toString());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void getUserDetailsFromFirebase(final String userID, final String userEmail, final String name, final String imageUrl) {
        final User[] user = new User[1];
        String currentUser = "users/".concat(userID);
//        String currentUser = "users/".concat("sample");
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
                    if (user[0].getImageUrl() == null ){
                        user[0].setImageUrl("");
                    }
                    singleTonInstance.createUser(user[0]);
                    Log.d(TAG, "User Value After creating singleton instance is: " + singleTonInstance.getAppUser().toString());
                } else {
                    singleTonInstance.createUser(name, userID, userEmail, imageUrl, new ArrayList<String>(), new ArrayList<String>());
                    singleTonInstance.addUserToFireBaseDB();
                }
                mProgress.dismiss();
                navigateToApplication();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                mProgress.dismiss();
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Error with Firebase database. please try later", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }

    private void navigateToApplication() {
        Intent intentFromLogin = new Intent(getApplicationContext(), NavigationActivityCS.class);
        intentFromLogin.putExtra(MESSAGE_FROM_LOGIN, "Message Login");
        txtEmail.setText("");
        txtPassword.setText("");
        startActivity(intentFromLogin);
    }
}

