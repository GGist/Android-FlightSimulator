package com.flightsimulator.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Toast;

public class SimulatorActivity extends Activity {
	
	private GLSurfaceView surfaceView;
	private boolean rendererSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		surfaceView = new GLSurfaceView(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Grab OpenGL ES device version information
		ActivityManager activityInfo = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configInfo = activityInfo.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configInfo.reqGlEsVersion >= 0x20000;
		
		//System.out.println(activityInfo.getMemoryClass());
		
		final SimulatorRenderer renderer = new SimulatorRenderer(this);

		if (supportsEs2) {
			surfaceView.setEGLContextClientVersion(2);
			
			surfaceView.setRenderer(renderer);
			rendererSet = true;
		} else {
			//Could choose to render using OpenGL ES 1.0
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG)
			.show();
			
	        return;
		}

		surfaceView.setOnTouchListener(getTouchListener(renderer));
		
		setContentView(surfaceView);
	}
	
	private OnTouchListener getTouchListener(final SimulatorRenderer renderer) {
		return new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					if (event != null) {
						
						//For the wiki: getPointerId() gets the pointer id of the pointer at the specified index
						//accounting for pointers coming and going. getX() and getY() accept an index which do change
						//per pointer but as long as the pointerId is associated with the correct coordinate data, that
						//is all that matters
						
						final int maskedAction = event.getActionMasked();
						
						if (maskedAction == MotionEvent.ACTION_MOVE) {
							final int pointerCount = event.getPointerCount();
							for (int i = 0; i < pointerCount; ++i) {
									final int touchPointer = event.getPointerId(i);
									final float x = event.getX(i),
											y = event.getY(i);
									surfaceView.queueEvent(new Runnable() {
										@Override
										public void run() {
											renderer.handleTouch(maskedAction, touchPointer, x, y);
										}
									});
							}
						} else {
							final int touchPointer = event.getPointerId(event.getActionIndex());
							final float x = event.getX(event.getActionIndex()),
									y = event.getY(event.getActionIndex());
							surfaceView.queueEvent(new Runnable() {
								@Override
								public void run() {	
									renderer.handleTouch(maskedAction, touchPointer, x, y);
								}
							});
						}
						
						return true;
					}
					
				} catch (IllegalArgumentException e) {
					//Pointer out of bounds, no big deal
				}

				return false;
			}
		};
	}
	
	protected void onPause() {
		super.onPause();
		if (rendererSet) {
			surfaceView.onPause();
		}
	}
	
	protected void onResume() {
		super.onResume();
		if (rendererSet) {
			surfaceView.onResume();
		}
	}
}
