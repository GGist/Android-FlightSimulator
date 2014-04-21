package com.flightsimulator.container;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

import com.flightsimulator.utility.Constants;
import com.flightsimulator.utility.LoggerStatus;

public class GLFloatArray implements GLArray {
	private final String TAG = "GLFloatArray";
	
	private final FloatBuffer buffer;
	
	public GLFloatArray(float[] data) {
		buffer = FloatBuffer.wrap(data);
	}
	
	public GLFloatArray(int size) {
		buffer = allocateMemory(size);
	}
	
	public void writeData(int index, float data) {
		if (index > buffer.capacity()) {
			throw new ArrayIndexOutOfBoundsException(TAG + ": buffer index of " + 
					index + " is out of bounds.");
		}
		
		buffer.put(index, data);
	}
	
	@Override
	public void setVertexAttribPointer(int dataOffset, int attribLocation,
			int numComponents, int stride) {
		buffer.position(dataOffset);
		 
		glVertexAttribPointer(attribLocation, numComponents, GL_FLOAT, 
				false, stride, buffer);

	    glEnableVertexAttribArray(attribLocation);
	    
	    buffer.position(0);
	}

	@Override
	public Buffer getBuffer() {
		return buffer;
	}

	@Override
	public BufferType getBufferType() {
		return GLArray.BufferType.FLOAT;
	}

	@Override
	public int getNumElements() {
		return buffer.capacity();
	}

	@Override
	public int bytesPerElement() {
		return Constants.BYTES_PER_FLOAT;
	}
	
	private FloatBuffer allocateMemory(int size) {
		FloatBuffer newMemory = null;
		
		try {
			newMemory = ByteBuffer.allocateDirect(size * Constants.BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
		} catch (OutOfMemoryError o) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Could not allocate new memory of size " + size);
			
			throw o;
		}
		
		return newMemory;
	}
}
