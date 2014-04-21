package com.flightsimulator.ui;

import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.shader.TextureShader;
import com.flightsimulator.ui.widget.Control;
import com.flightsimulator.utility.LoggerStatus;

public class UI {
	private static final String TAG = "UI";
	
	private final Vec2<Float> viewRes;
	
	private final int MAX_UI_POINTERS;
	private final List<Vec2<Float>> touchCoords;
	
	private final Control[] controls;
	/*private final Element[] elements;*/
	
	public UI(int screenWidth, int screenHeight, int maxPointers, Control[] controls/*, Element[] elements*/) {
		this.viewRes = new Vec2<Float>((float) screenWidth, (float) screenHeight);
		
		this.MAX_UI_POINTERS = maxPointers;
		this.touchCoords = new ArrayList<Vec2<Float>>(MAX_UI_POINTERS);
		for (int i = 0; i < MAX_UI_POINTERS; ++i) {
			touchCoords.add(new Vec2<Float>());
		}
		
		this.controls = controls;
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
	
	public void reportDimensions(int width, int height) {
		viewRes.x = (float) width;
		viewRes.y = (float) height;
	}
	
	public void draw(TextureShader program) {
		float[] projMatrix  = new float[16];

		setIdentityM(projMatrix, 0);
		
		if (viewRes.x > viewRes.y)
			orthoM(projMatrix, 0, -(viewRes.x / viewRes.y), (viewRes.x / viewRes.y), -1, 1, -0.1f, 100f);
		else
			orthoM(projMatrix, 0, -1, 1, -(viewRes.y / viewRes.x), (viewRes.y / viewRes.x), -0.1f, 100f);

		//Draw Elements
		
		//Draw Controls
		for(Control control : controls) {
			control.draw(program, projMatrix);
		}
	}
	
	//Must pass in a valid touchId
	private Vec2<Float> normAdjustedDelta(int touchId, float x, float y) {
		Vec2<Float> deltaTouch = normAdjustedCoord(x, y);
		
		deltaTouch.x -= touchCoords.get(touchId).x;
		deltaTouch.y -= touchCoords.get(touchId).y;
		
		return deltaTouch;
	}
	
	private Vec2<Float> normAdjustedCoord(float x, float y) {
		Vec2<Float> normCoord = new Vec2<Float>();
		
		//Normalize [-1, 1]
		normCoord.x = x / (viewRes.x / 2) - 1;
		normCoord.y = -(y / (viewRes.y / 2) - 1);
		
		//Normalized Inverse Device Coordinate, expand coordinate on larger axis
		//NIDC <- Expand - Normalized Coordinates - Shrink -> NDC
		if (viewRes.x > viewRes.y)
			normCoord.x = normCoord.x * (viewRes.x / viewRes.y);
		else
			normCoord.y = normCoord.y * (viewRes.y / viewRes.x);
			
		return normCoord;
	}
	
	private boolean trackingTouchId(int touchId) {
		return touchId < MAX_UI_POINTERS;
	}
	
	private void updateTouchCoord(int touchId, float x, float y) {
		touchCoords.get(touchId).x = x;
		touchCoords.get(touchId).y = y;
	}
}
