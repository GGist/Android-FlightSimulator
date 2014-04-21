package com.flightsimulator.container;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import android.util.Log;

import com.flightsimulator.utility.LoggerStatus;

public class GLVertexBuffer implements GLBuffer {
	private static final String TAG = "GLVertexBuffer";
	
	private int usage, id, numVertices;
	private GLArray.BufferType bufferType;
	
	//bufferUsage is either GL_STREAM_DRAW, GL_STATIC_DRAW, or GL_DYNAMIC_DRAW
	public GLVertexBuffer(GLArray array, int bufferUsage) {
		usage = bufferUsage;
		id = genVertexBuffer(array);
		numVertices = array.getNumElements();
		bufferType = array.getBufferType();
	}
	
	//As per the OpenGL standard, buffers should only be updated if
	//DYNAMIC_DRAW was specified as the usage pattern
	public void updateBuffer(GLArray data) {
		bindBuffer();
		
		glBufferSubData(GL_ARRAY_BUFFER, 0, 
				data.getNumElements() * data.bytesPerElement(), data.getBuffer());
		
		unbindBuffer();
	}

	@Override
	public void bindBuffer() {
		glBindBuffer(GL_ARRAY_BUFFER, id);
	}

	@Override
	public void unbindBuffer() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void freeBuffer() {
		glDeleteBuffers(1, new int[] { id }, 0);
		id = usage = numVertices = 0;
	}

	@Override
	public int getBufferSize() {
		return numVertices;
	}

	public void setVertexAttribPointer(int dataOffset, int attribLocation,
			int numComponents, int stride) {
		if (id == 0) {
			if (LoggerStatus.ON)
				Log.e(TAG, "Buffer was never allocated or has already been delete.");
			
			return;
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, id);
		
		switch (bufferType)
		{
		case BYTE:
			glVertexAttribPointer(attribLocation, numComponents, GL_BYTE,
					false, stride, dataOffset);
		case SHORT:
			glVertexAttribPointer(attribLocation, numComponents, GL_SHORT,
					false, stride, dataOffset);
			break;
		case FLOAT:
			glVertexAttribPointer(attribLocation, numComponents, GL_FLOAT,
					false, stride, dataOffset);
			break;
		}
		
		glEnableVertexAttribArray(attribLocation);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private int genVertexBuffer(GLArray array) {
		//Allocate vertex buffer
		final int buffer[] = new int[1];
		
        glGenBuffers(buffer.length, buffer, 0);
        if (buffer[0] == 0)
            throw new RuntimeException("Could not create a new vertex buffer object.");
        
        //Bind to the allocated vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, buffer[0]);
        
        //Transfer data
        glBufferData(GL_ARRAY_BUFFER, array.getNumElements() * array.bytesPerElement(), array.getBuffer(), usage);
        
        //Unbind from the vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        return buffer[0];
	}
}
