package com.flightsimulator.ui;

import com.flightsimulator.ui.widget.Control;

public class MenuUI extends UI {
	private static final String TAG = "MenuUI";
	private static final int MAX_POINTERS = 1;
	
	public MenuUI(int screenWidth, int screenHeight, Control[] controls) {
		super(screenWidth, screenHeight, MAX_POINTERS, controls);

	}
}
