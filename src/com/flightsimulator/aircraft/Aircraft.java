package com.flightsimulator.aircraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public abstract class Aircraft<T> {
	//Data Storage Objects
	
	//Encapsulates GLES draw calls
	protected interface DrawMethod {
		void draw();
	}
	
	//Encapsulates attachment points for aircraft parts
	protected static class AttachmentData {
		public enum Point {
			COWL,
			COCKPIT,
			FRONT_LEFT_WING,
			FRONT_RIGHT_WING,
			BACK_LEFT_WING,
			BACK_RIGHT_WING,
			FIN,
			EXHAUST
		};
		
		private final EnumMap<Point, Float> map;
		
		public AttachmentData() {
			map = new EnumMap<Point, Float>(Point.class);
		}
		
		public void setAttachPoint(Point key, float value) {
			map.put(key, Float.valueOf(value));
		}
		
		public float getAttachPoint(Point key) throws IllegalArgumentException {
			if (map.get(key) == null)
				throw new IllegalArgumentException("Attachment Point Has Not Been Set");
			
			return map.get(key).floatValue();
		}
	}
	
	//Encapsulates aircraft vertex data and draw call aggregate
	protected static class AircraftData {
		public final float[] vertexData;
		public final List<DrawMethod> drawCalls;
		
		AircraftData(float[] vData, List<DrawMethod> drawCalls) {
			this.vertexData = vData;
			this.drawCalls = drawCalls;
		}
	}
	
	//Class Body
	//private FloatArray vertexData = null;
	private List<DrawMethod> drawCalls = null;
	
	//public AircraftData build() {
		//FloatArray vData = vertexData;
		//List<DrawMethod> dCalls = drawCalls;
		
		//Allocate new data storage if appending->building again
		//vertexData = null;
		//drawCalls = null;
		
		//return new AircraftData(vData.asArray(), dCalls);
	//}
	
	public void appendData(float[] vData, DrawMethod dCall) {
		//if (vertexData == null)
			//vertexData = new FloatArray();
		if (drawCalls == null)
			drawCalls = new ArrayList<DrawMethod>();
		
		//vertexData.appendArray(vData);
		drawCalls.add(dCall);
	}
	
	public abstract T createFuselage(int numPoints);
	
	public abstract T appendCowl(int numPoints);
	
	public abstract T appendCockpit(int numPoints);
	
	public abstract T appendFrontWings(int numPoints);
	
	public abstract T appendBackWings(int numPoints);
	
	public abstract T appendFin(int numPoints);
	
	public abstract T appendExhaust(int numPoints);
}
