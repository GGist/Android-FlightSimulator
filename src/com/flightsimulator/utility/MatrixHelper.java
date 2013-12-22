package com.flightsimulator.utility;

public class MatrixHelper {
	public static void perspecitveM(float[] m, float angleDegree, float aspect, float near, float far) {
		float scale = (float) Math.tan(angleDegree / 2 * (Math.PI / 180)) * near;
		float right = aspect * scale, left = -right,
			  top = scale, bottom = -top;
		//final float angleInRadians = (float) (angleDegree * Math.PI / 180.0);
		//final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
		
		m[0] = 2 * near / (right - left);
		m[1] = 0f;
		m[2] = 0f;
		m[3] = 0f;
		
		m[4] = 0f;
		m[5] = 2 * near / (top - bottom);
		m[6] = 0f;
		m[7] = 0f;
		
		m[8] = (right + left) / (right - left);
		m[9] = (top + bottom) / (top - bottom);
		m[10] = -((far + near) / (far - near));
		m[11] = -1f;
		
		m[12] = 0f;
		m[13] = 0f;
		m[14] = -((2f * far * near) / (far - near));
		m[15] = 0f;
	}
}
