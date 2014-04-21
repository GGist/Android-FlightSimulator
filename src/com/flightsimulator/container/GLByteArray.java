package com.flightsimulator.container;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import android.util.Log;

import com.flightsimulator.utility.Constants;
import com.flightsimulator.utility.LoggerStatus;

public class GLByteArray implements GLArray {
	private final String TAG = "GLByteArray";
	
	private final ByteBuffer buffer;
	
	public GLByteArray(byte[] data) {
		buffer = ByteBuffer.wrap(data);
	}
	
	public GLByteArray(int size) {
		buffer = allocateMemory(size);
	}
	
	public void writeData(int index, byte data) {
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
		 
		glVertexAttribPointer(attribLocation, numComponents, GL_BYTE, 
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
		return GLArray.BufferType.BYTE;
	}

	@Override
	public int getNumElements() {
		return buffer.capacity();
	}

	@Override
	public int bytesPerElement() {
		return Constants.BYTES_PER_BYTE;
	}
	
	private ByteBuffer allocateMemory(int size) {
		ByteBuffer newMemory = null;
		
		try {
			newMemory = ByteBuffer.allocateDirect(size )
					.order(ByteOrder.nativeOrder());
		} catch (OutOfMemoryError o) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Could not allocate new memory of size " + size);
			
			throw o;
		}
		
		return newMemory;
	}
}
