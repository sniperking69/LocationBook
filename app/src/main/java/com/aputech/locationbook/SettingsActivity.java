package com.aputech.locationbook;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ImageView imageView = findViewById(R.id.user);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            Glide
                    .with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(imageView);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }
    }
}