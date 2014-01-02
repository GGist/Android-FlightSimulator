package com.flightsimulator.container;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import android.util.Log;

import com.flightsimulator.container.GLArray.BufferType;
import com.flightsimulator.utility.LoggerStatus;

public class GLIndexBuffer {
	private static final String TAG = "GLIndexBuffer";
	
	private final int bufferId;
	
	public GLIndexBuffer(GLArray array) {
		bufferId = genIndexBuffer(array);
	}
	
	private int genIndexBuffer(GLArray array) {
		//Allocate vertex buffer
		final int buffer[] = new int[1];
		glGenBuffers(buffer.length, buffer, 0);
		if (buffer[0] == 0)
			throw new RuntimeException("Could not create a new index buffer object.");
		
		//Not wise to use a FloatBuffer for this object, issue warning
		if (array.getBufferType() == BufferType.FLOAT && LoggerStatus.ON)
			Log.w(TAG, "FloatBuffer being used to construct a GLIndexBuffer may cause issues.");
		
		//Bind to the allocated vertex buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer[0]);

		// Transfer data
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, array.getSizeInBytes(), array.getBuffer(), GL_STATIC_DRAW);
        //Note: We do not need our GLArray anymore because the gpu has allocated memory to
        //hold our data from the glBufferData() call

		// Unbind from the vertex buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		return buffer[0];
	}
	
	public int getBufferId() {
        return bufferId;
    }
}
