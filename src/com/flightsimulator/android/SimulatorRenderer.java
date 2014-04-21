package com.flightsimulator.android;

import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.flightsimulator.R;
import com.flightsimulator.container.GLQuadTreeBuffer;
import com.flightsimulator.container.GLVertexBuffer;
import com.flightsimulator.shader.ColorShader;
import com.flightsimulator.shader.TextureShader;
import com.flightsimulator.ui.SimulatorUI;
import com.flightsimulator.ui.widget.Control;
import com.flightsimulator.ui.widget.Joystick;
import com.flightsimulator.ui.widget.Task;
import com.flightsimulator.utility.BasicGeometry.Circle;
import com.flightsimulator.utility.BasicGeometry.Point;
import com.flightsimulator.utility.Constants;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.MathHelper;
import com.flightsimulator.utility.TextureLoader;
import com.flightsimulator.world.Camera;
import com.flightsimulator.world.DiamondSquare;

public class SimulatorRenderer implements Renderer {
	private static final String TAG = "SimulatorRenderer";
	private final Context context;
	
	private ColorShader colorProgram;
	private TextureShader textureProgram;

	private GLVertexBuffer model;
	private GLQuadTreeBuffer tree;
	
	private SimulatorUI myUI;
	private Camera camera;
	
	private int currLevel = 7;
	
	//FPS
    private long startTime, totalTime;
    private int counter;
    
	SimulatorRenderer(Context context) {
		this.context = context;
	}
	
	//Do not bleed over too much android specific code
	public void handleTouch(int maskedAction, int touchId, float x, float y) {
		
		switch (maskedAction)
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if (currLevel == 0)
				currLevel = 7;
			else
				--currLevel;
			
			tree.updateTree(currLevel);
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
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		myUI.draw(textureProgram);
		
		colorProgram.setProgramActive();
		float[] matrix = camera.getMatrix();

		colorProgram.setUniform(matrix);
		glUniform4f(colorProgram.getColorUniformLocation(), 0.0f, 1.0f, 1.0f, 0.0f);
		
		glEnableVertexAttribArray(colorProgram.getPositionAttribLocation());
		tree.testMethod().setVertexAttribPointer(0, colorProgram.getPositionAttribLocation(), Constants.NUM_POSITION_COMPONENTS, 0);
		tree.bindBuffer();
		glDrawElements(GL_TRIANGLES, tree.getIndexCount(), GL_UNSIGNED_SHORT, 0);
		
		//model.setVertexAttribPointer(0, colorProgram.getPositionAttribLocation(), Constants.NUM_POSITION_COMPONENTS, 0);
		//glDrawArrays(GL_TRIANGLES, 0, model.getSize() / Constants.NUM_POSITION_COMPONENTS);
		
		if (LoggerStatus.ON)
			fps();
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
		
		myUI.reportDimensions(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_CULL_FACE);
		glFrontFace(GL_CCW);

		float[][] testData = new DiamondSquare(128, 0.8f, -100, 100).generateTerrain();
		
		tree = new GLQuadTreeBuffer(testData);
		
		//ModelLoader myLoader = new ModelLoader(context, R.raw.f16model);
		//model = new GLVertexBuffer(new GLArray(myLoader.getVertexArray()));
		
		DisplayMetrics screen = context.getResources().getDisplayMetrics();
		
		camera = new Camera(screen.widthPixels, screen.heightPixels);
		Joystick myJoystick = new Joystick(Task.MOVEMENT_CARDINAL, camera, new Circle(new Point(-1.2f, -0.6f, 0f), 0.2f), TextureLoader.loadTexture(context, R.drawable.flatdark06), TextureLoader.loadTexture(context, R.drawable.flatdark00));
		Joystick aJoystick = new Joystick(Task.MOVEMENT_HEIGHT, camera, new Circle(new Point(1.2f, -0.6f, 0f), 0.2f), TextureLoader.loadTexture(context, R.drawable.flatdark06), TextureLoader.loadTexture(context, R.drawable.flatdark00));
		
		Control[] controls = new Control[2];
		controls[0] = myJoystick;
		controls[1] = aJoystick;

		myUI = new SimulatorUI(screen.widthPixels, screen.heightPixels, controls);
		
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
	}
}
