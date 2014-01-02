package com.flightsimulator.world;

import java.util.Random;

public class TerrainGenerator {
	public static final String TAG = "TerrainGenerator";
	
	//http://www.gameprogrammer.com/fractal.html#midpoint
	//Diamond-Square Algorithm
	// The diamond step: Taking a square of four points, generate a random value
	// at the square midpoint, where the two diagonals meet. The midpoint value
	// is calculated by averaging the four corner values, plus a random amount.
	// This gives you diamonds when you have multiple squares arranged in a
	// grid.
	// The square step: Taking each diamond of four points, generate a random
	// value at the center of the diamond. Calculate the midpoint value by
	// averaging the corner values, plus a random amount generated in the same
	// range as used for the diamond step. This gives you squares again.
	
	//Free-Roam can utilize heightmaps or DS Algorithm
	
	//Objective should utilize heightmaps
	
	private static final Random rand = new Random();
	
	public static float[][] genTerrain(int height, int width, int smoothFactor, int maxHeight, int minHeight) {
		//2^-H
		float[][] terrainData = new float[height + 1][width + 1];
		final double H = 1 / Math.pow(2, smoothFactor);
		
		//Seed corners
		terrainData[0][0] = terrainData[0][width] = terrainData[height][0] = terrainData[height][width] = rand.nextInt((maxHeight - minHeight) + 1) + minHeight;
		
		//Treat terrainData as having dimensions height * width since the first column will 
		//be mirrored with the last column and the first row will be mirrored with the last row
		
		//Diamond-Square
		for (int i = 0; i < height / 2; ++i) {
			//Diamond Step First Pass: 1 Second Pass: 4
					
			//Square Step

		}
		
		return terrainData;
	}
}
