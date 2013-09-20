/* 
 * Copyright (C) 2012 Paul Burke
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

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays a list of Files in a given path.
 *
 * @version 2012-10-28
 *
 * @author paulburke (ipaulpro)
 *
 */
public class FileListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<File>> {

	private static final int LOADER_ID = 0;

    private FileListAdapter mAdapter;
	private String mPath;
    private ArrayList<String> mFilterIncludeExtensions = new ArrayList<String>();

    /**
	 * Create a new instance with the given file path.
	 *
	 * @param path The absolute path of the file (directory) to display.
	 * @return A new Fragment with the given file path.
	 */
	public static FileListFragment newInstance(String path, ArrayList<String> filterIncludeExtensions) {
		FileListFragment fragment = new FileListFragment();
		Bundle args = new Bundle();
		args.putString(FileChooserActivity.PATH, path);
        args.putStringArrayList(FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS,filterIncludeExtensions);
                fragment.setArguments(args);
		return fragment;
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mAdapter = new FileListAdapter(getActivity()) {
            @Override
            public void onFileSelected(File file) {
                if (file.isDirectory()) {
                    this.clearSelectedFiles();
                    ((FileChooserActivity) getActivity()).onFolderSelected(file);
                }
            }

            @Override
            public void onFileDeselected(File file) {
            }
        };

		mPath = getArguments() != null ? getArguments().getString(
				FileChooserActivity.PATH) : FileChooserActivity.EXTERNAL_BASE_PATH;
        if(getArguments() != null){
              mFilterIncludeExtensions = getArguments().getStringArrayList(
                      FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS);
            }
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }


    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
	}

	@Override
	public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new FileLoader(getActivity(), mPath, mFilterIncludeExtensions);
	}

	@Override
	public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
		mAdapter.setListItems(data);
	}

	@Override
	public void onLoaderReset(Loader<List<File>> loader) {
		mAdapter.clear();
	}

    public ArrayList<String> getSelectedFiles() {
        return mAdapter.getSelectedFiles();
    }
}