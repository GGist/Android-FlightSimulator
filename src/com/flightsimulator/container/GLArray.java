package com.flightsimulator.container;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.flightsimulator.android.Constants;

public class GLArray {
	private static final String TAG = "GLArray";

	public enum BufferType {
		BYTE,
		SHORT,
		FLOAT,
	};
	
	private final Buffer data;
	private final int size;
	private final BufferType bufferType;
	
	public GLArray(byte[] array) {
		size = array.length;
		data = allocateMemory(size, Constants.BYTES_PER_SHORT)
				.put(array)
				.position(0);
		bufferType = BufferType.BYTE;
	}
	
	public GLArray(short[] array) {
		size = array.length;
		data = allocateMemory(size, Constants.BYTES_PER_SHORT)
				.asShortBuffer()
				.put(array)
				.position(0);
		bufferType = BufferType.SHORT;
	}
	
	public GLArray(float[] array) {
		size = array.length;
		data = allocateMemory(size, Constants.BYTES_PER_FLOAT)
				.asFloatBuffer()
				.put(array)
				.position(0);
		bufferType = BufferType.FLOAT;
	}
	
	//Shallow Copy
	public GLArray(GLArray array) {
		size = array.size;
		data = array.data;
		bufferType = array.bufferType;
	}
	
	public void setPosition(int position) {
		data.position(position);
	}
	
	public Buffer getBuffer() {
		return data;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getSizeInBytes() {
		switch (bufferType)
		{
		case BYTE:
			return getSize() * Constants.BYTES_PER_BYTE;
		case SHORT:
			return getSize() * Constants.BYTES_PER_SHORT;
		case FLOAT:
			return getSize() * Constants.BYTES_PER_FLOAT;
		default:
			return -1;
		}
	}
	
	public BufferType getBufferType() {
		return bufferType;
	}
	
	private ByteBuffer allocateMemory(int size, int bytesPerType) {
		ByteBuffer newMemory = null;
		
		try {
			newMemory = ByteBuffer.allocateDirect(size * bytesPerType)
					.order(ByteOrder.nativeOrder());
		} catch (OutOfMemoryError o) {
			Log.w(TAG, "Could not allocate new memory");
			throw o;
		}
		
		return newMemory;
	}
}
