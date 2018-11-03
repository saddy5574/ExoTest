package com.example.sayyad.exotest.Setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.sayyad.exotest.Constants.Constant;
import com.example.sayyad.exotest.R;

public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingFragment()).commit();


        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class MainSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,Constant{

        CheckBoxPreference skipCheckBoxPreference;
        CheckBoxPreference justPlayCheckBoxPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            skipCheckBoxPreference = (CheckBoxPreference) findPreference(ENABLE_SKIPPING);
            justPlayCheckBoxPreference = (CheckBoxPreference) findPreference(ENABLE_JUST_PLAY);

            skipCheckBoxPreference.setOnPreferenceClickListener(this);
            justPlayCheckBoxPreference.setOnPreferenceClickListener(this);

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            String key = preference.getKey();

            if(key.equals(ENABLE_SKIPPING) && skipCheckBoxPreference.isChecked()){
                justPlayCheckBoxPreference.setChecked(false);
            }

            if (key.equals(ENABLE_JUST_PLAY) && justPlayCheckBoxPreference.isChecked()){
                skipCheckBoxPreference.setChecked(false);
            }

            return true;
        }
    }
}
