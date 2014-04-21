package com.flightsimulator.shader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import android.content.Context;

public class TextureShader extends Shader {
	private static final String TAG = "ColorShader";
	
	//Uniforms
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	
	//Attributes
	private final int aPositionLocation;
	private final int aTextureCoordinateLocation;
	
	public TextureShader(Context context, int vertexSourceId, int fragmentSourceId) {
		super(context, vertexSourceId, fragmentSourceId);
		
		uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit");
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aTextureCoordinateLocation = glGetAttribLocation(program, "a_TextureCoordinates");
	}
	
	public void setUniforms(float[] matrix, int textureId) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		
		//Set texture to texture unit 0
		glActiveTexture(GL_TEXTURE0);
		
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		//Shader will read from texture unit 0
		glUniform1i(uTextureUnitLocation, 0);
	}
	
	public int getPositionAttribLocation() {
		return aPositionLocation;
	}
	
	public int getTextureCoordinateLocation() {
		return aTextureCoordinateLocation;
	}
}
