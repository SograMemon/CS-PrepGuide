# CS-PrepGuide

In this project we have implemented an app that provides users with a platform where they can explore the different jobs available in the field of Computer Science. For each job listed on this application the required skills and the job preparation guides are posted. Our app also allows user to filter jobs based on variables such as: office location, company, stream of computer science etc. This valuable information in the form of job preparation guides can help users plan their career path. Once the users have made this decision the users can
browse through job preparation guides that help the user achieve the skills they require to qualify for the best jobs at companies ranging from the top companies to smaller passionate companies.
This will be useful to fresh Computer Science graduates as well as professionals considering a career change to join the booming IT industries.
Key features included are: login/logout using social media, fingerprint login, filtering of the jobs, setting up the user skill profile, posts and comments.
For a more detailed description of this application please see the screen shots section of this document.

#### Team Members
 - Faraaz Nizar Dhuka  (B00784039)
 - Sogra Bilal Memon   (B00786252)
 - Udaya Bhanu Lekhala      (B00775670)
 - Vamshi Krishna Moogala     (B00785801)


## Installation Notes
This project zip file can be downloaded and unzipped after which it can be opened in Android studio to be able to test and run the application.

## Libraries
##### Picasso:  Picasso allows for hassle-free image loading in your application—often in one line of code! [here](http://square.github.io/picasso/)
##### hdodenhof/CircleImageView: A fast circular ImageView based on RoundedImageView from Vince Mi which itself is based on techniques recommended by Romain Guy.[here](https://github.com/hdodenhof/CircleImageView)

##### Facebook SDK:It is a component of the Facebook SDK for Android. To use the Facebook Login SDK, make it a dependency in Maven, or download it.[here](https://developers.facebook.com/docs/facebook-login/android)

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
- Fingure print Sign-in
  User can use fingure print to Sign-in
- Social Media Sign-in
  User can use facebook or google account to Sign-in. User does not need to Sign-up for this app in this case.
- Filters Page
  To filter areas in Computer Science, Companies and their Job Positions available. Jobs can be filltered based on the number of required job skills that the user has as well as by office locations that are closest to the user
- Guides Page
  User can choose a guide where there is a post, links and comments section.
- Logout
  User can log out from the app
- Profile Page
  Lets user to make changes in their profile like adding their skills, photo, name, email etc.
- Help Page
  Provides user information on how to use the app



## Final Project Status
We have completed the development of the application with all functionalities working properly in the app. For now, we have implemented login, user profile, filtering jobs, and job guide view and navigation between various screens. The future of this application would be to use dynamic data that would be populated by companies who want to post their job preparation guides. More functionalities would have to be added to allow these companies to post and edit their job guides. The breakdown of the functionalities we aimed to achieved and their status is given below.

#### Minimum Functionality
 - Sign up/Sign in  (Completed)
 - Profile Page     (Completed)
 - Guides Page      (Completed)
 - Filters Page     (Completed)
 - Help Page        (Completed)

#### Expected Functionality
 - Job Suggestion (Completed)
 - Save/bookmark Guide (Completed)
 - Login: social media integration (Completed)
 - Edit/delete comments (Completed)
 - Upload Profile photo (Completed)
 - Reset password (Completed)

#### Bonus Functionality
 - Login using fingerprint sensor (Completed)
 - Listing jobs based on nearby locations (Completed)
 - Email the people who bookmarked for a job (Not implemented)

## Firebase Data Model
##### Model
![Image of Model](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2FDbStruct.PNG?alt=media&token=822ba35e-fd2d-48ac-b967-5353ea6f25fc)

##### Post Model
![Image of Post Model](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2FpostModel.PNG?alt=media&token=4bd3e80f-bf7c-4a2d-897b-d97a4a14e152)

##### Jobs Model
![Image of Jobs Model](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2FJob%20Model.PNG?alt=media&token=07b9ea27-b085-4b5c-928b-a5c1342fca21)

##### Users Model
![Image of Users Model](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2FUserModelCapture.PNG?alt=media&token=6c2276f6-a96d-40fc-a7ad-f91db86908b3)

## Credits
- [Moksh Mohindra](https://www.linkedin.com/in/moksh-mohindra-331b70144) - Our classmate for designing us the application icon

## Screenshots

![Image of Login Screen](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2FLogin.PNG?alt=media&token=7ef128a6-b27d-427d-ab44-90fd3b5b2a05)

##### Login Screen: allows users to login though facebook, google, CS-prepGuide account or by using their fingure print.

![Image of Login Screen](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2Fl2.PNG?alt=media&token=923d6170-2d3c-46cf-a024-a15e2104744f)

##### Job Filter Page: allows users to select job filtering either by location, skills or general. The option selection is done on a pop up dialogInterface. If location is selected jobs are listed based on the distance of the office from the user. Listing the closet locations first. Similarly if filtering by skills is selected, jobs are suggested based on the skills the user has and the number of the user's skills that match the required job skills. The user must enter his/her skills in their profile for this feature to work. If the user selects general as the filter option the values that are set in the spinners by the user are used to filter the jobs. For example if the company spinner is set to "Google" only jobs at google are displayed. 

##### Profile page: allows users to update their details such as name, email, photo, skills etc. The skills entered here are used to suggest jobs when filtering by skills is selected. The name entered here is the name that is displayed when you write a post.


![Image of Login Screen](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2Fl3.PNG?alt=media&token=62ca19d1-3b37-480a-bb8f-c3d8973e7d4c)
##### Post Page: When you click on a job in the filter Page the app redirects to the Post Page. At the moment this page is static. This is beacause of a lack of data and the scope of this project was to work with static data. In the future this can be made dynamic. This guide can also be book marked using the bookmark button. This allows users to view the guide later though the Bookmarks page

##### Comments Page: This is the page where users can comment about their experiences and their thoughts about the prepartion guide provided

![Image of Login Screen](https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/Screenshots%2Fl4.PNG?alt=media&token=fc18719d-0567-4523-bccb-e8b7162f42c9)
##### Help Page: This page provides users with information on how to use the app and how to overcome some issues

##### Bookmarks Page: displays a list of bookmarked guides. These guides can be viewed by clicking them. There is a button that can be clicked to remove a guide from the bookmarks page.


## References

##### [1] stackoverflow.com. "Image capture with camera & upload to Firebase (Uri in onActivityResult() is null)". [Online]. [Available](https://stackoverflow.com/questions/40710599/image-capture-withhttps://jbt.github.io/markdown-editor/emoji/smirk.png-camera-upload-to-firebase-uri-in-onactivityresult-is-nul).[Accessed on: 17th March 2018].

##### [2] stackoverflow.com. "ImageView in circular through xml". [Online]. [Available](https://stackoverflow.com/questions/22105775/imageview-in-circular-through-xml).[Accessed on: 30th March 2018].

##### [3] stackoverflow.com. "Image capture with camera & upload to Firebase (Uri in onActivityResult() is null)". [Online]. [Available](https://stackoverflow.com/questions/40710599/image-capture-with-camera-upload-to-firebase-uri-in-onactivityresult-is-nul).[Accessed on: 17th March 2018].

##### [4] developer.android.com. "Taking Photos". [Online]. [Available](https://developer.android.com/training/camera/photobasics.html).[Accessed on: 17th March 2018].


##### [5] EDMT. Dev. youtube.com. "Android Studio Tutorial - Fingerprint Authentication". [Online]. [Available](https://www.youtube.com/watch?v=zYA5SJgWrLk).[Accessed on: 27th March 2018].

##### [6] developer.android.com. "Fingerprint Authentication". [Online]. [Available](https://developer.android.com/about/versions/marshmallow/android-6.0.html).[Accessed on: 27th March 2018].

##### [7] developer.android.com. "Request App Permissions". [Online]. [Available](https://developer.android.com/training/permissions/requesting.html).[Accessed on: 27th March 2018].

##### [8] stackoverflow.com. "how to create custom spinner like border around the spinner with down triangle on the right side?". [Online]. [Available](https://stackoverflow.com/questions/17231683/how-to-create-custom-spinner-like-border-around-the-spinner-with-down-triangle-o).[Accessed on: 6th April 2018].

##### [9] developer.android.com. "Request App Permissions". [Online]. [Available](http://square.github.io/picasso/).[Accessed on: 17th March 2018].

##### [10] Abhiandroid.com. (2018). TabLayout Tutorial With Example In Android Studio. [online]. [Available](http://abhiandroid.com/materialdesign/tablayout-example-android-studio.html) [Accessed 8 Apr. 2018].

##### [11] textview, s. (2018). setMovementMethod doesn't work in android textview. [online] Stackoverflow.com. [Available](https://stackoverflow.com/questions/37429656/setmovementmethod-doesnt-work-in-android-textview) [Accessed 8 Apr. 2018]

##### [12] YouTube. (2018). Android Studio Tutorial - Firebase Retrieve Realtime Data - Part 7. [online]. [Available](https://www.youtube.com/watch?v=KEp5RAZNMng) [Accessed 8 Apr. 2018].

##### [13] "Implementing back button in android fragment activity", Stackoverflow.com, 2018. [Online]. [Available](https://stackoverflow.com/questions/25153364/implementing-back-button-in-android-fragment-activity). [Accessed: 08- Apr- 2018].

##### [14] "How to change text of a TextView in navigation drawer header?", Stackoverflow.com, 2018. [Online]. [Available](https://stackoverflow.com/questions/34973456/how-to-change-text-of-a-textview-in-navigation-drawer-header). [Accessed: 08- Apr- 2018].

##### [15] "Remove Android App Title Bar", Stackoverflow.com, 2018. [Online]. [Available](https://stackoverflow.com/questions/14475109/remove-android-app-title-bar). [Accessed: 08- Apr- 2018].

##### [16] "Get listview item position on button click", Stackoverflow.com, 2018. [Online]. [Available](https://stackoverflow.com/questions/20541821/get-listview-item-position-on-button-click). [Accessed: 08- Apr- 2018].

##### [17] "'Metro UI Icon Set' by Dakirby309", Iconfinder, 2018. [Online]. [Available](https://www.iconfinder.com/icons/98785/logo_microsoft_new_icon#size=128). [Accessed: 08- Apr- 2018].

##### [20] “Android: Create spinner programmatically from array - Stack Overflow.” [Online]. Available: https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array/12880688?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa. [Accessed: 08-Apr-2018].

##### [21]	“Firebase Realtime Database Tutorial for Android - YouTube - YouTube.” [Online]. Available: https://www.youtube.com/playlist?list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1. [Accessed: 08-Apr-2018].

Logo Images from:

##### [22] “Facebook, square icon | Icon search engine.” [Online]. Available: https://www.iconfinder.com/icons/107117/facebook_square_icon#size=256. [Accessed: 08-Apr-2018].

##### [23] “Logo, microsoft, new icon | Icon search engine.” [Online]. Available: https://www.iconfinder.com/icons/98785/logo_microsoft_new_icon#size=128. [Accessed: 08-Apr-2018].

##### [24] “Chat, communication, google icon | Icon search engine.” [Online]. Available: https://www.iconfinder.com/icons/1775164/chat_communication_google_icon#size=256. [Accessed: 08-Apr-2018].


