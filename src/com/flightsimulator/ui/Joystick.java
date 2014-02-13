package com.flightsimulator.ui;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;

import com.flightsimulator.android.Constants;
import com.flightsimulator.container.GLArray;
import com.flightsimulator.container.GLVertexBuffer;
import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.shaders.TextureShader;
import com.flightsimulator.utility.AdvancedGeometry;
import com.flightsimulator.utility.BasicGeometry.Circle;
import com.flightsimulator.utility.BasicGeometry.Point;

public class Joystick extends Control {
	private final static Circle textureOutline = new Circle(new Point(0.5f, 0.5f, 0f), 0.5f);
	private final static float HANDLE_SCALE = 0.5f;
	private final static int POINTS_PER_PRIM = 100;
	
	private Circle base;
	private Vec2<Float> handlePos;
	
	private final GLVertexBuffer baseData, handleData, textureData;
	private final int baseTextureId, handleTextureId;
	private final float[] handleModelMatrix = new float[16], 
            baseModelMatrix = new float[16];

	public Joystick(Task task, Controllable object, Circle base, int baseTextureId, int handleTextureId) {
		super(task, object);
		
		this.base = base;
		this.handlePos = new Vec2<Float>(base.center.x, base.center.y);
		this.baseTextureId = baseTextureId;
		this.handleTextureId = handleTextureId;
		
		setIdentityM(baseModelMatrix, 0);
		setIdentityM(handleModelMatrix, 0);
		
		baseData = new GLVertexBuffer(new GLArray(AdvancedGeometry.gen2DCircleFan(base, POINTS_PER_PRIM)));
		Circle handle = new Circle(new Point(base.center.x, base.center.y, base.center.z), base.radius * HANDLE_SCALE);
		handleData = new GLVertexBuffer(new GLArray(AdvancedGeometry.gen2DCircleFan(handle, POINTS_PER_PRIM)));
		textureData = new GLVertexBuffer(new GLArray(AdvancedGeometry.gen2DCircleFan(textureOutline, POINTS_PER_PRIM)));
	}
	
	//Coordinates come in as normalized inverse device coordinates to check against bounds
	//Store them like this until they need to be drawn
	 //When they need to be drawn, index into the projection matrix
	 //and convert them back to normalized coordinates before drawing
	@Override
	public void handleClick(int touchId, float x, float y) {
		Point clickedPoint = new Point(x, y, base.center.z);
		
		//Check within base bounds
		if (base.center.distance(clickedPoint) > base.radius || currTouchId != NO_TOUCH_ID) {
			return;
		}
		currTouchId = touchId;

		//Update handle position
		handlePos.x = x;
		handlePos.y = y;
		
		//Send message to object
		object.handleJoystick(task, normHorizSensitivity(), normVertSensitivity());
	}
	
	@Override
	public void handleDrag(int touchId, float deltaX, float deltaY) {
		if (touchId != currTouchId) {
			return;
		}
		
		//Update handle position
		Point newPoint = new Point(handlePos.x + deltaX, handlePos.y + deltaY, base.center.z);
		if (base.center.distance(newPoint) > base.radius) {
			double theta = Math.atan2((newPoint.y - base.center.y), (newPoint.x - base.center.x));

			handlePos.x = (float) (base.radius * Math.cos(theta)) + base.center.x;
			handlePos.y = (float) (base.radius * Math.sin(theta)) + base.center.y;
		} else {
			handlePos.x += deltaX;
			handlePos.y += deltaY;
		}
		
		//Send message to object
		object.handleJoystick(task, normHorizSensitivity(), normVertSensitivity());
	}

	@Override
	public void handleRelease(int touchId, float x, float y) {
		if (touchId != currTouchId) {
			return;
		}
		currTouchId = NO_TOUCH_ID;
			
		//Update handle position
		handlePos.x = base.center.x;
		handlePos.y = base.center.y;
	}

	//Assuming Depth Buffer Uses GL_LEQUAL
	@Override
	public void draw(TextureShader program, float[] orthoMatrix) {
		float[] tempMatrix = new float[16];
		
		program.setProgramActive();
		textureData.setVertexAttribPointer(0, program.getTextureCoordinateLocation(), Constants.NUM_POSITION_COMPONENTS_2D, 0);
		
		//Base Drawing
		multiplyMM(tempMatrix, 0, baseModelMatrix, 0, orthoMatrix, 0);
		
		baseData.setVertexAttribPointer(0, program.getPositionAttribLocation(), Constants.NUM_POSITION_COMPONENTS_2D, 0);
		program.setUniforms(tempMatrix, baseTextureId);
		glDrawArrays(GL_TRIANGLE_FAN, 0, baseData.getSize() / Constants.NUM_POSITION_COMPONENTS_2D);
		
		//Handle Drawing
		normHandleTranslation(orthoMatrix);
		multiplyMM(tempMatrix, 0, handleModelMatrix, 0, orthoMatrix, 0);
		
		handleData.setVertexAttribPointer(0, program.getPositionAttribLocation(), Constants.NUM_POSITION_COMPONENTS_2D, 0);
		program.setUniforms(tempMatrix, handleTextureId);
		glDrawArrays(GL_TRIANGLE_FAN, 0, handleData.getSize() / Constants.NUM_POSITION_COMPONENTS_2D);
	
	}
	
	private float normHorizSensitivity() {
		return (handlePos.x - base.center.x) / base.radius;
	}
	
	private float normVertSensitivity() {
		return (handlePos.y - base.center.y) / base.radius;
	}
	
	//Convert inverse normalized device coordinates back to normalized device coordinates to draw correctly
	private void normHandleTranslation(float[] orthoMatrix) {
		handleModelMatrix[12] = (handlePos.x - base.center.x) * orthoMatrix[0];
		handleModelMatrix[13] = (handlePos.y - base.center.y) * orthoMatrix[5];
	}
}
