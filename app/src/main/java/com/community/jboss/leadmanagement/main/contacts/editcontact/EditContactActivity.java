package com.community.jboss.leadmanagement.main.contacts.editcontact;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.data.entities.ContactNumber;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.community.jboss.leadmanagement.SettingsActivity.PREF_DARK_THEME;

public class EditContactActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_CONTACT_NUM = "INTENT_EXTRA_CONTACT_NUM";
    private static final int RC_NEW_PHOTO = 1492;

    @BindView(R.id.add_contact_toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.contact_photo)
    CircularImageView contactAvatar;
    @BindView(R.id.contact_select_photo)
    FloatingActionButton contactSelectPhoto;
    @BindView(R.id.contact_name_field)
    EditText contactNameField;
    @BindView(R.id.contact_number_field)
    EditText contactNumberField;
    @BindView(R.id.contact_email_field)
    EditText contactEmailField;
    @BindView(R.id.contact_query_field)
    EditText contactQueryField;
    @BindView(R.id.contact_address_field)
    EditText contactAddressField;
    @BindView(R.id.contact_call_notes_field)
    EditText contactCallNotesField;

    private EditContactActivityViewModel mViewModel;
    private boolean photoExists = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contact);

        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(EditContactActivityViewModel.class);
        mViewModel.getContact().observe(this, contact -> {
            if (contact == null || mViewModel.isNewContact()) {
                setTitle(R.string.title_add_contact);
            } else {
                setTitle(R.string.title_edit_contact);
                contactNameField.setText(contact.getName());
                contactEmailField.setText(contact.getEmail());
                contactAddressField.setText(contact.getAddress());
                contactQueryField.setText(contact.getQuery());
                contactCallNotesField.setText(contact.getCallNotes());
                byte[] mContactPhotoBytes = contact.getPhotoBytes();
                if(mContactPhotoBytes != null){
                    photoExists = true;
                    Glide.with(this).load(BitmapFactory.decodeByteArray(mContactPhotoBytes,0, mContactPhotoBytes.length)).into(contactAvatar);
                }
            }
            contactSelectPhoto.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, RC_NEW_PHOTO);
            });
        });
        mViewModel.getContactNumbers().observe(this, contactNumbers -> {
            if (contactNumbers == null || contactNumbers.isEmpty()) {
                return;
            }
            // Get only the first one for now
            final ContactNumber contactNumber = contactNumbers.get(0);
            contactNumberField.setText(contactNumber.getNumber());
        });

        final Intent intent = getIntent();
        final String number = intent.getStringExtra(INTENT_EXTRA_CONTACT_NUM);
        if(mViewModel.getContactNumberByNumber(number)!=null){
            mViewModel.setContact(mViewModel.getContactNumberByNumber(number).getContactId());
        }else{
            mViewModel.setContact(null);
            contactNumberField.setText(number);
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                saveContact();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_contact, menu);
        return true;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_NEW_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                photoExists = true;
                Glide.with(getApplicationContext()).load(bitmap).into(contactAvatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO Add multiple numbers
    private void saveContact() {
        // Check is Name or Password is empty
        if (!checkEditText(contactNameField, "Please enter name")||!checkNo(contactNumberField,"Enter Correct no.")
                || !checkEditText(contactNumberField, "Please enter number")) {
            return;
        }

        final String name = contactNameField.getText().toString();
        final String email = contactEmailField.getText().toString();
        final String query = contactQueryField.getText().toString();
        final String address = contactAddressField.getText().toString();
        final String callNotes = contactCallNotesField.getText().toString();
        byte[] photoBytes = null;
        if(photoExists){
            photoBytes = getBytesFromBitmap(((BitmapDrawable)contactAvatar.getDrawable()).getBitmap());
        }
        mViewModel.saveContact(name, email, query,address, callNotes, photoBytes);

        final String number = contactNumberField.getText().toString();
        mViewModel.saveContactNumber(number);

        finish();
    }

    private boolean checkEditText(EditText editText, String errorStr) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(errorStr);
            return false;
        }

        return true;
    }
    private boolean checkNo(EditText editText, String errorStr) {
        if (editText.getText().toString().length() < 4) {
            editText.setError(errorStr);
            return false;
        }
        return true;
    }
}
