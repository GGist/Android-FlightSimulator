package com.andrewmiller.flightsimulator.aircraft;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import com.andrewmiller.flightsimulator.container.GLESArray;
import com.andrewmiller.flightsimulator.shaders.ColorShader;
import com.andrewmiller.flightsimulator.utility.Geometry.Circle;
import com.andrewmiller.flightsimulator.utility.Geometry.Point;
import com.andrewmiller.flightsimulator.utility.GeometryHelper;

public class F16Aircraft extends Aircraft<F16Aircraft> {
	private final static int NUM_POSITION_COMPONENTS = 3;
	private AircraftData myData;
	private final GLESArray vertexData;
	private int vertexOffset = 0;
	
	public F16Aircraft() {
		/*
		myData = new AircraftBuilder(100002)
		.appendFuselage(new Circle(new Point(0.5f, 0f, 0f), 0.2f), 100000)
		.build();
		*/
		myData = appendCockpit(100)
				.build();
		vertexData = new GLESArray(myData.vertexData);
	}
	
    public void bindData(ColorShader colorShader) {
        vertexData.setVertexAttribPtr(0,
        colorShader.getPositionAttribLocation(),
        NUM_POSITION_COMPONENTS, 0);
    }
	
	public void draw() {
		for (DrawMethod drawCall : myData.drawCalls) {
			drawCall.draw();
		}
	}

	public F16Aircraft appendCowl(int numPoints) {

		return this;
	}

	public F16Aircraft appendCockpit(int numPoints) {
		float[] cockpitData = GeometryHelper.gen2DCircle(new Circle(new Point(0, 0, 0), 1f), numPoints);
		final int numVertices = (cockpitData.length / NUM_POSITION_COMPONENTS);
		final int vertOffset = vertexOffset;
		vertexOffset += cockpitData.length;
		
		appendData(cockpitData, new DrawMethod() {
			public void draw() {
				glDrawArrays(GL_TRIANGLE_FAN, vertOffset, numVertices);
			}
		});
		
		return this;
	}

	public F16Aircraft appendFuselage(int numPoints) {

		return this;
	}

	public F16Aircraft appendFrontWing(int numPoints) {

		return this;
	}

	public F16Aircraft appendBackWing(int numPoints) {

		return this;
	}

	public F16Aircraft appendFin(int numPoints) {

		return this;
	}

	public F16Aircraft appendExhaust(int numPoints) {

		return this;
	}
}
