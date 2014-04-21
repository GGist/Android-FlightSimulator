package com.flightsimulator.ui.widget;


public interface Controllable {
	public void handleJoystick(Task task, float horiz, float vert);
	
	public void handleButton(Task task, boolean on);
	
	public void handleSlider(Task task, float sliderPos);
}
