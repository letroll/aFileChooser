/* 
 * Copyright (C) 2013 Paul Burke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.ipaulpro.afilechooser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main Activity that handles the FileListFragments
 * 
 * @version 2013-06-25
 * 
 * @author paulburke (ipaulpro)
 * 
 */
public class FileChooserActivity extends SherlockFragmentActivity implements
		OnBackStackChangedListener {

	public static final String PATH = "path";
    public static final String URIS = "uris";
    public static final String TAG = "aFileChooser";
	public static final String EXTERNAL_BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String EXTRA_FILTER_INCLUDE_EXTENSIONS =
            "com.ipaulpro.afilechooser.EXTRA_FILTER_INCLUDE_EXTENSIONS";
    private ArrayList<String> mFilterIncludeExtensions = new ArrayList<String>();

	private FragmentManager mFragmentManager;

	private final BroadcastReceiver mStorageListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, R.string.storage_removed, Toast.LENGTH_LONG)
					.show();
			finishWithResult(null);
		}
	};

	private String mPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        if(intent != null){
            mFilterIncludeExtensions = intent.getStringArrayListExtra(EXTRA_FILTER_INCLUDE_EXTENSIONS);
        }

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            mPath = EXTERNAL_BASE_PATH;
            addFragment();
        } else {
            mPath = savedInstanceState.getString(PATH);
        }

		setTitle(mPath);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterStorageListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerStorageListener();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PATH, mPath);
	}

	@Override
	public void onBackStackChanged() {
		int count = mFragmentManager.getBackStackEntryCount();
		if (count > 0) {
			BackStackEntry fragment = mFragmentManager
					.getBackStackEntryAt(count - 1);
			mPath = fragment.getName();
		} else {
			mPath = EXTERNAL_BASE_PATH;
		}
		setTitle(mPath);
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.menu_ok, 0, "select current directory")
				.setIcon(R.drawable.abs__ic_cab_done_holo_light)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				setResult(RESULT_CANCELED);
				finish();
			}
			mFragmentManager.popBackStack();
			return true;
		} else if (itemId == R.id.menu_ok) {
            switch (FileUtils.selectMode){
                case FileUtils.MODE_SELECT_DIR:
                    onDecisionMade(new ArrayList<String>(Arrays.asList(mPath)));
                    break;
                case FileUtils.MODE_SELECT_FILE:
                    onDecisionMade(((FileListFragment)mFragmentManager.findFragmentByTag(mPath)).getSelectedFiles());
                    break;
            }
            return true;
		}

		return super.onOptionsItemSelected(item);
	}

    /**
	 * Add the initial Fragment with given path.
	 */
	private void addFragment() {
		FileListFragment fragment = FileListFragment.newInstance(mPath, mFilterIncludeExtensions);
		mFragmentManager.beginTransaction()
				.add(R.id.explorer_fragment, fragment,mPath).commit();
	}

	/**
	 * "Replace" the existing Fragment with a new one using given path. We're
	 * really adding a Fragment to the back stack.
	 * 
	 * @param file
	 *            The file (directory) to display.
	 */
	private void replaceFragment(File file) {
		mPath = file.getAbsolutePath();
		FileListFragment fragment = FileListFragment.newInstance(mPath, mFilterIncludeExtensions);
		mFragmentManager.beginTransaction()
				.replace(R.id.explorer_fragment, fragment,mPath)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(mPath).commit();
	}

    /**
     * Finish this Activity with a result code and URI of the selected file.
     *

     * @param files The file selected.
     */
    private void finishWithResult(ArrayList<String> files) {
        if (files != null) {
            Intent result = new Intent();
            result.putExtra(URIS, files);
            this.setResult(RESULT_OK, result);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

      /**
   * Called when the user selects a File
   *
   * @param file
   *            The file that was selected
   */
  protected void onFolderSelected(File file) {
      if (file != null)
          if (file.isDirectory()) {
              replaceFragment(file);
          }
  }

    /**
     * Called when the user selects a File
     *
     * @param files The files that where selected
     */
    protected void onDecisionMade(ArrayList<String> files) {
        if(files.isEmpty())Toast.makeText(FileChooserActivity.this,
                  R.string.choose_file, Toast.LENGTH_SHORT).show();
        else
        finishWithResult(files);
    }

	/**
	 * Register the external storage BroadcastReceiver.
	 */
	private void registerStorageListener() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		registerReceiver(mStorageListener, filter);
	}

	/**
	 * Unregister the external storage BroadcastReceiver.
	 */
	private void unregisterStorageListener() {
		unregisterReceiver(mStorageListener);
	}
}
