package com.flightsimulator.container;

import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.util.Log;

import com.flightsimulator.utility.Constants;
import com.flightsimulator.utility.LoggerStatus;

public class GLShortArray implements GLArray {
	private final String TAG = "GLShortArray";

	private ShortBuffer buffer;
	
	public GLShortArray(short[] data) {
		buffer = ShortBuffer.wrap(data);
	}
	
	public GLShortArray(int size) {
		buffer = allocateMemory(size);
	}
	
	public void writeData(int index, short data) {
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
		 
		glVertexAttribPointer(attribLocation, numComponents, GL_SHORT, 
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
		return GLArray.BufferType.SHORT;
	}

	@Override
	public int getNumElements() {
		return buffer.capacity();
	}

	@Override
	public int bytesPerElement() {
		return Constants.BYTES_PER_SHORT;
	}
	
	private ShortBuffer allocateMemory(int size) {
		ShortBuffer newMemory = null;
		
		try {
			newMemory = ByteBuffer.allocateDirect(size * Constants.BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder())
					.asShortBuffer();
		} catch (OutOfMemoryError o) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Could not allocate new memory of size " + size);
			
			throw o;
		}
		
		return newMemory;
	}
}
