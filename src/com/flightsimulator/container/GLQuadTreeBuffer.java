package com.flightsimulator.container;

import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import android.util.Log;

import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.MathHelper;

public class GLQuadTreeBuffer {
	private static final String TAG = "QuadTree";
	
	private class Node {
		public Vec2<Integer> bottomLeft;
		public int dim;
		
		public short[] indices = new short[4];
		
		public Node first, second, third, fourth;
	}
	
	private static final int LEVEL_QUALITY_DIFF = 4, LEVEL_DISTANCE_DIFF = 2, 
			NUM_POSITIONS_COMPONENTS = 3, INDICES_PER_NODE = 6;
	private static final float MORPH_AREA = 0.15f;
	
	private final GLVertexBuffer vertexData;
	private final GLIndexBuffer indexData;
	
	private final int numLevels;
	private final Node root;
	
	private int currLevel, numIndices;

	//Documented Hack: Treating the short indices in each node as unsigned shorts. This will support
	//terrain data up to 128 x 128. If higher terrain is required, we can not simply use ints as GLES
	//only supports UNSIGNED_SHORTS or UNSIGNED_BYTE as indices. We must instead use multiple buffers.
	public GLQuadTreeBuffer(float[][] heightData) {
		//Check data requirements
		boolean goodData = MathHelper.isPowerOfTwo(heightData.length - 1);
		for (int i = 0; i < heightData.length; ++i) {
			goodData = goodData && (heightData.length == heightData[i].length);
		}
		if (!goodData) {
			if (LoggerStatus.ON)
				Log.e(TAG, "The heightData[][] passed in can not be used to create a QuadTree. Make sure it "
						+ "has length (power of two - 1) and has the same length as all of its sub-arrays.");
			
			throw new RuntimeException("The heightData[][] passed in can not be used to create a QuadTree. Make sure it "
					+ "has length (power of two - 1) and has the same length as all of its sub-arrays.");
		}
		
		//Build the data structure
		int numQuads = (heightData.length - 1) * (heightData.length - 1);
		numLevels = (int) (Math.log(numQuads) / Math.log(LEVEL_QUALITY_DIFF)) + 1;
		currLevel = numLevels - 1;
		numIndices = numQuads * INDICES_PER_NODE;
		
		root = buildTree(heightData);
		
		//Allocate vertex buffer
		int currIndex = 0;
		GLFloatArray vertices = new GLFloatArray(heightData.length * heightData.length * NUM_POSITIONS_COMPONENTS);
		//float[] vertices = new float[heightData.length * heightData.length * NUM_POSITIONS_COMPONENTS];
		for (int i = 0; i < heightData.length; ++i) {
			for (int j = 0; j < heightData[i].length; ++j) {
				vertices.writeData(currIndex++, j);
				vertices.writeData(currIndex++, heightData[i][j]);
				vertices.writeData(currIndex++, -i);
			}
		}
		vertexData = new GLVertexBuffer(vertices, GL_STATIC_DRAW);
		
		//Allocate index buffer
		short[] data = new short[numIndices];
		
		levelTraversal(data, new Ref<Integer>(0), 0, numLevels - 1, root);

		indexData = new GLIndexBuffer(new GLShortArray(data), GL_DYNAMIC_DRAW);
	}
	
	public GLVertexBuffer testMethod() {
		return vertexData;
	}


	public void bindBuffer() {
		vertexData.bindBuffer();
		indexData.bindBuffer();
	}


	public void unbindBuffer() {
		vertexData.unbindBuffer();
		indexData.unbindBuffer();
	}


	public void freeBuffer() {
		vertexData.freeBuffer();
		indexData.freeBuffer();
	}


	public int getIndexCount() {
		return numIndices;
	}
	
	public void updateTree(int treeLevel) {
		if (treeLevel == currLevel || treeLevel > numLevels - 1 || treeLevel < 0)
			return;
		
		currLevel = treeLevel;
		numIndices = (int) (Math.pow(LEVEL_QUALITY_DIFF, treeLevel) * INDICES_PER_NODE);
		
		short[] data = new short[numIndices];
		
		levelTraversal(data, new Ref<Integer>(0), 0, treeLevel, root);
		
		indexData.updateBuffer(new GLShortArray(data));
	}
	
	private void levelTraversal(final short[] data, Ref<Integer> index, int currLevel, final int targetLevel, final Node node) {
		if (currLevel != targetLevel) {
			levelTraversal(data, index, currLevel + 1, targetLevel, node.first);
			levelTraversal(data, index, currLevel + 1, targetLevel, node.second);
			levelTraversal(data, index, currLevel + 1, targetLevel, node.third);
			levelTraversal(data, index, currLevel + 1, targetLevel, node.fourth);
		} else {
			data[index.val++] = node.indices[0];
			data[index.val++] = node.indices[1];
			data[index.val++] = node.indices[2];
			
			data[index.val++] = node.indices[2];
			data[index.val++] = node.indices[1];
			data[index.val++] = node.indices[3];
		}
	}
	
	//Coordinate indices are stored so that 1 is the upper left, 2 the bottom left, 3 the 
	//upper right, and 4 the bottom right. To draw two front facing triangles, draw 123 then 324
	private Node buildTree(float[][] heightData) {
		Node node = new Node();
		
		node.dim = heightData.length - 1;
		
		node.bottomLeft = new Vec2<Integer>(0, 0);
		
		node.indices[0] = (short) getLinearIndex(heightData.length, node.bottomLeft.x, node.bottomLeft.y + node.dim);
		node.indices[1] = (short) getLinearIndex(heightData.length, node.bottomLeft.x, node.bottomLeft.y);
		node.indices[2] = (short) getLinearIndex(heightData.length, node.bottomLeft.x + node.dim, node.bottomLeft.y + node.dim);
		node.indices[3] = (short) getLinearIndex(heightData.length, node.bottomLeft.x + node.dim, node.bottomLeft.y);
		
		node.first = buildSubTree(node, heightData.length, 1, node.bottomLeft.x, node.bottomLeft.y + node.dim / 2);
		node.second = buildSubTree(node, heightData.length, 1, node.bottomLeft.x + node.dim / 2, node.bottomLeft.y + node.dim / 2);
		node.third = buildSubTree(node, heightData.length, 1, node.bottomLeft.x, node.bottomLeft.y);
		node.fourth = buildSubTree(node, heightData.length, 1, node.bottomLeft.x + node.dim / 2, node.bottomLeft.y);
		
		return node;
	}
	
	private Node buildSubTree(final Node parent, final int dim, final int currLevel, final int xIndex, final int yIndex) {
		if (currLevel >= numLevels)
			return null;
		
		Node node = new Node();
		
		node.dim = parent.dim / 2;
		
		node.bottomLeft = new Vec2<Integer>(xIndex, yIndex);
		
		node.indices[0] = (short) getLinearIndex(dim, node.bottomLeft.x, node.bottomLeft.y + node.dim);
		node.indices[1] = (short) getLinearIndex(dim, node.bottomLeft.x, node.bottomLeft.y);
		node.indices[2] = (short) getLinearIndex(dim, node.bottomLeft.x + node.dim, node.bottomLeft.y + node.dim);
		node.indices[3] = (short) getLinearIndex(dim, node.bottomLeft.x + node.dim, node.bottomLeft.y);
		
		node.first = buildSubTree(node, dim, currLevel + 1, node.bottomLeft.x, node.bottomLeft.y + node.dim / 2);
		node.second = buildSubTree(node, dim, currLevel + 1, node.bottomLeft.x + node.dim / 2, node.bottomLeft.y + node.dim / 2);
		node.third = buildSubTree(node, dim, currLevel + 1, node.bottomLeft.x, node.bottomLeft.y);
		node.fourth = buildSubTree(node, dim, currLevel + 1, node.bottomLeft.x + node.dim / 2, node.bottomLeft.y);
		
		return node;
	}
	
	/*
	private Node combineNodes(Node one, Node two) {
		
	}
	*/
	private int getLinearIndex(int dim, int x, int y) {
		return (dim * y) + x;
	}
}