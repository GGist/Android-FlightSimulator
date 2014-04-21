package com.flightsimulator.world;

import java.util.Random;

import android.util.Log;

import com.flightsimulator.algorithm.TerrainGenerator;
import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.MathHelper;

public class DiamondSquare implements TerrainGenerator {
	public static final String TAG = "TerrainGenerator";
	
	private static final int SQUARES_GENERATED_BASE = 4, SMOOTH_FACTOR_BASE = 2;
	private static final Random rand = new Random();
	
	private int dim;
	private float smoothFactor, minHeight, maxHeight;

	//http://www.gameprogrammer.com/fractal.html#diamond
	
	//Range (maxHeight - minHeight) will be reduced by 2^(-smoothFactor) after each pass
	//Article states squares generated == 2^(i + 2) where i == number of passes,
	//squares generated is actually == 4^(i) Example: (4^3 != 2^5)
	
	public DiamondSquare(final int dim, final float smoothFactor, float minHeight, float maxHeight) {
		this.dim = dim;
		this.smoothFactor = smoothFactor;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}
	
	@Override
	public float[][] generateTerrain() {
		return genTerrainDS(dim, smoothFactor, minHeight, maxHeight);
	}
	
	private static float[][] genTerrainDS(final int dim, final float smoothFactor, float minHeight, float maxHeight) {
		if (!MathHelper.isPowerOfTwo(dim)) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Dimension specified for terrain is not a power of two!");
			
			throw new RuntimeException("Dimension specified for terrain is not a power of two!");
		}	
		
		final double H = 1 / Math.pow(SMOOTH_FACTOR_BASE, smoothFactor);
		final int NUM_PASSES = (int) (Math.log(dim * dim) / Math.log(SQUARES_GENERATED_BASE));
		final float MAX_HEIGHT = maxHeight, MIN_HEIGHT = minHeight;
		
		float[][] terrainData = new float[dim + 1][dim + 1];
		
		//Seed corners
		terrainData[0][0] = terrainData[0][dim] = terrainData[dim][0] = terrainData[dim][dim] = 0;
		
		//Diamond-Square Iterations
		for (int i = 0; i < NUM_PASSES; ++i) {
			//Diamond Step First Pass: 1 Second Pass: 4 Third Pass: 16 Fourth Pass: 64
		    int numSquares = (int) Math.pow(SQUARES_GENERATED_BASE, i),
		    	subSquareDim = (int) (numSquares <= 1 ? dim : dim / Math.sqrt(numSquares)); //Don't do numSquares / 2 if numSquares == 1 because width / 0;
		    
		    for (int j = 0; j < numSquares; ++j) {
		    	int currSquare = numSquares - j;
		    	Vec2<Integer> sCoord = getSquareTopLeftCoord(dim, numSquares, currSquare, subSquareDim);
		    	
		    	float average = terrainData[sCoord.x][sCoord.y];
		    	average += terrainData[sCoord.x][sCoord.y + subSquareDim];
		    	average += terrainData[sCoord.x + subSquareDim][sCoord.y];
		    	average += terrainData[sCoord.x + subSquareDim][sCoord.y + subSquareDim];
		    	average /= 4;
		    	average += (rand.nextFloat() * (maxHeight - minHeight + 1)) + minHeight;

		    	//Clamp Value
		    	if (average > MAX_HEIGHT)
		    		average = MAX_HEIGHT;
		    	else if (average < MIN_HEIGHT)
		    		average = MIN_HEIGHT;
		    		
		    	terrainData[sCoord.x + (subSquareDim / 2)][sCoord.y + (subSquareDim / 2)] = average;
		    }
		    
			//Square Step First Pass: 2 Second Pass: 8 Third Pass: 32 Fourth Pass: 128
		    int numDiamonds = numSquares * 2,
		    	subDiamondDim = (int) Math.sqrt((dim * dim) / (numDiamonds * 2));

		    for (int j = 0; j < numDiamonds; ++j) {
		    	int currDiamond = numDiamonds - j;
		    	Vec2<Integer> dCoord = getDiamondCenterCoord(dim, numDiamonds, currDiamond, subDiamondDim);
		    	
		    	float average = dataFromNormalizedIndex(terrainData, dim, dCoord.x - subDiamondDim, dCoord.y);
		    	average += dataFromNormalizedIndex(terrainData, dim, dCoord.x, dCoord.y - subDiamondDim);
		    	average += dataFromNormalizedIndex(terrainData, dim, dCoord.x + subDiamondDim, dCoord.y);
		    	average += dataFromNormalizedIndex(terrainData, dim, dCoord.x, dCoord.y + subDiamondDim);
		    	average /= 4;
		    	average += (rand.nextFloat() * (maxHeight - minHeight + 1)) + minHeight;

		    	//Clamp Value
		    	if (average > MAX_HEIGHT)
		    		average = MAX_HEIGHT;
		    	else if (average < MIN_HEIGHT)
		    		average = MIN_HEIGHT;

		    	terrainData[dCoord.x][dCoord.y] = average;
		    	mirrorEdgeData(terrainData, dim, average, dCoord.x, dCoord.y);
		    }

		    //Reduce Range
		    minHeight *= H;
		    maxHeight *= H;
		}

		return terrainData;
	}
	
	private static Vec2<Integer> getSquareTopLeftCoord(final int dim, final int numSquares, final int currSquare, final int subSquareDim) {
		Vec2<Integer> coord = new Vec2<Integer>();
    	int squaresPerRow = dim / subSquareDim,
    	    xPos, 
    	    yPos;
    	
    	if (currSquare > squaresPerRow) {
    		xPos = (currSquare - 1) % squaresPerRow * subSquareDim;
    		yPos = (currSquare - 1) / squaresPerRow * subSquareDim;
    	} else {
    		xPos = (currSquare - 1) * subSquareDim;
    		yPos = 0;
    	}
    	
    	coord.x = xPos;
    	coord.y = yPos;
    	
    	return coord;
	}
	
	private static Vec2<Integer> getDiamondCenterCoord(final int dim, final int numDiamonds, final int currDiamond, final int subDiamondDim) {
		Vec2<Integer> coord = new Vec2<Integer>();
		int diamondsPerRow = (dim / (subDiamondDim * 2)) * 2,
			xPos,
			yPos;
		
		if (currDiamond > diamondsPerRow) {
			xPos = ((currDiamond - 1) % diamondsPerRow) * subDiamondDim;
			if (currDiamond % 2 == 0) {
				yPos = (currDiamond - 1) / diamondsPerRow * subDiamondDim * 2;
			} else {
				yPos = currDiamond / diamondsPerRow * subDiamondDim * 2 + subDiamondDim;
			}
		} else {
			xPos = (currDiamond - 1) * subDiamondDim;
			if (currDiamond % 2 == 0) {
				yPos = 0;
			} else {
				yPos = subDiamondDim;
			}
		}
		
		coord.x = xPos;
		coord.y = yPos;
		
		return coord;
	}
	
	private static float dataFromNormalizedIndex(float[][] terrainData, int dim, int x, int y) {
		if (x < 0)
			x = dim + x; //x is negative

		if (y < 0)
			y = dim + y; //y is negative
		
		return terrainData[x][y];
	}
	
	private static void mirrorEdgeData(float[][] terrainData, int dim, float average, int x, int y) {
		if (x == 0 && y == 0)
			terrainData[dim][dim] = average;
		else if (x == 0)
			terrainData[dim][y] = average;
		else if (y == 0)
			terrainData[x][dim] = average;
	}
}
