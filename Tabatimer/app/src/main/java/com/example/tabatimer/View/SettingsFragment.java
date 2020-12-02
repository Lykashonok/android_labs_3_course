package com.example.tabatimer.View;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.DropDownPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.ExtraFunctions;
import com.example.tabatimer.Model.Tables.TabataSetting;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.View.Settings.TSeekBarPreference;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String langKey = "lang";
    public static final String fontKey = "font";
    public static final String themeKey = "theme";
    public static final String killdataKey = "killdata";
    public static final String summaryTag = "summary";
    public static final String titleTag = "title";
    private DB database;
    private ApplicationViewModel applicationViewModel;

    private Preference killdataButton;
    private SeekBarPreference fontPreference;
    private SwitchPreference themePreference;
    private DropDownPreference langPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        database = DB.getInstance(requireActivity());
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);

        setHasOptionsMenu(false);
        killdataConfigure();
        langConfigure();
        themeConfigure();
        fontConfigure();
    }

    private void fontConfigure() {
        fontPreference = getPreferenceManager().findPreference(fontKey);
        fontPreference.setMin(10);
        fontPreference.setMax(16);
        fontPreference.setSeekBarIncrement(2);
        fontPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            database.tabataSettingDao().setSetting(fontKey, newValue.toString());
            applicationViewModel.setFontSizeSetting(Integer.parseInt(newValue.toString()));
            Integer newValueInt = Integer.parseInt(newValue.toString());

            ApplicationViewModel.setThemeInContext(requireActivity(), newValueInt);

            RecyclerView rv = getListView();
            int order = killdataButton.getOrder();

            View v1 = rv.getChildAt(order);
            int count = rv.getChildCount();

            List<View> prefViewList = new LinkedList<>();
            for (int i = 0; i < rv.getChildCount(); i++) {
                prefViewList.add(rv.getChildAt(i));
            }

            for (View v : prefViewList) {
                setTextSizeViews(v, newValueInt);
            }

            return true;
        });
    }

    private static void setTextSizeViews(View v, Integer value) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    // recursively call this method
                    setTextSizeViews(child, value);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTextSize(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void killdataConfigure() {
        killdataButton = getPreferenceManager().findPreference(killdataKey);
        if (killdataButton != null) {
            killdataButton.setOnPreferenceClickListener(preference -> {
                Dialog dialog = new Dialog(requireActivity(),
                        Boolean.parseBoolean(database.tabataSettingDao().getSetting(themeKey)) ?
                                R.style.ThemeOverlay_AppCompat_Dark :
                                R.style.Theme_Tabatimer_alert
                        );
                // Context of delete dialog
                dialog.setContentView(R.layout.sure_dialog);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                // Show dialog
                dialog.show();

                Button button_apply = dialog.findViewById(R.id.dialog_apply);

                // Buttons onclick listeners
                button_apply.setOnClickListener(v -> {
                    try {
                        // clearing app data
                        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                            ((ActivityManager)requireActivity().getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
                        } else {
                            String packageName = requireActivity().getApplicationContext().getPackageName();
                            Runtime runtime = Runtime.getRuntime();
                            runtime.exec("pm clear "+packageName);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                });
                return true;
            });
        }
    }

    private void themeConfigure() {
        themePreference = getPreferenceManager().findPreference(themeKey);
        Boolean darkTheme = Boolean.parseBoolean(database.tabataSettingDao().getSetting(themeKey));
        themePreference.setChecked(darkTheme);
        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            database.tabataSettingDao().setSetting(themeKey, newValue.toString());
            ExtraFunctions.triggerRebirth(requireActivity());
            return true;
        });
    }

    private void langConfigure() {
        langPreference = getPreferenceManager().findPreference(langKey);
        List<String> langs = new LinkedList<>();
        for(CharSequence cs : langPreference.getEntryValues()) { langs.add(cs.toString()); }
        String lang = database.tabataSettingDao().getSetting(langKey);
        int index = langs.indexOf(lang);
        langPreference.setValueIndex(index);
        langPreference.setSummary(langPreference.getEntries()[index]);
        langPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            database.tabataSettingDao().setSetting(langKey, newValue.toString());
            ExtraFunctions.triggerRebirth(requireActivity());
            return true;
        });
    }
}