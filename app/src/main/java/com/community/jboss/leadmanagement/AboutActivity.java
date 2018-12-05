package com.community.jboss.leadmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.community.jboss.leadmanagement.main.MainActivity;

import static com.community.jboss.leadmanagement.SettingsActivity.PREF_DARK_THEME;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, true);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
        }
        Glide.with(this)
                .load("https://zdnet2.cbsistatic.com/hub/i/2015/12/18/76f753d8-4015-4a70-82c2-cd79e715160e/b9a155bf35b06ad26501ac53b3158f55/jboss-logo.jpg")
                .into((ImageView)findViewById(R.id.image_logo_jboss));
        TextView compInfo = findViewById(R.id.compilation_info);
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version code: " + pInfo.versionName;
            compInfo.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_mail:
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:me@jbossoutreach.org"));
                startActivity(i);
                break;
            case R.id.twitter:
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse("https://twitter.com/jboss"));
                startActivity(in);
        }
    }
}
