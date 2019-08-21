package com.erdemsiyam.memorizeyourwords;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsDetailFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.settings);
    }
    public int getFontSize(){
        ListPreference prefFontSize = (ListPreference)findPreference("pref_font_size");
        return Integer.valueOf(prefFontSize.getValue());
    }
}
