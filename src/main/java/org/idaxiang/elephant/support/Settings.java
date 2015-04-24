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

package org.idaxiang.elephant.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
  Settings Provider
*/
public class Settings
{
	public static final String XML_NAME = "settings";

    public static final String TRADITIONAL_CHINESE = "key_traditional_chinese";
    public static final String FONT_SIZE = "key_font_size";
    public static final String SCREEN_ON = "key_screen_on";
    public static final String INDENT = "key_indent";
    public static final String VOLUME_KEY = "key_scroll_read";
    public static final String TRADITIONAL_SYMBOL = "tradition_symbol";
    public static final String RIGHT_HANDED = "right_hand";
    public static final String AUTO_PIC = "auto_nopic";
    public static final String READ_SETTING = "key_read_set";

    private static Settings sInstance;
	
	private SharedPreferences mPrefs;
	
	public static Settings getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Settings(context);
		}
		
		return sInstance;
	}
	
	private Settings(Context context) {
//		mPrefs = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public Settings putBoolean(String key, boolean value) {
		mPrefs.edit().putBoolean(key, value).commit();
		return this;
	}
	
	public boolean getBoolean(String key, boolean def) {
		return mPrefs.getBoolean(key, def);
	}
	
	public Settings putInt(String key, int value) {
		mPrefs.edit().putInt(key, value).commit();
		return this;
	}
	
	public int getInt(String key, int defValue) {
		return mPrefs.getInt(key, defValue);
	}

	public Settings putString(String key, String value) {
		mPrefs.edit().putString(key, value).commit();
		return this;
	}

	public String getString(String key, String defValue) {
		return mPrefs.getString(key, defValue);
	}
	
}
