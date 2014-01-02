package com.flightsimulator.container;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BYTE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_SHORT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.flightsimulator.container.GLArray.BufferType;

public class GLVertexBuffer {
	private static final String TAG = "GLVertexBuffer";
	
	private int bufferId;
	private BufferType bufferType;
	
	public GLVertexBuffer(GLArray array) {
		bufferId = genVertexBuffer(array);
		bufferType = array.getBufferType();
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
        glBufferData(GL_ARRAY_BUFFER, array.getSizeInBytes(), array.getBuffer(), GL_STATIC_DRAW);
        //Note: We do not need our GLArray anymore because the gpu has allocated memory to
        //hold our data from the glBufferData() call
        
        //Unbind from the vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        return buffer[0];
	}
	
	public void setVertexAttribPointer(int dataOffset, int attribLocation,
			int numComponents, int stride) {
		glBindBuffer(GL_ARRAY_BUFFER, bufferId);
		
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
}
