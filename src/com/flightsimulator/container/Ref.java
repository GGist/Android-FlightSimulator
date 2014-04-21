package com.flightsimulator.container;

public class Ref<T> {
	public T val;
	
	public Ref() {
		val = null;
	}
	
	public Ref(T value) {
		val = value;
	}
}
