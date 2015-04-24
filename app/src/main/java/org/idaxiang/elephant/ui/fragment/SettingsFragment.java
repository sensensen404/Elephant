/* 
 * Copyright (C) 2014 Peter Cai
 *
 * This file is part of BlackLight
 *
 * BlackLight is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BlackLight is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BlackLight.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.idaxiang.elephant.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.support.Settings;
import org.idaxiang.elephant.ui.activity.TypeActivity;
import org.idaxiang.elephant.utils.AppLogger;


public class SettingsFragment extends PreferenceFragment implements
		Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

    private static final String TRADITIONAL_CHINESE = "key_traditional_chinese";

	private Settings mSettings;

    // Reading
    private CheckBoxPreference mPrefTraditionalChinese;
    private CheckBoxPreference mPrefScreenOn;
    private CheckBoxPreference mPrefScrollRead;
    private Preference mPrefReadingSetting;

	@SuppressWarnings("deprecation")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		mSettings = Settings.getInstance(getActivity());

//        mPrefTraditionalChinese = (CheckBoxPreference)findPreference(Settings.TRADITIONAL_CHINESE);
//        mPrefTraditionalChinese.setChecked(mSettings.getBoolean(Settings.TRADITIONAL_CHINESE, false));
//        mPrefTraditionalChinese.setOnPreferenceChangeListener(this);

        mPrefScreenOn = (CheckBoxPreference)findPreference(Settings.SCREEN_ON);
        mPrefScreenOn.setChecked(mSettings.getBoolean(Settings.SCREEN_ON, true));
        mPrefScreenOn.setOnPreferenceChangeListener(this);

        mPrefScrollRead = (CheckBoxPreference)findPreference(Settings.VOLUME_KEY);
        mPrefScrollRead.setChecked(mSettings.getBoolean(Settings.VOLUME_KEY, true));
        mPrefScrollRead.setOnPreferenceChangeListener(this);

//        mPrefReadingSetting = findPreference(Settings.READ_SETTING);
//        mPrefReadingSetting.setOnPreferenceClickListener(this);

    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
        String pre = preference.getKey();
        AppLogger.e(pre);
		if (preference == mPrefReadingSetting) {
			Intent i = new Intent(getActivity(), TypeActivity.class);
			startActivity(i);
			return true;
		}

		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mPrefTraditionalChinese) {
            mSettings.putBoolean(Settings.TRADITIONAL_CHINESE, Boolean.parseBoolean(newValue.toString()));
            return true;
        } else if(preference == mPrefScreenOn){
            mSettings.putBoolean(Settings.SCREEN_ON, Boolean.parseBoolean(newValue.toString()));
            return true;
        } else if(preference == mPrefScrollRead){
            mSettings.putBoolean(Settings.VOLUME_KEY, Boolean.parseBoolean(newValue.toString()));
            return true;
        }

		return false;
	}

	
}
