package com.flightsimulator.container;

public interface GLBuffer {
	public abstract void bindBuffer();
	
	public abstract void unbindBuffer();
	
	public abstract void freeBuffer();
	
	public abstract int getBufferSize();
}
