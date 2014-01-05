package com.flightsimulator.aircraft;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

import com.flightsimulator.container.GLArray;
import com.flightsimulator.container.GLVertexArray;
import com.flightsimulator.shaders.ColorShader;

public class F16Aircraft extends Aircraft<F16Aircraft> {
	
	private final static int NUM_POSITION_COMPONENTS = 3;
	private int vertexOffset = 0;
	private AircraftData myData;
	private AttachmentData assembleInfo;
	private GLVertexArray vertexData;
	
	public F16Aircraft() {
		assembleInfo = new AttachmentData();
		myData = createFuselage(3)
				.build();
		vertexData = new GLVertexArray(new GLArray(myData.vertexData));
	}
	
    public void bindData(ColorShader colorShader) {
        vertexData.setVertexAttribPointer(0,
        colorShader.getPositionAttribLocation(),
        NUM_POSITION_COMPONENTS, 0);
    }
	
	public void draw() {
		for (DrawMethod drawCall : myData.drawCalls) {
			drawCall.draw();
		}
	}

	@Override
	public F16Aircraft createFuselage(int numPoints) {
		float[] array = new float[3];
		array[0] = -1;
		array[1] = -1;
		array[2] = -1;
		/*
		array[3] = 1;
		array[4] = -1;
		array[5] = 1;
		
		array[6] = -1;
		array[7] = -1;
		array[8] = 1;
		
		array[9] = -1;
		array[10] = -1;
		array[11] = -1;
		
		array[12] = 1;
		array[13] = 1;
		array[14] = -1;
		
		array[15] = 1;
		array[16] = 1;
		array[17] = 1;
		
		array[18] = -1;
		array[19] = 1;
		array[20] = 1;
		
		array[21] = -1;
		array[22] = 1;
		array[23] = -1;
		*/
		final int numVertices = (array.length / NUM_POSITION_COMPONENTS);
		final int vertOffset = vertexOffset;
		vertexOffset += array.length;
		appendData(array, new DrawMethod() {
			@Override
			public void draw() {
				glDrawArrays(GL_POINTS, vertOffset, numVertices);
			}
		});
		/*float[] cockpitData = AdvancedGeometry.gen2DCircle(new Circle(new Point(0, 0, 0), 1f), numPoints);
		final int numVertices = (cockpitData.length / NUM_POSITION_COMPONENTS);
		final int vertOffset = vertexOffset;
		vertexOffset += cockpitData.length;
		
		appendData(cockpitData, new DrawMethod() {
			public void draw() {
				glDrawArrays(GL_TRIANGLE_FAN, vertOffset, numVertices);
			}
		});
		*/
		return this;
	}
	
	@Override
	public F16Aircraft appendCowl(int numPoints) {

		return this;
	}

	@Override
	public F16Aircraft appendCockpit(int numPoints) {
		
		return this;
	}

	@Override
	public F16Aircraft appendFrontWings(int numPoints) {

		return this;
	}

	@Override
	public F16Aircraft appendBackWings(int numPoints) {

		return this;
	}

	@Override
	public F16Aircraft appendFin(int numPoints) {

		return this;
	}

	@Override
	public F16Aircraft appendExhaust(int numPoints) {

		return this;
	}
}
