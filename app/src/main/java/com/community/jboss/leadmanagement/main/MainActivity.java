package com.community.jboss.leadmanagement.main;


import android.Manifest;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.community.jboss.leadmanagement.BaseActivity;
import com.community.jboss.leadmanagement.PermissionManager;
import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.SettingsActivity;
import com.community.jboss.leadmanagement.main.contacts.ContactsFragment;
import com.community.jboss.leadmanagement.main.contacts.editcontact.EditContactActivity;
import com.community.jboss.leadmanagement.main.contacts.importcontact.ImportContactActivity;
import com.community.jboss.leadmanagement.main.groups.GroupsFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import static com.community.jboss.leadmanagement.SettingsActivity.PREF_DARK_THEME;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final int ID = 512;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private MainActivityViewModel mViewModel;
    private PermissionManager permissionManager;

    public static boolean useDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        visibleBtn(useDarkTheme);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mViewModel.getSelectedNavItem().observe(this, this::displayNavigationItem);

        NavigationView navView = findViewById(R.id.nav_view);
        View header =  navView.getHeaderView(0);

        header.findViewById(R.id.sign_in_button).setOnClickListener(this);
        header.findViewById(R.id.sign_out_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            signIn();
        }

        permissionManager = new PermissionManager(this, this);
        if (!permissionManager.permissionStatus(Manifest.permission.READ_PHONE_STATE)) {
            permissionManager.requestPermission(ID, Manifest.permission.READ_PHONE_STATE);
        }

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set initial selected item to Contacts
        if (savedInstanceState == null) {
            selectInitialNavigationItem();
        }

        initFab();
    }

    private void selectInitialNavigationItem() {
        final @IdRes int initialItem = R.id.nav_contacts;
        onNavigationItemSelected(navigationView.getMenu().findItem(initialItem));
        navigationView.setCheckedItem(initialItem);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }else if( id == R.id.action_import ){
            if(permissionManager.permissionStatus(Manifest.permission.READ_CONTACTS)){
                startActivity(new Intent(MainActivity.this,ImportContactActivity.class));
            }else{
                permissionManager.requestPermission(109,Manifest.permission.READ_CONTACTS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final MainActivityViewModel.NavigationItem navigationItem;
        switch (item.getItemId()) {
            case R.id.nav_contacts:
                navigationItem = MainActivityViewModel.NavigationItem.CONTACTS;
                break;
            case R.id.nav_groups:
                navigationItem = MainActivityViewModel.NavigationItem.GROUPS;
                break;
            case R.id.nav_settings:
                navigationItem = MainActivityViewModel.NavigationItem.SETTINGS;
                break;
            case R.id.toggle_theme:
                darkTheme(true);
                navigationItem = MainActivityViewModel.NavigationItem.CONTACTS;
                break;
            case R.id.light_theme:
                darkTheme(false);
                navigationItem = MainActivityViewModel.NavigationItem.CONTACTS;
                break;
            default:
                Timber.e("Failed to resolve selected navigation item id");
                throw new IllegalArgumentException();

        }
        mViewModel.setSelectedNavItem(navigationItem);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayNavigationItem(MainActivityViewModel.NavigationItem navigationItem) {
        MainFragment newFragment;

        switch (navigationItem) {
            case CONTACTS:
                newFragment = new ContactsFragment();
                break;
            case GROUPS:
                newFragment = new GroupsFragment();
                break;
            case SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
            case TOGGLE_THEME:
                darkTheme(true);

                return;
            case LIGHT_THEME:
                darkTheme(false);
            default:
                Timber.e("Failed to resolve selected NavigationItem");
                throw new IllegalArgumentException();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, newFragment)
                .commit();
        setTitle(newFragment.getTitle());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                updateUI(user);
                // ...
            } else {
                Log.w(TAG, "Google sign in failed");
                updateUI(null);
            }
        }
    }

    // [START auth_with_google]

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder().setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateUI(null);
            }
        });
    }


    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        View header =  navigationView.getHeaderView(0);

        TextView mDetailTextView = header.findViewById(R.id.nav_detail);
        TextView mStatusTextView = header.findViewById(R.id.nav_status);
        CircularImageView mProfileImageView = header.findViewById(R.id.nav_prof_pic);

        if (user != null) {
            Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();

            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getDisplayName()));
            mProfileImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(user.getPhotoUrl()).into(mProfileImageView);

            header.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            header.findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();

            mStatusTextView.setText(R.string.app_desc);
            mDetailTextView.setText(R.string.app_name);
            Glide.with(this).load("https://github.com/jboss-outreach/lead-management-android/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png?raw=true").into(mProfileImageView);

            header.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            header.findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }


    public void initFab() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ContactsFragment) {
            fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditContactActivity.class)));
            fab.setImageResource(R.drawable.ic_add_white_24dp);
        }
    }

    private void darkTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }

    private void visibleBtn(boolean darkTheme){
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem darkBtn = menu.findItem(R.id.toggle_theme);
        MenuItem lightBtn = menu.findItem(R.id.light_theme);

        if (darkTheme){
            darkBtn.setVisible(false);
            lightBtn.setVisible(true);
        }
        else {
            darkBtn.setVisible(true);
            lightBtn.setVisible(false);
        }
    }

}
