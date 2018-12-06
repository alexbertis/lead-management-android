package com.community.jboss.leadmanagement;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class ContributorFragment extends BottomSheetDialogFragment {


    public ContributorFragment() {
        // Required empty public constructor
    }
    MaterialButton goButton;
    CircularImageView civ; TextView name, contributions;
    JSONObject info;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_contributor, container, false);
        String jsonObject = getArguments().getString("jsonData");
        try {
            info = new JSONObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        civ = v.findViewById(R.id.contrib_picture);
        name = v.findViewById(R.id.contrib_name);
        contributions = v.findViewById(R.id.contrib_num_commits);
        try {
            Glide.with(getContext()).load(info.getString("avatar_url")).into(civ);
            name.setText(info.getString("login"));
            contributions.setText("Contributions from this user: " + String.valueOf(info.getInt("contributions")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        goButton = v.findViewById(R.id.contrib_link);
        goButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            try {
                intent.setData(Uri.parse(info.getString("html_url")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        });
        // Inflate the layout for this fragment
        return v;
    }

}
