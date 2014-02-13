package com.flightsimulator.android;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setLookAtM;
import android.util.Log;

import com.flightsimulator.container.Vec.Vec3;
import com.flightsimulator.ui.Controllable;
import com.flightsimulator.ui.Task;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.MatrixHelper;

public class Camera implements Controllable {
	private static final String TAG = "Camera";
	
	private float[] projMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	
	private Vec3<Float> eye = new Vec3<Float>(0f, 0f, 5f), lookAt = new Vec3<Float>(0f, 0f, 0f), up = new Vec3<Float>(0f, 1f, 0f);
	private float degree = 0;
	
	public Camera(int screenWidth, int screenHeight) {
		MatrixHelper.perspecitveM(projMatrix, 90f, (float) screenWidth / (float) screenHeight, 0.1f, 10f);
	}
	
	public float[] getMatrix() {
		float[] temp = new float[16];
		
		setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
		rotateM(viewMatrix, 0, degree, 0f, 1f, 0f);
		
		multiplyMM(temp, 0, projMatrix, 0, viewMatrix, 0);
		
		return temp;
	}
	
	@Override
	public void handleJoystick(Task task, float horiz, float vert) {
		// TODO Auto-generated method stub
		switch (task)
		{
		case MOVEMENT_CARDINAL:
			eye.x += 0.05f * horiz;
			//lookAt.x += 0.05f * horiz;
			eye.y += 0.05f * vert;
			//lookAt.y += 0.05f * vert;
			break;
		case MOVEMENT_HEIGHT:
			eye.z += 0.05f * vert;
			lookAt.z += 0.05f * vert;
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
