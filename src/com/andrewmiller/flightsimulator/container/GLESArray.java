package com.andrewmiller.flightsimulator.container;

import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_INT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

/**
 * This class holds any data that needs to be accessed by 
 * OpenGL such as vertices and color data.
 * @author GGist
 *
 */

public class GLESArray {
	private static final String TAG = "GLESArray";
	
	private static int BYTES_PER_BYTE = 1;
	private static int BYTES_PER_FLOAT = 4;
	private static int BYTES_PER_INT = 4;
	private enum DataType {
		BYTE,
		FLOAT,
		INT
	};
	
	private final Buffer data;
	private final DataType bufferType;
	
	public GLESArray(byte[] array) {
		data = allocateMemory(array.length, BYTES_PER_BYTE)
				.put(array);
		bufferType = DataType.BYTE;
	}
	
	public GLESArray(float[] array) {
		data = allocateMemory(array.length, BYTES_PER_FLOAT)
				.asFloatBuffer()
				.put(array);
		bufferType = DataType.FLOAT;
	}
	
	public GLESArray(int[] array) {
		data = allocateMemory(array.length, BYTES_PER_INT)
				.asIntBuffer()
				.put(array);
		bufferType = DataType.INT;
	}

	public void setVertexAttribPtr(int dataOffset, int attribLocation,
			int numComponents, int stride) {
		data.position(dataOffset);
		
		switch (bufferType)
		{
		case BYTE:
			glVertexAttribPointer(attribLocation, numComponents, GL_BYTE,
					false, stride, data);
			break;
		case FLOAT:
			glVertexAttribPointer(attribLocation, numComponents, GL_FLOAT,
					false, stride, data);
			break;
		case INT:
			glVertexAttribPointer(attribLocation, numComponents, GL_INT,
					false, stride, data);
			break;
		}

	    glEnableVertexAttribArray(attribLocation);
	        
	    data.position(0);
	}
	
	/**
	 * Allocates new memory that is not managed by the JVM
	 * @param size Number of elements in the array
	 * @param bytesPerType Bytes per element in the array
	 * @return newMemory ByteBuffer holding the allocated memory
	 */
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