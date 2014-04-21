package com.flightsimulator.world;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import android.util.Log;

import com.flightsimulator.container.Vec.Vec3;
import com.flightsimulator.ui.widget.Controllable;
import com.flightsimulator.ui.widget.Task;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.MatrixHelper;

public class Camera implements Controllable {
	private static final String TAG = "Camera";
	
	private float[] projMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	
	private Vec3<Float> eye = new Vec3<Float>(0f, 0f, 5f), lookAt = new Vec3<Float>(0f, 0f, 0f), up = new Vec3<Float>(0f, 1f, 0f);
	private float degree = 0;
	
	public Camera(int screenWidth, int screenHeight) {
		MatrixHelper.perspecitveM(projMatrix, 90f, (float) screenWidth / (float) screenHeight, 0.1f, 10000f);
	}
	
	public float[] getMatrix() {
		float[] temp = new float[16];
		
		setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
		rotateM(viewMatrix, 0, degree, 0f, 1f, 0f);
		
		float[] model = new float[16];
		setIdentityM(model, 0);
		scaleM(model, 0, 2f, 1f, 2f);
		multiplyMM(model, 0, projMatrix, 0, model, 0);
		
		multiplyMM(temp, 0, model, 0, viewMatrix, 0);
		
		
		return temp;
	}
	
	@Override
	public void handleJoystick(Task task, float horiz, float vert) {
		// TODO Auto-generated method stub
		switch (task)
		{
		case MOVEMENT_CARDINAL:
			eye.x += 0.5f * horiz;
			lookAt.x += 0.5f * horiz;
			eye.y += 0.5f * vert;
			lookAt.y += 0.5f * vert;
			break;
		case MOVEMENT_HEIGHT:
			eye.z += 0.5f * vert;
			lookAt.z += 0.5f * vert;
			degree += 0.5f * horiz;
			break;
			default:
				if (LoggerStatus.ON)
					Log.v(TAG, "Task not handled: " + task.name());
		}
	}

	@Override
	public void handleButton(Task task, boolean on) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSlider(Task task, float sliderPos) {
		// TODO Auto-generated method stub
		
	}

}
