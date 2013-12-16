package com.flightsimulator.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.flightsimulator.R;
import com.flightsimulator.aircraft.F16Aircraft;
import com.flightsimulator.shaders.ColorShader;
import com.flightsimulator.utility.ModelLoader;

public class SimulatorRenderer implements Renderer {
	private static final String TAG = "SimulatorRenderer";
	private final Context context;
	
	private ColorShader program;
	private int uColorLocation;
	F16Aircraft myAircraft;
	
	SimulatorRenderer(Context context) {
		this.context = context;

		myAircraft = new F16Aircraft();	
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT);

		myAircraft.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		ModelLoader aa = new ModelLoader(context, R.raw.mybox);
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		program = new ColorShader(context, R.raw.vertex_shader, R.raw.fragment_shader);
		program.setProgramActive();
		
		//Sets the color for the current draw call. Needs to change if switching between shaders.
		uColorLocation = program.getColorUniformLocation();
		glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 0.0f);
		
		//Sets the positions for the current draw call. Needs to change if switching between shaders.
		myAircraft.bindData(program);
	}
}
