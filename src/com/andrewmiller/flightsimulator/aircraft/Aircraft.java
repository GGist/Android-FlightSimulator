package com.andrewmiller.flightsimulator.aircraft;

import java.util.ArrayList;
import java.util.List;

import com.andrewmiller.flightsimulator.container.FloatArray;

public abstract class Aircraft<T> {
	
	//Used to encapsulate OpenGL draw calls that are meant to be aggregated
	public interface DrawMethod {
		void draw();
	}
	
	//Used to encapsulate the data and draw calls that make up the OpenGL vertex 
	//data for a sub-class
	public static class AircraftData {
		public final float[] vertexData;
		public final List<DrawMethod> drawCalls;
		
		AircraftData(float[] vData, List<DrawMethod> drawCalls) {
			this.vertexData = vData;
			this.drawCalls = drawCalls;
		}
	}
	
	private FloatArray vertexData = new FloatArray();
	private List<DrawMethod> drawCalls = new ArrayList<DrawMethod>();
	
	public AircraftData build() {
		FloatArray vData = vertexData;
		List<DrawMethod> dCalls = drawCalls;
		
		//vertexData = null;
		//dCalls = null;
		
		return new AircraftData(vData.asArray(), dCalls);
	}
	
	public void appendData(float[] vData, DrawMethod dCall) {
		if (vertexData == null)
			vertexData = new FloatArray();
		if (drawCalls == null)
			drawCalls = new ArrayList<DrawMethod>();
		
		vertexData.appendArray(vData);
		drawCalls.add(dCall);
	}
	
	public abstract T appendCowl(int numPoints);
	
	public abstract T appendCockpit(int numPoints);
	
	public abstract T appendFuselage(int numPoints);
	
	public abstract T appendFrontWing(int numPoints);
	
	public abstract T appendBackWing(int numPoints);
	
	public abstract T appendFin(int numPoints);
	
	public abstract T appendExhaust(int numPoints);
}
