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
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class NavigationActivityCS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        filterFragment.filterFragmentInterface,
        guideFragment.OnFragmentInteractionListener,
        profileFragment.profileFragmentInterface,
        bookmarksFragment.bookmarksFragmentInterface,
        helpFragment.helpFragmentInterface {

    FragmentManager fmg = null;
    private FirebaseAuth mAuth;
    TextView txtNavUserId, txtNavUserEmail;
    private static final String TAG = "NavigationActivity";
    CSPrepGuideSingleTon singleTonInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleTonInstance = CSPrepGuideSingleTon.getInstance(getApplicationContext());
        Log.d(TAG, "user details after Navigation"+singleTonInstance.getAppUser().toString());


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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

//        https://stackoverflow.com/questions/34973456/how-to-change-text-of-a-textview-in-navigation-drawer-header
        View navigationHeaderView = navigationView.getHeaderView(0);
        txtNavUserEmail = navigationHeaderView.findViewById(R.id.txtNavUserEmail);
        txtNavUserId = navigationHeaderView.findViewById(R.id.txtNavUserId);
//        txtNavUserId.setText(user.getUid());

        if(singleTonInstance.getAppUser() != null){
            txtNavUserId.setText(singleTonInstance.getAppUser().getEmail());
            txtNavUserId.setText(singleTonInstance.getAppUser().getName());
            //        txtNavUserEmail.setText(user.getEmail());
//            Log.d(TAG, String.valueOf(user.getDisplayName()));
            txtNavUserEmail.setText(singleTonInstance.getAppUser().getEmail());
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

            // Create an instance of ExampleFragment
//            filterFragment firstFragment = new filterFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());

//            FragmentManager manager = getSupportFragmentManager();
//            // Add the fragment to the 'fragment_container' FrameLayout
//            manager.beginTransaction()
//                    .add(R.id.fragment_container, firstFragment)
//                    .commit();

            if(singleTonInstance.getAppUser().getName().isEmpty()){
                displaySelectedScreen(R.id.nav_profile);
            }else{
                displaySelectedScreen(R.id.nav_filter_jobs);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_activity_c, menu);
        return true;
    }

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

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch(id){
            case R.id.nav_profile:
                fragment = new profileFragment();
                break;
            case R.id.nav_filter_jobs:
                fragment = new filterFragment();
                break;
            case R.id.nav_manage:
                fragment = new guideFragment(this, fmg);
                break;
            case R.id.nav_bookmarks:
                fragment = new bookmarksFragment();
                break;
            case R.id.nav_help:
                fragment = new helpFragment();
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

        if(fragment != null){

            // Add the fragment to the 'fragment_container' FrameLayout
            fmg.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) //https://stackoverflow.com/questions/25153364/implementing-back-button-in-android-fragment-activity
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public FragmentManager getFmg(){
        return fmg;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFilterClicked(String JobId, String JobTitle) {
        Log.d(TAG, JobId + JobTitle);
        displaySelectedScreen(R.id.nav_manage);
    }

    @Override
    public void bookmarksItemClicked(int position, String id) {
        Log.d(TAG, "bookmarks clicked with" + position + id);
    }
}
