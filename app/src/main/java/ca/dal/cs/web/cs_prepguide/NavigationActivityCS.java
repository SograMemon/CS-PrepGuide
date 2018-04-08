package ca.dal.cs.web.cs_prepguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * This Activity is used for displaying all the content of the application using fragments
 */
public class NavigationActivityCS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FilterFragment.filterFragmentInterface,
        ProfileFragment.profileFragmentInterface,
        BookmarksFragment.bookmarksFragmentInterface {

    // Tag for logging
    private static final String TAG = "NavigationActivity";

    // Fragment Manager used to manage fragments that are to be shown to the user
    FragmentManager fmg = null;

    //Firebase instances for authentication
    private FirebaseAuth mAuth;

    // Declaring UI components
    TextView txtNavUserId, txtNavUserEmail;
    ImageView userImageView;

    // Single Instance for managing user data (login, profile, bookmarks)
    CSPrepGuideSingleTon singleTonInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleTonInstance = CSPrepGuideSingleTon.getInstance(getApplicationContext());
        Log.d(TAG, "user details after Navigation" + singleTonInstance.getAppUser().toString());

        setContentView(R.layout.activity_navigation_cs);

        //Support action toolbar in NavigationDrawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer in Navigation Activity
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Firebase instances for authentication and Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Reference: https://stackoverflow.com/questions/34973456/how-to-change-text-of-a-textview-in-navigation-drawer-header

        View navigationHeaderView = navigationView.getHeaderView(0);
        txtNavUserEmail = navigationHeaderView.findViewById(R.id.txtNavUserEmail);
        txtNavUserId = navigationHeaderView.findViewById(R.id.txtNavUserId);
        userImageView = navigationHeaderView.findViewById(R.id.imgNavUserPic);

        // Updating the details in navigation pane when user modifies the data in profile section
        if (singleTonInstance.getAppUser() != null) {
            onUserDetailsChanged();
        }

        fmg = getSupportFragmentManager();


        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // If name is not present for user, Navigate the user ot profile screen,
            // Else, Navigate to filter screen
            if (singleTonInstance.getAppUser().getName().isEmpty()) {
                displaySelectedScreen(R.id.nav_profile);
            } else {
                displaySelectedScreen(R.id.nav_filter_jobs);
            }
        }
    }

    /**
     * Default methods of Navigation Activity
     * Currently not needed in the app
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Default methods of Navigation Activity
     * Currently not needed in the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // Default menu provided by navigation activity is hidden as it is not needed in our app
        // getMenuInflater().inflate(R.menu.navigation_activity_c, menu);
        return true;
    }

    /**
     * Default methods of Navigation Activity
     * Currently not needed in the app
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method which handles logic for displaying the fragment in fragment container
     */
    private void displaySelectedScreen(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_filter_jobs:
                fragment = new FilterFragment();
                break;
            case R.id.nav_manage:
                fragment = new GuideFragment(this, fmg);
                break;
            case R.id.nav_bookmarks:
                fragment = new BookmarksFragment();
                break;
            case R.id.nav_help:
                fragment = new HelpFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                // This line will logout user from facebook
                // Instead do not logout user and handle this on login screen
                LoginManager.getInstance().logOut();
                Intent intentFromLogin = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentFromLogin);
                finish();
                break;

        }

        if (fragment != null) {

            // Add the fragment to the 'fragment_container' FrameLayout
            fmg.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) //https://stackoverflow.com/questions/25153364/implementing-back-button-in-android-fragment-activity
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public FragmentManager getFmg() {
        return fmg;
    }


    /**
     * Default methods of Navigation Activity
     * Method which handles user selection of a fragment from the navigation drawer
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }


    /**
     * Interface implementation methods from ProfileFragment
     * These methods allow interaction between fragments
     * This method is used to update user details in navigation pane whenever
     * details in profile page are changed (eg., photo and name)
     */
    @Override
    public void onUserDetailsChanged() {
        txtNavUserId.setText(singleTonInstance.getAppUser().getEmail());
        txtNavUserId.setText(singleTonInstance.getAppUser().getName());
        txtNavUserEmail.setText(singleTonInstance.getAppUser().getEmail());
        Picasso.with(getApplicationContext()).load(Uri.parse(singleTonInstance.getAppUser().getImageUrl())).fit().centerCrop().into(userImageView);

    }

    /**
     * Interface implementation methods from FilterFragment
     * These methods allow interaction between fragments
     */
    @Override
    public void onFilterClicked(String PostId, String JobTitle) {
        Log.d(TAG, PostId + JobTitle);
        singleTonInstance.setCurrentPostId(PostId);
        displaySelectedScreen(R.id.nav_manage);
    }

    /**
     * Interface implementation methods from BookmarksFragment
     * These methods allow interaction between fragments
     */

    @Override
    public void bookmarksItemClicked(int position, String id) {
        Log.d(TAG, "bookmarks clicked with" + position + id);
    }
}
