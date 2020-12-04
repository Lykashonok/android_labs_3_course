package com.example.tabatimer.View.Settings;

import android.content.Context;
import android.view.View;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

public class TSeekBarPreference extends SeekBarPreference {
    public View view;

    public TSeekBarPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        this.view = view.itemView;
    }
}
