package com.andrewmiller.flightsimulator.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

public class SimulatorActivity extends Activity {
	private GLSurfaceView mySurfaceView;
	private boolean rendererSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mySurfaceView = new GLSurfaceView(this);
		
		//Grab OpenGL ES device version information
		ActivityManager activityInfo = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configInfo = activityInfo.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configInfo.reqGlEsVersion >= 0x20000;
		
		final SimulatorRenderer myRenderer = new SimulatorRenderer(this);
		
		if (supportsEs2) {
			//Continue with the program
			mySurfaceView.setEGLContextClientVersion(2);
			mySurfaceView.setRenderer(myRenderer);
			rendererSet = true;
		} else {
			//Could choose to render using OpenGL ES 1.0
			//Instead, display error message to the user
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.", 
				Toast.LENGTH_LONG).show();
	        return;
		}
		
		setContentView(mySurfaceView);
	}
	
	protected void onPause() {
		super.onPause();
		if (rendererSet) {
			mySurfaceView.onPause();
		}
	}
	
	protected void onResume() {
		super.onResume();
		if (rendererSet) {
			mySurfaceView.onResume();
		}
	}
	
}
