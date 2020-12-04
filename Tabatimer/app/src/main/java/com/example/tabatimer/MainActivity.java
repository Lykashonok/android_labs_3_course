package com.example.tabatimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataSetting;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.View.SettingsFragment;
import com.example.tabatimer.View.TabataItemEditFragment;
import com.example.tabatimer.View.TabataItemListFragment;
import com.example.tabatimer.View.TabataSetEditFragment;
import com.example.tabatimer.View.TabataSetListFragment;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ApplicationViewModel applicationViewModel;
    private final String SettingsTitle = "Settings";
    NavController navController;


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeApplicationViewModel();
        setContentView(R.layout.activity_main);
        NavHostFragment navController_fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navController_fragment.getNavController();
    }

    void initializeApplicationViewModel() {
        applicationViewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);
        applicationViewModel.createSound(
                MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_NOTIFICATION_URI)
        );
        DB db = DB.getInstance(this);

        // Theme
        applicationViewModel.setDatabase(db);
        if (db.tabataSettingDao().getAll().size() == 0) {
            initializeSettings(db);
        }
        if (Boolean.parseBoolean(db.tabataSettingDao().getSetting(SettingsFragment.themeKey))) {
            setTheme(R.style.Theme_AppCompat);
        } else {
            setTheme(R.style.Theme_AppCompat_DayNight);
        }

        // Font
        Integer font = Integer.parseInt(db.tabataSettingDao().getSetting(SettingsFragment.fontKey));
        applicationViewModel.setFontSizeSetting(font);

        ApplicationViewModel.setThemeInContext(this, font);

        // Locale
        Locale locale;
        switch(db.tabataSettingDao().getSetting(SettingsFragment.langKey)) {
            case "ru":
                locale = new Locale("ru","RU");
                break;
            default:
                locale = new Locale("en","US");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    void initializeSettings(DB db) {
        db.tabataSettingDao().insert(new TabataSetting(SettingsFragment.langKey, "en"));
        db.tabataSettingDao().insert(new TabataSetting(SettingsFragment.themeKey, "false"));
        db.tabataSettingDao().insert(new TabataSetting(SettingsFragment.fontKey, "14"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(SettingsTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getTitle().toString()) {
            case SettingsTitle:
                navController.navigate(R.id.action_tabataSetListFragment_to_settingsFragment);
                break;
        }
        return true;
    }
}