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
		
        mySurfaceView.setOnTouchListener(new OnTouchListener() {
            float previousX, previousY;
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {                                                                           
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;
                        
                        previousX = event.getX();
                        previousY = event.getY();
                        
                        mySurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                myRenderer.handleTouchDrag(
                                    deltaX, deltaY);
                            }
                        });
                    }                                        

                    return true;                    
                } else {
                    return false;
                }
            }
        });
		
		setContentView(mySurfaceView);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (rendererSet) {
			mySurfaceView.onPause();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (rendererSet) {
			mySurfaceView.onResume();
		}
	}
	
}
