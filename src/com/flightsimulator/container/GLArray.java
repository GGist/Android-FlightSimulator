package com.flightsimulator.container;

import java.nio.Buffer;

public interface GLArray {
	public enum BufferType {
		BYTE,
		SHORT,
		FLOAT,
	};
	
	public abstract void setVertexAttribPointer(int dataOffset, int attribLocation, 
			int numComponents, int stride);
	
	public abstract Buffer getBuffer();
	
	public abstract BufferType getBufferType();
	
	public abstract int getNumElements();
	
	public abstract int bytesPerElement();
}
