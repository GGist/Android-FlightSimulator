package com.flightsimulator.ui;

public interface Control {
	public boolean setPosition(float x, float y);
	public boolean handleClick(float x, float y);
	public boolean handleRelease(float x, float y);
	public boolean handleDrag(float deltaX, float deltaY);
	public void draw();
}
