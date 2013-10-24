package com.andrewmiller.flightsimulator.utility;

import com.andrewmiller.flightsimulator.android.Constants;
import com.andrewmiller.flightsimulator.utility.Geometry.Circle;

public abstract class GeometryHelper {
	private static final String TAG = "ShapeGenerator";
	
	private final static int NUM_POSITION_COMPONENTS = Constants.NUM_POSITION_COMPONENTS;
	
	public static float[] gen2DCircle(Circle base, int numPoints) {
		float[] vertexData = new float[getNumCircleVertices(numPoints) * NUM_POSITION_COMPONENTS];
		int vertexOffset = 0;

		vertexData[vertexOffset++] = base.center.x;
		vertexData[vertexOffset++] = base.center.y;
		vertexData[vertexOffset++] = base.center.z;
		
		for (int i = 0; i <= numPoints; ++i) {
			float angleInRadians = ((float) i / (float) numPoints)
	                * ((float) Math.PI * 2f);
			vertexData[vertexOffset++] = (float) (base.center.x + base.radius * 
					Math.cos(angleInRadians));
			vertexData[vertexOffset++] = (float) (base.center.y + base.radius *
					Math.sin(angleInRadians));
			vertexData[vertexOffset++] = base.center.z;
		}
		
		return vertexData;
	}
	
	private static int getNumCircleVertices(int numPoints) {
		return numPoints + 2;
	}

}
