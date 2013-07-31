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
package com.mobileinno.multifilechooserexample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.mobileinno.multifilechooser.FileChooserActivity;

/**
 * @author paulburke (ipaulpro)
 */
public class FileChooserExampleActivity extends Activity {

    private static final int REQUEST_CODE = 6384; // onActivityResult request code

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// Create a simple button to start the file chooser process
	Button button = new Button(this);
	button.setText(R.string.choose_file);
	button.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// Display the file chooser dialog
		showChooser();
	    }
	});

	setContentView(button);
    }

    private void showChooser() {
	// Create the chooser Intent
	Intent intent = new Intent(this, FileChooserActivity.class);
	intent.putExtra(FileChooserActivity.ECHO, (long) 8883);
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
		    long echo = data.getLongExtra(FileChooserActivity.ECHO, -1);
		    Log.d("echo", "" + echo);
		}
		break;
	}
	super.onActivityResult(requestCode, resultCode, data);
    }
}