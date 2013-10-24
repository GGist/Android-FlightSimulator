package com.andrewmiller.flightsimulator.container;

public class FloatArray {
	
	private float[] array = null;
	private final int defSize = 50, 
					  resizeFactor = 2;
	private int index = 0;
	
	public FloatArray() {
		array = new float[defSize];
	}
	
	public FloatArray(int size) {
		if (size > 0)
			array = new float[size];
		else
			array = new float[defSize];
	}
	
	public void appendData(float data) {
		if (index + 1 == array.length) {
			resize(resizeFactor);
		}
		
		array[index++] = data;
	}
	
	public void appendArray(float[] secondArray) {
		if (secondArray.length > array.length) {
			resize((secondArray.length / array.length) + 1);
		}
		
		for (int i = 0; i < secondArray.length; ++i) {
			array[index++] = secondArray[i];
		}
	}
	
	public float[] asArray() {
		return array;
	}
	
	private void resize(int factor) {
		float[] newArray = new float[array.length * factor];
		
		for (int i = 0; i < array.length; ++i) {
			newArray[i] = array[i];
		}
		
		array = newArray;
	}
}
