package com.anengine.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.anengine.samples.views.View3D;
import com.anengine.samples_dae.R;

public class DAEActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new View3D(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dae, menu);
		return true;
	}

}
