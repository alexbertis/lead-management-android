package com.community.jboss.leadmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import static com.community.jboss.leadmanagement.SettingsFragment.PREF_DARK_THEME;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private final String githubUrl = "https://api.github.com/repos/JBossOutreach/lead-management-android/contributors";
    private final String TAG = getClass().getName();

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

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, githubUrl, null, response -> {
                    LinearLayout linearLayout, rootLayout = findViewById(R.id.about_layout);
                    try {
                        for (int i=0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            linearLayout = new LinearLayout(AboutActivity.this);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            linearLayout.setPadding(6,6,6,6);
                            TextView username = new TextView(AboutActivity.this);
                            username.setTextSize(14.0f);
                            username.setTextColor(Color.parseColor("#777777"));
                            username.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            username.setText(obj.getString("login"));
                            username.setPadding(10,10,10,10);
                            linearLayout.addView(username);
                            linearLayout.setClickable(true);
                            linearLayout.setFocusable(true);
                            linearLayout.setOnClickListener(v -> {
                                ContributorFragment contributorFragment = new ContributorFragment();
                                Bundle b = new Bundle();
                                b.putString("jsonData", obj.toString());
                                contributorFragment.setArguments(b);
                                contributorFragment.show(getSupportFragmentManager(), null);
                            });
                            rootLayout.addView(linearLayout);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        RequestQueue requestQueue = Volley.newRequestQueue(AboutActivity.this);
        requestQueue.add(jsonObjectRequest);

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
                break;
            case R.id.facebook:
                Intent inte = new Intent(Intent.ACTION_VIEW);
                inte.setData(Uri.parse("https://www.facebook.com/jbossoutreach/"));
                startActivity(inte);
                break;
            case R.id.youtube:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCmka-21Sp6uc33v8ApDSOEg"));
                startActivity(intent);
        }
    }
}
