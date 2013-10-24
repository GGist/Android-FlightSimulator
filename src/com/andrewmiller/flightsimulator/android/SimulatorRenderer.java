package com.andrewmiller.flightsimulator.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.andrewmiller.flightsimulator.R;
import com.andrewmiller.flightsimulator.aircraft.F16Aircraft;
import com.andrewmiller.flightsimulator.container.GLESArray;
import com.andrewmiller.flightsimulator.shaders.ColorShader;

public class SimulatorRenderer implements Renderer {
	private static final String TAG = "SimulatorRenderer";
	private final Context context;
	
	private ColorShader program;
	private int uColorLocation;
	private int aPositionLocation;
	private GLESArray vertexData;
	F16Aircraft myAircraft;
	
	SimulatorRenderer(Context context) {
		this.context = context;
		System.out.println("h");
		myAircraft = new F16Aircraft();
		/*
		float[] tableV = {	
				// Triangle 1
				-0.5f, 0f,
				0f, -0.5f,
				0.5f, 0f
		};
		
		vertexData = new GLESArray(tableV);
		*/
		/*
		vertexData = ByteBuffer.allocateDirect(tableV.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexData.put(tableV);
		*/
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT);
		
		myAircraft.draw();
		
		//glDrawArrays(GL_TRIANGLES, 0, 3);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		program = new ColorShader(context, R.raw.vertex_shader, R.raw.fragment_shader);
		program.setProgramActive();
		
		//Sets the color for the current draw call. Needs to change if switching between shaders.
		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 0.0f);
		
		//Sets the positions for the current draw call. Needs to change if switching between shaders.
		myAircraft.bindData(program);
		//vertexData.setVertexAttribPtr(0, aPositionLocation, 2, 0);
	}
}
