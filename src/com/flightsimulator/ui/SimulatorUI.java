package com.flightsimulator.ui;

import com.flightsimulator.ui.widget.Control;

public class SimulatorUI extends UI {
	private static final String TAG = "SimulatorUI";
	private static final int MAX_POINTERS = 2;
	
	public SimulatorUI(int screenWidth, int screenHeight, Control[] controls) {
		super(screenWidth, screenHeight, MAX_POINTERS, controls);

	}
}
