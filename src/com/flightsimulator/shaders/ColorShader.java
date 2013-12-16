package com.flightsimulator.shaders;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import android.content.Context;

public class ColorShader extends Shader {
	private static final String TAG = "ColorShader";
	
	//Uniforms
	private final int uColorLocation;
	
	//Attributes
	private final int aPositionLocation;
	
	public ColorShader(Context context, int vertexSourceId, int fragmentSourceId) {
		super(context, vertexSourceId, fragmentSourceId);
		
        uColorLocation = glGetUniformLocation(program, "u_Color");
		aPositionLocation = glGetAttribLocation(program, "a_Position");
	}
	
	public int getPositionAttribLocation() {
		return aPositionLocation;
	}
	
	public int getColorUniformLocation() {
		return uColorLocation;
	}
}
