package com.flightsimulator.container;

import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_INT;
import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GLVertexArray extends GLArray {
	private static final String TAG = "GLVertexArray";
	
	public GLVertexArray(GLArray array) {
		super(array);
	}

	public void setVertexAttribPointer(int dataOffset, int attribLocation,
			int numComponents, int stride) {
		super.setPosition(dataOffset);
		 
		switch (super.getBufferType())
		{
		case BYTE:
			glVertexAttribPointer(attribLocation, numComponents, GL_BYTE,
					false, stride, super.getBuffer());
		case SHORT:
			glVertexAttribPointer(attribLocation, numComponents, GL_SHORT,
					false, stride, super.getBuffer());
			break;
		case FLOAT:
			glVertexAttribPointer(attribLocation, numComponents, GL_FLOAT,
					false, stride, super.getBuffer());
			break;
		}

	    glEnableVertexAttribArray(attribLocation);
	    
	    super.setPosition(0);
	}
}