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

package org.idaxiang.elephant.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.support.Utility;


public abstract class ToolbarActivity extends ActionBarActivity
{
	protected Toolbar mToolbar;
	protected int mLayout = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= 21) {
			requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			getWindow().setAllowEnterTransitionOverlap(true);
			getWindow().setAllowReturnTransitionOverlap(true);
		}
		super.onCreate(savedInstanceState);
        if(mLayout != 0){
            setContentView(mLayout);
        }

		mToolbar = Utility.findViewById(this, R.id.toolbar);
		
		if (mToolbar != null) {
//			mToolbar.bringToFront();
            mToolbar.setTitle("Elephant");//设置Toolbar标题
            mToolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
			setSupportActionBar(mToolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public Toolbar getToolbar() {
		return mToolbar;
	}
}
