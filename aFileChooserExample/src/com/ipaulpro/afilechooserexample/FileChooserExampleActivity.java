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

package com.ipaulpro.afilechooserexample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.util.ArrayList;

/**
 * @author paulburke (ipaulpro)
 */
public class FileChooserExampleActivity extends Activity {

	private static final int REQUEST_CODE = 6384; // onActivityResult request code

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

        Button button= (Button) findViewById(R.id.select);
		button.setText(R.string.choose_file);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Display the file chooser dialog
				showChooser();
			}
		});

        RadioGroup group= (RadioGroup) findViewById(R.id.set_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int pos) {
                switch (pos){
                    case R.id.select_file:
                        FileUtils.setSelectMode(FileUtils.MODE_SELECT_FILE);
                        break;
                    case R.id.select_dir:
                        FileUtils.setSelectMode(FileUtils.MODE_SELECT_DIR);
                        break;
                    default:
                        FileUtils.setSelectMode(FileUtils.MODE_SELECT_FILE);
                        break;
                }
            }
        });
	}

//      private static final ArrayList<String> INCLUDE_EXTENSIONS_LIST = new ArrayList<String>();
//      static{
//            INCLUDE_EXTENSIONS_LIST.add(".apk");
//            INCLUDE_EXTENSIONS_LIST.add(".bin");
//          }

	private void showChooser() {
        // Create the chooser Intent
        Intent intent = new Intent(this, FileChooserActivity.class);
        // add filter on file extenstion
//        intent.putStringArrayListExtra(FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS, INCLUDE_EXTENSIONS_LIST);

        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        break;
                    }
                    ArrayList<String> paths=data.getStringArrayListExtra(FileChooserActivity.URIS);
                    if(paths!=null){
                        StringBuilder sb = new StringBuilder();
                        for (String str : paths)
                         sb.append(str+"\n");
                        Toast.makeText(this,sb.toString(),Toast.LENGTH_LONG).show();
                        Log.d(FileChooserActivity.TAG, sb.toString());
                    }

                }
                break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}