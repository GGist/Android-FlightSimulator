package com.andrewmiller.flightsimulator.aircraft;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import com.andrewmiller.flightsimulator.container.GLESArray;
import com.andrewmiller.flightsimulator.shaders.ColorShader;
import com.andrewmiller.flightsimulator.utility.BasicGeometry.Circle;
import com.andrewmiller.flightsimulator.utility.BasicGeometry.Point;
import com.andrewmiller.flightsimulator.utility.AdvancedGeometry;

public class F16Aircraft extends Aircraft<F16Aircraft> {
	
	private final static int NUM_POSITION_COMPONENTS = 3;
	private int vertexOffset = 0;
	private AircraftData myData;
	private AttachmentData assembleInfo;
	private GLESArray vertexData;
	
	public F16Aircraft() {
		assembleInfo = new AttachmentData();
		myData = createFuselage(10)
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

	public F16Aircraft createFuselage(int numPoints) {
		float[] cockpitData = AdvancedGeometry.gen2DCircle(new Circle(new Point(0, 0, 0), 1f), numPoints);
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
	
	public F16Aircraft appendCowl(int numPoints) {

		return this;
	}

	public F16Aircraft appendCockpit(int numPoints) {
		
		return this;
	}

	public F16Aircraft appendFrontWings(int numPoints) {

		return this;
	}

	public F16Aircraft appendBackWings(int numPoints) {

		return this;
	}

	public F16Aircraft appendFin(int numPoints) {

		return this;
	}

	public F16Aircraft appendExhaust(int numPoints) {

		return this;
	}
}
