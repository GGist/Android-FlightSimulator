package com.flightsimulator.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
<<<<<<< HEAD
=======
import static android.opengl.GLES20.GL_LEQUAL;
>>>>>>> master
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glViewport;
<<<<<<< HEAD
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
=======
>>>>>>> master

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.flightsimulator.R;
<<<<<<< HEAD
import com.flightsimulator.aircraft.F16Aircraft;
import com.flightsimulator.container.GLArray;
import com.flightsimulator.container.GLVertexArray;
import com.flightsimulator.container.GLVertexBuffer;
import com.flightsimulator.shaders.ColorShader;
import com.flightsimulator.utility.MatrixHelper;
import com.flightsimulator.world.TerrainGenerator;
=======
import com.flightsimulator.container.GLArray;
import com.flightsimulator.container.GLVertexBuffer;
import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.shaders.ColorShader;
import com.flightsimulator.shaders.TextureShader;
import com.flightsimulator.ui.Control;
import com.flightsimulator.ui.Joystick;
import com.flightsimulator.ui.SimulatorUI;
import com.flightsimulator.ui.Task;
import com.flightsimulator.utility.BasicGeometry.Circle;
import com.flightsimulator.utility.BasicGeometry.Point;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.ModelLoader;
import com.flightsimulator.utility.TextureHelper;
>>>>>>> master

public class SimulatorRenderer implements Renderer {
	private static final String TAG = "SimulatorRenderer";
	private final Context context;
	
<<<<<<< HEAD
	private ColorShader program;
	private int uColorLocation;
	private final float[] projMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	private final float[][] terrain;
	private final float[] coordArray;
	F16Aircraft myAircraft;
	
	private float xRotation, yRotation;
=======
	private ColorShader colorProgram;
	private TextureShader textureProgram;

	private GLVertexBuffer model;
	
	private SimulatorUI myUI;
	private Camera camera;
	
	//FPS
    private long startTime, totalTime;
    private int counter;
>>>>>>> master
	
	private GLVertexArray myArray;
	
	private GLVertexBuffer myBuffer;
	private int size;
	//
	SimulatorRenderer(Context context) {
		this.context = context;
<<<<<<< HEAD
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
=======
	}
	
	//Do not bleed over too much android specific code
	public void handleTouch(int maskedAction, int touchId, float x, float y) {
		
		switch (maskedAction)
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			myUI.reportClick(touchId, x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			myUI.reportDrag(touchId, x, y);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			myUI.reportRelease(touchId, x, y);
			break;
			default:
				if (LoggerStatus.ON)
					Log.v(TAG, "A maskedAction Id was not handled in handleTouch()");
		}
	}
	
	//Starting Order: onSurfaceCreated -> onSurfaceChanged -> onDrawFrame -> ... Android can delete
	//GL context if it needs space, onSucfaceCreated could be called more than once during app lifecycle
>>>>>>> master
	
	//Starting Order: onSurfaceCreated -> onSurfaceChanged -> onDrawFrame -> ...
	//
	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
<<<<<<< HEAD
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
=======
		myUI.draw(textureProgram);
		
		colorProgram.setProgramActive();
		colorProgram.setUniform(camera.getMatrix());
		glUniform4f(colorProgram.getColorUniformLocation(), 0.0f, 1.0f, 1.0f, 0.0f);
		model.setVertexAttribPointer(0, colorProgram.getPositionAttribLocation(), Constants.NUM_POSITION_COMPONENTS, 0);
		glDrawArrays(GL_TRIANGLES, 0, model.getSize() / Constants.NUM_POSITION_COMPONENTS);
		
		if (LoggerStatus.ON)
			fps();
>>>>>>> master
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
		
<<<<<<< HEAD
		MatrixHelper.perspecitveM(projMatrix,  90f,  (float) width / (float) height, 1f, 1000f);
		scaleM(projMatrix, 0, 0.5f, 0.5f, 0.5f);
		
		setIdentityM(modelMatrix, 0);
		
		//translateM(modelMatrix, 0, 0f, -4f, -3f);
		
		setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0f);
		//System.arraycopy(temp, 0, projMatrix, 0, temp.length);
		
		//glUniformMatrix4fv(program.getMatrixUniformLocation(), 1, false, projMatrix, 0);
=======
		myUI.reportScreenRotate();
>>>>>>> master
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		
<<<<<<< HEAD
		//myArray.setVertexAttribPointer(0, program.getPositionAttribLocation(), 3, 0);
=======
		ModelLoader myLoader = new ModelLoader(context, R.raw.f16model);
		model = new GLVertexBuffer(new GLArray(myLoader.getVertexArray()));
>>>>>>> master
		
		DisplayMetrics screen = context.getResources().getDisplayMetrics();
		
		camera = new Camera(screen.widthPixels, screen.heightPixels);
		Joystick myJoystick = new Joystick(Task.MOVEMENT_CARDINAL, camera, new Circle(new Point(-1.2f, -0.6f, 0f), 0.2f), TextureHelper.loadTexture(context, R.drawable.flatdark06), TextureHelper.loadTexture(context, R.drawable.flatdark00));
		Joystick aJoystick = new Joystick(Task.MOVEMENT_HEIGHT, camera, new Circle(new Point(1.2f, -0.6f, 0f), 0.2f), TextureHelper.loadTexture(context, R.drawable.flatdark06), TextureHelper.loadTexture(context, R.drawable.flatdark00));
		
		Control[] controls = new Control[2];
		controls[0] = myJoystick;
		controls[1] = aJoystick;
		myUI = new SimulatorUI(new Vec2<Integer>(screen.widthPixels, screen.heightPixels), controls);
		myUI.reportScreenRotate();
		
<<<<<<< HEAD
		myBuffer = new GLVertexBuffer(new GLArray(coordArray));
		
		//Sets the positions for the current draw call. Needs to change if switching between shaders.
		//myAircraft.bindData(program);
=======
		colorProgram = new ColorShader(context, R.raw.vertex_shader, R.raw.fragment_shader);
		textureProgram = new TextureShader(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
	}
	
	private void fps() {
		totalTime += System.nanoTime() - startTime;
        startTime = System.nanoTime();
        ++counter;
        if ((float)totalTime / 1000000000 > 1) {
            System.out.println("FPS: " + counter);
            startTime = System.nanoTime();
            totalTime = 0;
            counter = 0;
        }
>>>>>>> master
	}
}
