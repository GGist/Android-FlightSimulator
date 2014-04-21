package com.flightsimulator.utility;

public class MathHelper {
	public static boolean isPowerOfTwo(int num) {
		return (num != 0) && ((num & (num - 1)) == 0);
	}
	
	public static boolean isEven(int num) {
		return num % 2 == 0 && num != 0;
	}
	
	public static boolean isOdd(int num) {
		return num % 2 != 0 && num != 0;
	}
}
