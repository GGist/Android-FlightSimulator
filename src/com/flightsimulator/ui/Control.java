package com.flightsimulator.ui;

import com.flightsimulator.shaders.TextureShader;

public abstract class Control {
	protected final static int NO_TOUCH_ID = -1;
	
	protected int currTouchId;
	
	protected final Controllable object;
	protected final Task task;
	
	public Control(Task task, Controllable object) {
		this.object = object;
		this.task = task;
		this.currTouchId = NO_TOUCH_ID;
	}
	
	public abstract void handleClick(int touchId, float x, float y);
	
	public abstract void handleDrag(int touchId, float deltaX, float deltaY);
	
	public abstract void handleRelease(int touchId, float x, float y);

	public abstract void draw(TextureShader program, float[] orthoMatrix);
}