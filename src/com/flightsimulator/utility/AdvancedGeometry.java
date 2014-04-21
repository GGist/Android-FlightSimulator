package com.flightsimulator.utility;

import com.flightsimulator.utility.BasicGeometry.Circle;
import com.flightsimulator.utility.BasicGeometry.Rectangle;

public abstract class AdvancedGeometry {
	private static final String TAG = "AdvancedGeometry";
	
	private final static int NUM_POSITION_COMPONENTS_2D = Constants.NUM_POSITION_COMPONENTS_2D;
	private final static int MIN_POINTS = 3;
	private final static double TOP_RIGHT_DEG = 45.0, TOP_LEFT_DEG = 135.0, 
			BOTTOM_LEFT_DEG = 225.0, BOTTOM_RIGHT_DEG = 315.0;
	
	public static float[] gen2DCircleFan(Circle base, int numPoints) {
		if (numPoints <= MIN_POINTS)
			throw new RuntimeException("Must pass more than " + MIN_POINTS + " points to gen2DCircleFan.");
		
		float[] vertexData = new float[getNumCircleFanVertices(numPoints) * NUM_POSITION_COMPONENTS_2D];
		int vertexOffset = 0;

		vertexData[vertexOffset++] = base.center.x;
		vertexData[vertexOffset++] = base.center.y;
		
		for (int i = 0; i <= numPoints; ++i) {
			float angleInRadians = ((float) i / (float) numPoints)
	                * ((float) Math.PI * 2f);
			vertexData[vertexOffset++] = (float) (base.center.x + base.radius * 
					Math.cos(angleInRadians));
			vertexData[vertexOffset++] = (float) (base.center.y + base.radius *
					Math.sin(angleInRadians));
		}
		
		return vertexData;
	}
	
	private static int getNumCircleFanVertices(int numPoints) {
		return numPoints + 2;
	}
	
	
	//Must be power of 2
	public static float[] gen2DRectangleFan(Rectangle rect, int numPoints) {
		if (!MathHelper.isPowerOfTwo(numPoints))
			throw new RuntimeException("Must pass a power of two number of points to gen2DRectangleFan.");
		
		float[] vertexData = new float[getNumRectangleFanVertices(numPoints) * NUM_POSITION_COMPONENTS_2D];
		int vertexOffset = 0;
		
		vertexData[vertexOffset++] = rect.center.x;
		vertexData[vertexOffset++] = rect.center.y;
		
		//Move counter clockwise through the fan
		for (int i = numPoints; i >= 0; --i) {
			double angleDegree = i / numPoints * 360.0;
			float x, y;
			
			if (angleDegree <= TOP_RIGHT_DEG && angleDegree >= BOTTOM_RIGHT_DEG) {
				x = rect.width / 2;
				y = (float) (Math.cos(angleDegree) / x);
			} else if (angleDegree <= BOTTOM_RIGHT_DEG && angleDegree >= BOTTOM_LEFT_DEG) {
				y = -(rect.height / 2);
				x = (float) (Math.cos(angleDegree) / y);
			} else if (angleDegree <= BOTTOM_LEFT_DEG && angleDegree >= TOP_LEFT_DEG) {
				x = -(rect.width / 2);
				y = (float) (Math.cos(angleDegree) / x);
			} else {
				y = rect.height / 2;
				x = (float) (Math.cos(angleDegree) / y);
			}
			
			vertexData[vertexOffset++] = x;
			vertexData[vertexOffset++] = y;
		}
		
		return vertexData;
	}
	
	private static int getNumRectangleFanVertices(int numPoints) {
		return numPoints + 2;
	}
	
	//Must be even
	public static float[] gen2DRectangleStrip(Rectangle rect, int numPoints) {
		if (numPoints <= MIN_POINTS)
			throw new RuntimeException("Must pass more than " + MIN_POINTS + " points to gen2DRectangleStrip.");
		else if (numPoints % 2 != 0)
			--numPoints;
		
		float[] vertexData = new float[numPoints * NUM_POSITION_COMPONENTS_2D];
		int numIterations = getNumRectangleStripTriangles(numPoints) / 2;
		int vertexOffset = 0;
		
		//Start from right of strip to get points ordered counter clockwise
		for (int i = numIterations; i >= 0; --i) {
			vertexData[vertexOffset++] = rect.width * (i / numIterations);
			vertexData[vertexOffset++] = 0;
					
			vertexData[vertexOffset++] = rect.width * (i / numIterations);
			vertexData[vertexOffset++] = rect.height;
		}
		
		return vertexData;
	}
	
	private static int getNumRectangleStripTriangles(int numPoints) {
		return numPoints - 2;
	}

}
