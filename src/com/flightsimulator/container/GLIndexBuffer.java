package com.flightsimulator.container;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glGenBuffers;
import android.util.Log;

import com.flightsimulator.utility.LoggerStatus;

public class GLIndexBuffer  implements GLBuffer {
	private static final String TAG = "GLIndexBuffer";
	
	private int usage, id, numIndices;
	
	public GLIndexBuffer(GLArray array, int bufferUsage) {
		usage = bufferUsage;
		id = genIndexBuffer(array);
		numIndices = array.getNumElements();
	}
	
	//As per the OpenGL standard, buffers should only be updated if
	//DYNAMIC_DRAW was specified as the usage pattern
	public void updateBuffer(GLArray data) {
		bindBuffer();
		
		glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, 
				data.getNumElements() * data.bytesPerElement(), data.getBuffer());
		
		unbindBuffer();
	}
	
	@Override
	public void bindBuffer() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
	}

	@Override
	public void unbindBuffer() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void freeBuffer() {
		glDeleteBuffers(1, new int[] { id }, 0);
		id = usage = numIndices = 0;
	}

	@Override
	public int getBufferSize() {
		return numIndices;
	}

	private int genIndexBuffer(GLArray array) {
		//Allocate vertex buffer
		final int buffer[] = new int[1];

		glGenBuffers(buffer.length, buffer, 0);
		if (buffer[0] == 0)
			throw new RuntimeException("Could not create a new index buffer object.");
		
		//Not wise to use a FloatBuffer for this object, issue warning
		if (array.getBufferType() == GLArray.BufferType.FLOAT && LoggerStatus.ON)
			Log.w(TAG, "FloatBuffer being used to construct a GLIndexBuffer may cause issues.");
		
		//Bind to the allocated vertex buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer[0]);

		// Transfer data
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, array.getNumElements() * array.bytesPerElement(), array.getBuffer(), usage);

		// Unbind from the vertex buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		return buffer[0];
	}

}
