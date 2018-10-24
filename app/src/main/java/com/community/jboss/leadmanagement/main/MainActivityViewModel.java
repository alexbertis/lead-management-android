package com.community.jboss.leadmanagement.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<NavigationItem> mSelectedNavItem = new MutableLiveData<>();

    LiveData<NavigationItem> getSelectedNavItem() {
        return mSelectedNavItem;
    }

    void setSelectedNavItem(NavigationItem item) {
        mSelectedNavItem.setValue(item);
    }

    enum NavigationItem {
        CONTACTS,
        GROUPS,
        SETTINGS,
        TOGGLE_THEME,
        LIGHT_THEME
    }
}
