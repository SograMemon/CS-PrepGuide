# CS-PrepGuide

We aim to create a platform that provides information about the different disciplines in Computer Science and the jobs or positions available within each discipline in a particular company in which
they want to get placed. This valuable information in the form of guide provided in our app will help users to make and plan their career path. Once the users have made this decision the users can
browse through job preparation guides that help the user achieve the skills they require to qualify for the best jobs at companies ranging from the top companies to smaller passionate companies.
This will be useful to fresh Computer Science graduates as well as professionals considering a career change to join the booming IT industries.


## Installation Notes
This project zip file can be downloaded and unzipped after which it can be opened in Android studio to be able to test and run the application.

## Code Examples

**Problem 1: We used Firebase to allow users to login using email and password**

Firebase makes the process easy for login implementation, Below is a code example which authenticates the users
```java
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intentFromLogin = new Intent(getApplicationContext(), NavigationActivityCS.class);
                            intentFromLogin.putExtra(MESSAGE_FROM_LOGIN, "Message Login");
                            txtEmail.setText("");
                            txtPassword.setText("");

                            startActivity(intentFromLogin);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

```

**Problem 2: We tried using Firebase RealtimeDB to store the data**

Below is a code example which can be used to update email address to a user account using Firebase

```java
    private void addUserToFireBaseDB(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child("users").child(uid).child("email").setValue(email);
        }
    }
```

**Problem 3: We used fragments in NavigationActivity to allow users to navigate between the screens**

Below is a code example which shows the initial implementation

```java
public class NavigationActivityCS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        filterFragment.OnFragmentInteractionListener,
        guideFragment.OnFragmentInteractionListener,profileFragment.OnFragmentInteractionListener {

    FragmentManager fmg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fmg = getSupportFragmentManager();

        setContentView(R.layout.activity_navigation_cs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            displaySelectedScreen(R.id.nav_filter_jobs);
        }
    }
```

## Feature Section
List all the main features of your application with a brief description of each feature.

- Sign-up/Sign-in:
  User credentials to access the application, new user can register
- Filters Page
  To filter areas in Computer Science, Companies and their Job Positions available
- Guides Page
  User can choose a guide where there is a post, links and comments section.
- Logout
  User can log out from the app
- Profile Page
  Lets user to make changes in their profile like adding their skills etc.


## Final Project Status
We have started with the development of the application and currently working on various functionalities within the app. For now, we have implemented login, filtering jobs, and job guide view and navigation between various screens. The breakdown of the functionalities and the breakdown is given below.

#### Minimum Functionality
 - Sign up/Sign in  (Completed)
 - Profile Page     (In progress)
 - Guides Page      (In progress)
 - Filters Page     (In progress)

#### Expected Functionality
 - Job Suggestion (Not implemented)
 - Save/bookmark Guide (Not implemented)
 - Login: social media integration (Completed)
 - Edit/delete comments (Not implemented)
 - Upload Profile photo (Not implemented)
 - Reset password (Not implemented)

#### Bonus Functionality
 - Login using fingerprint sensor (Not implemented)
 - Listing jobs based on nearby locations (Not implemented)
 - Email the people who bookmarked for a job (Not implemented)

## Sources

##### [1] stackoverflow.com. "Image capture with camera & upload to Firebase (Uri in onActivityResult() is null)". [Online].[Available](https://stackoverflow.com/questions/40710599/image-capture-withhttps://jbt.github.io/markdown-editor/emoji/smirk.png-camera-upload-to-firebase-uri-in-onactivityresult-is-nul).[Accessed on: 17th March 2018].

## References

 Abhiandroid.com. (2018). TabLayout Tutorial With Example In Android Studio. [online] Available at: http://abhiandroid.com/materialdesign/tablayout-example-android-studio.html [Accessed 8 Apr. 2018].

Html.fromHtml, S. (2018). Set text in TextView using Html.fromHtml. [online] Stackoverflow.com. Available at: https://stackoverflow.com/questions/12566371/set-text-in-textview-using-html-fromhtml [Accessed 8 Apr. 2018].

 textview, s. (2018). setMovementMethod doesn't work in android textview. [online] Stackoverflow.com. Available at: https://stackoverflow.com/questions/37429656/setmovementmethod-doesnt-work-in-android-textview [Accessed 8 Apr. 2018]

 YouTube. (2018). Android Studio Tutorial - Firebase Retrieve Realtime Data - Part 7. [online] Available at: https://www.youtube.com/watch?v=KEp5RAZNMng [Accessed 8 Apr. 2018].

Logo Images from:
https://www.iconfinder.com/icons/98785/logo_microsoft_new_icon#size=128
https://www.iconfinder.com/icons/98785/logo_microsoft_new_icon#size=128
