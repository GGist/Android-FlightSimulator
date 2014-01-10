package com.flightsimulator.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import com.flightsimulator.R;
import com.flightsimulator.aircraft.F16Aircraft;
import com.flightsimulator.container.GLArray;
import com.flightsimulator.container.GLVertexArray;
import com.flightsimulator.container.GLVertexBuffer;
import com.flightsimulator.shaders.ColorShader;
import com.flightsimulator.utility.MatrixHelper;
import com.flightsimulator.world.TerrainGenerator;

public class SimulatorRenderer implements Renderer {
	private static final String TAG = "SimulatorRenderer";
	private final Context context;
	
	private ColorShader program;
	private int uColorLocation;
	private final float[] projMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	private final float[][] terrain;
	private final float[] coordArray;
	F16Aircraft myAircraft;
	
	private float xRotation, yRotation;
	
	private GLVertexArray myArray;
	
	private GLVertexBuffer myBuffer;
	private int size;
	//
	SimulatorRenderer(Context context) {
		this.context = context;
		//ModelLoader cube = new ModelLoader(context, R.raw.f16model);
		//myArray = new GLVertexArray(new GLArray(cube.getVertexArray()));
//
		terrain = TerrainGenerator.genTerrainDS(64, 0.1f, 0, 20);
		size = (terrain.length - 1) * (terrain.length - 1) * 2 * 3 * 3;
		coordArray = new float[size];
		int counter = 0;
		final int factor = 1;
		for (int i = 1; i < terrain.length; ++i) {
			for (int j = 0; j < terrain[i].length - 1; ++j) {
				//First Triangle
				coordArray[counter++] = j / factor;
				coordArray[counter++] = terrain[i][j];
				coordArray[counter++] = -(i / factor);
				
				coordArray[counter++] = (j + 1) / factor;
				coordArray[counter++] = terrain[i][j + 1];
				coordArray[counter++] = -(i / factor);
				
				coordArray[counter++] = j / factor;
				coordArray[counter++] = terrain[i - 1][j];
				coordArray[counter++] = -((i - 1) / factor);
				//Second Triangle
				coordArray[counter++] = (j + 1) / factor;
				coordArray[counter++] = terrain[i - 1][j + 1];
				coordArray[counter++] = -((i - 1) / factor);
				
				coordArray[counter++] = (j + 1) / factor;
				coordArray[counter++] = terrain[i][j + 1];
				coordArray[counter++] = -(i / factor);
				
				coordArray[counter++] = j / factor;
				coordArray[counter++] = terrain[i - 1][j];
				coordArray[counter++] = -((i - 1) / factor);
			}
			
		}
	}
	
	public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }       
    }
	
	//Starting Order: onSurfaceCreated -> onSurfaceChanged -> onDrawFrame -> ...
	//
	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		setLookAtM(viewMatrix, 0, 10f, 5f, 0f, 0f, 0f, 0f, 0f, 1f, 0f);
		rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
		
		final float[] temp = new float[16];
				
		multiplyMM(temp, 0, projMatrix, 0, viewMatrix, 0);
		multiplyMM(viewMatrix, 0, temp, 0, modelMatrix, 0);
		
		//rotateM(projMatrix, 0, 1f, 1f, 0f, 0f);
		
		glUniformMatrix4fv(program.getMatrixUniformLocation(), 1, false, viewMatrix, 0);
		
		
		myBuffer.setVertexAttribPointer(0, program.getPositionAttribLocation(), 3, 0);
		glDrawArrays(GL_TRIANGLES, 0, size / 3);
		//glDrawArrays(GL_TRIANGLES, 0, myArray.getSize() / Constants.NUM_POSITION_COMPONENTS);
		//myAircraft.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
		
		MatrixHelper.perspecitveM(projMatrix,  90f,  (float) width / (float) height, 1f, 1000f);
		scaleM(projMatrix, 0, 0.5f, 0.5f, 0.5f);
		
		setIdentityM(modelMatrix, 0);
		
		//translateM(modelMatrix, 0, 0f, -4f, -3f);
		
		setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0f);
		//System.arraycopy(temp, 0, projMatrix, 0, temp.length);
		
		//glUniformMatrix4fv(program.getMatrixUniformLocation(), 1, false, projMatrix, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		program = new ColorShader(context, R.raw.vertex_shader, R.raw.fragment_shader);
		program.setProgramActive();
		
		//myArray.setVertexAttribPointer(0, program.getPositionAttribLocation(), 3, 0);
		
		//Sets the color for the current draw call. Needs to change if switching between shaders.
		uColorLocation = program.getColorUniformLocation();
		glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 0.0f);
		
		glEnable(GL_DEPTH_TEST);
		
		myBuffer = new GLVertexBuffer(new GLArray(coordArray));
		
		//Sets the positions for the current draw call. Needs to change if switching between shaders.
		//myAircraft.bindData(program);
	}
}
