package com.community.jboss.leadmanagement.main.contacts;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.community.jboss.leadmanagement.data.daos.ContactDao;
import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.utils.DbUtil;

import java.util.List;

public class ContactsFragmentViewModel extends AndroidViewModel {
    private LiveData<List<Contact>> mContacts;

    public ContactsFragmentViewModel(@NonNull Application application) {
        super(application);

        final ContactDao dao = DbUtil.contactDao(getApplication());
        mContacts = dao.getContacts();
    }

    public LiveData<List<Contact>> getContacts() {
        return mContacts;
    }

    public void deleteContact(Contact contact) {
        final ContactDao dao = DbUtil.contactDao(getApplication());
        dao.delete(contact);
    }
}
