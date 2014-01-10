package com.flightsimulator.world;

import java.util.Random;

import com.flightsimulator.container.Vec.Vec2;

public class TerrainGenerator {
	public static final String TAG = "TerrainGenerator";
	
	private static final Random rand = new Random();

	//http://www.gameprogrammer.com/fractal.html#diamond
	public static float[][] genTerrainDS(final int dim, final float smoothFactor, float minHeight, float maxHeight) {
		float[][] terrainData = new float[dim + 1][dim + 1];
		//Range (maxHeight - minHeight) will be reduced by 2^(-smoothFactor) after each pass
		final double H = 1 / Math.pow(2, smoothFactor);
		//Article states squares generated == 2^(i + 2) where i == number of passes,
		//squares generated is actually == 4^(i) (4^3 != 2^5)
		final int numPasses = (int) (Math.log(dim * dim) / Math.log(4));
		
		//Seed corners
		terrainData[0][0] = terrainData[0][dim] = terrainData[dim][0] = terrainData[dim][dim] = maxHeight / 2;//rand.nextFloat() * (maxHeight - minHeight) + minHeight;

		//Diamond-Square Iterations
		for (int i = 0; i < numPasses; ++i) {
			//Diamond Step First Pass: 1 Second Pass: 4 Third Pass: 16 Fourth Pass: 64
		    int numSquares = (int) Math.pow(4, i),
		    	subSquareDim = (int) (numSquares <= 1 ? dim : dim / Math.sqrt(numSquares)); //Don't do numSquares / 2 if numSquares == 1 because width / 0;
		    
		    for (int j = 0; j < numSquares; ++j) {
		    	int currSquare = numSquares - j;
		    	Vec2<Integer> sCoord = getSquareTopLeftCoord(dim, numSquares, currSquare, subSquareDim);
		    	
		    	float average = terrainData[sCoord.x][sCoord.y];
		    	average += terrainData[sCoord.x][sCoord.y + subSquareDim];
		    	average += terrainData[sCoord.x + subSquareDim][sCoord.y];
		    	average += terrainData[sCoord.x + subSquareDim][sCoord.y + subSquareDim];
		    	average /= 4;
		    	average += rand.nextFloat() * (maxHeight - minHeight) + minHeight;

		    	//Clamp Value
		    	if (average > maxHeight)
		    		average = maxHeight;
		    	else if (average < minHeight)
		    		average = minHeight;

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
		    	average += rand.nextFloat() * (maxHeight - minHeight) + minHeight;
		    	
		    	//Clamp Value
		    	if (average > maxHeight)
		    		average = maxHeight;
		    	else if (average < minHeight)
		    		average = minHeight;
		    	
		    	terrainData[dCoord.x][dCoord.y] = average;
		    	mirrorEdgeData(terrainData, dim, average, dCoord.x, dCoord.y);
		    }

		    //Reduce Range
		    float midHeight = (maxHeight + minHeight) / 2;
		    minHeight = (float) (midHeight - ((midHeight - minHeight) * H));
		    maxHeight = (float) (midHeight + ((maxHeight - midHeight) * H));
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
		if (x == 0 && y == 0) {
			terrainData[dim][dim] = average;
		} else if (x == 0) {
			terrainData[dim][y] = average;
		} else if (y == 0) {
			terrainData[x][dim] = average;
		}
	}
	
}
