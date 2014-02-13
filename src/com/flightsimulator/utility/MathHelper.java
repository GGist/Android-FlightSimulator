package com.flightsimulator.utility;

public class MathHelper {
	public static boolean isPowerOfTwo(int num) {
		return (num & (num - 1)) == 0;
	}
}
