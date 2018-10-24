package com.community.jboss.leadmanagement.main;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {
    public abstract @StringRes
    int getTitle();
}
