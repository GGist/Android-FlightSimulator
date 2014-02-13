package com.flightsimulator.ui;

import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;
import android.util.Log;

import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.shaders.TextureShader;
import com.flightsimulator.utility.LoggerStatus;

public class SimulatorUI {
	private static final String TAG = "SimulatorUI";
	private final static int FIRST_POINTER = 0, SECOND_POINTER = 1;
	
	private final Vec2<Float> viewRes;
	
	private Vec2<Float> primaryTouchCoord = new Vec2<Float>(0f, 0f), 
			secondaryTouchCoord = new Vec2<Float>(0f, 0f);
	
	private final Control[] controls;
	/*private final Element[] elements;*/
	
	//Tracks up to two touches simultaneously
	public SimulatorUI(Vec2<Integer> viewRes, Control[] controls/*, Element[] elements*/) {
		this.controls = controls;
		this.viewRes = new Vec2<Float>(viewRes.x.floatValue(), viewRes.y.floatValue());
	}
	
	public void reportClick(int touchId, float x, float y) {
		if (!trackingTouchId(touchId)) {
			if (LoggerStatus.ON)
				Log.e(TAG, "Not currently tracking pointer " + touchId);
			
			return;
		}
		
		Vec2<Float> touchCoord = normAdjustedCoord(x, y);
		
		for (int i = 0; i < controls.length; ++i) {
			controls[i].handleClick(touchId, touchCoord.x, touchCoord.y);
		}
		
		updateTouchCoord(touchId, touchCoord.x, touchCoord.y);
	}
	
	public void reportDrag(int touchId, float x, float y) {
		if (!trackingTouchId(touchId)) {
			if (LoggerStatus.ON)
				Log.e(TAG, "Not currently tracking pointer " + touchId);
			
			return;
		}
		//System.out.println(touchId);
		
		Vec2<Float> deltaTouch = normAdjustedDelta(touchId, x, y);
		
		for (int i = 0; i < controls.length; ++i) {
			controls[i].handleDrag(touchId, deltaTouch.x, deltaTouch.y);
		}
		
		Vec2<Float> touchCoord = normAdjustedCoord(x, y);
		updateTouchCoord(touchId, touchCoord.x, touchCoord.y);
	}
	
	public void reportRelease(int touchId, float x, float y) {
		if (!trackingTouchId(touchId)) {
			if (LoggerStatus.ON)
				Log.e(TAG, "Not currently tracking pointer " + touchId);
			
			return;
		}
		
		Vec2<Float> touchCoord = normAdjustedCoord(x, y);
		
		for (int i = 0; i < controls.length; ++i) {
			controls[i].handleRelease(touchId, touchCoord.x, touchCoord.y);
		}
		
		updateTouchCoord(touchId, touchCoord.x, touchCoord.y);
	}
	
	public void reportScreenRotate() {
		float temp = viewRes.x;
		
		viewRes.x = viewRes.y;
		viewRes.y = temp;
	}
	
	public void draw(TextureShader program) {
		float[] projMatrix  = new float[16];

		setIdentityM(projMatrix, 0);
		
		if (viewRes.x > viewRes.y)
			orthoM(projMatrix, 0, -(viewRes.x / viewRes.y), (viewRes.x / viewRes.y), -1, 1, -0.1f, 100f);
		else
			orthoM(projMatrix, 0, -1, 1, -(viewRes.y / viewRes.x), (viewRes.y / viewRes.x), -0.1f, 100f);

		for(Control control : controls) {
			control.draw(program, projMatrix);
		}
	}
	
	//Must pass in a valid touchId
	private Vec2<Float> normAdjustedDelta(int touchId, float x, float y) {
		Vec2<Float> deltaTouch = normAdjustedCoord(x, y);
		
		switch(touchId)
		{
		case FIRST_POINTER:
			deltaTouch.x -= primaryTouchCoord.x;
			deltaTouch.y -= primaryTouchCoord.y;
			break;
		case SECOND_POINTER:
			deltaTouch.x -= secondaryTouchCoord.x;
			deltaTouch.y -= secondaryTouchCoord.y;
			break;
		}
		
		return deltaTouch;
	}
	
	private Vec2<Float> normAdjustedCoord(float x, float y) {
		Vec2<Float> normCoord = new Vec2<Float>();
		
		//Normalize [-1, 1]
		normCoord.x = x / (viewRes.x / 2) - 1;
		normCoord.y = -(y / (viewRes.y / 2) - 1);
		
		//Inverse Normalized Device Coordinate, expand coordinate on larger axis
		if (viewRes.x > viewRes.y)
			normCoord.x = normCoord.x * (viewRes.x / viewRes.y);
		else
			normCoord.y = normCoord.y * (viewRes.y / viewRes.x);
			
		return normCoord;
	}
	
	private boolean trackingTouchId(int touchId) {
		return touchId == FIRST_POINTER || touchId == SECOND_POINTER;
	}
	
	private void updateTouchCoord(int touchId, float x, float y) {
		switch (touchId)
		{
		case FIRST_POINTER:
			primaryTouchCoord.x = x;
			primaryTouchCoord.y = y;
			break;
		case SECOND_POINTER:
			secondaryTouchCoord.x = x;
			secondaryTouchCoord.y = y;
			break;
		}
	}
}
