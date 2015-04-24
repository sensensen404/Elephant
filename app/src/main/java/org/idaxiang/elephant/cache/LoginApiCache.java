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

package org.idaxiang.elephant.cache;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginApiCache
{
	private static final String TAG = LoginApiCache.class.getSimpleName();
	
	private SharedPreferences mPrefs;
	private String mAccessToken;
	private String mUid;
	private long mExpireDate;
	private String mAppId;
	private String mAppSecret;
	
    public LoginApiCache(Context context) {
		mPrefs = context.getSharedPreferences("access_token", Context.MODE_PRIVATE);
		mAccessToken = mPrefs.getString("access_token", null);
		mUid = mPrefs.getString("uid", "");
		mExpireDate = mPrefs.getLong("expires_in", Long.MIN_VALUE);
		mAppId = mPrefs.getString("app_id", null);
		mAppSecret = mPrefs.getString("app_secret", null);
	}
	
	public void logout() {
		mAccessToken = null;
		mExpireDate = Long.MIN_VALUE;
		mPrefs.edit().remove("access_token").remove("expires_in").remove("uid").commit();
	}
	
	public void cache() {
		mPrefs.edit().putString("access_token", mAccessToken)
					 .putLong("expires_in", mExpireDate)
					 .putString("uid", mUid)
					 .putString("app_id", mAppId)
					 .putString("app_secret", mAppSecret)
					 .commit();
	}
	
	public String getAccessToken() {
		return mAccessToken;
	}
	
	public String getUid() {
		return mUid;
	}
	
	public long getExpireDate() {
		return mExpireDate;
	}
	
	public String getAppId() {
		return mAppId;
	}
	
	public String getAppSecret() {
		return mAppSecret;
	}
}
