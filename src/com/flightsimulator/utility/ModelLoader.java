package com.flightsimulator.utility;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.flightsimulator.android.Constants;
import com.flightsimulator.container.Vec.Vec2;
import com.flightsimulator.container.Vec.Vec3;

public class ModelLoader {
	private static final String TAG = "ModelLoader";
	private static final int POSITION_COMPONENTS = Constants.NUM_POSITION_COMPONENTS;
	private String contents;
	
	private ArrayList<Integer> vertIndices = new ArrayList<Integer>(), uvIndices = new ArrayList<Integer>(), 
			normIndices = new ArrayList<Integer>();
	private ArrayList<Vec3<Float>> tempVert = new ArrayList<Vec3<Float>>(), tempNorm = new ArrayList<Vec3<Float>>();
	private ArrayList<Vec2<Float>> tempUV = new ArrayList<Vec2<Float>>();
	
	public ModelLoader(Context context, int fileId) {
		contents = TextLoader.getTextFromFile(context, fileId);
		int startIndex = contents.indexOf('v', contents.lastIndexOf('#'));

		if (!parseFile(startIndex) && LoggerStatus.ON) {
			Log.e(TAG, "Error parsing OBJ file: " + contents.substring(0, startIndex));
		}
		
		vertIndices.trimToSize();
		uvIndices.trimToSize();
		normIndices.trimToSize();
		tempVert.trimToSize();
		tempNorm.trimToSize();
		tempUV.trimToSize();
		
		
		
		System.out.println("Vertices:");
		for (int i = 0; i < tempVert.size(); ++i) {
			System.out.println(tempVert.get(i).x + " " + tempVert.get(i).y + " " + tempVert.get(i).z);
		}
		
		System.out.println("Textures:");
		for (int i = 0; i < tempUV.size(); ++i) {
			System.out.println(tempUV.get(i).x + " " + tempUV.get(i).y);
		}
		
		System.out.println("Normals:");
		for (int i = 0; i < tempNorm.size(); ++i) {
			System.out.println(tempNorm.get(i).x + " " + tempNorm.get(i).y + " " + tempNorm.get(i).z);
		}
		
		System.out.println("V/T/N Faces:");
		for (int i = 0; i < vertIndices.size(); ++i) {
			System.out.println(vertIndices.get(i).longValue() + "//" + normIndices.get(i).longValue());
		}
	}
	
	public float[] getVertexArray() {
		float[] vertices = new float[vertIndices.size() * POSITION_COMPONENTS];
		Vec3<Float> temp;
		int index = 0;
		
		for (int i = 0; i < vertIndices.size() * 3; i += 3) {
			temp = tempVert.get(vertIndices.get(index) - 1);
			vertices[i] = temp.x;
			vertices[i + 1] = temp.y;
			vertices[i + 2] = temp.z;
			++index;
		}
		
		return vertices;
	}
	
	//Returns false if character at start is not valid
	private boolean parseFile(int start) {

		while (start < contents.length()) {
			if (contents.charAt(start) == 'v') {
				++start;
				if (contents.charAt(start) == ' ') {
					start = parseNumbers('v', start + 1);
				} else if (contents.charAt(start) == 't') {
					start = parseNumbers('t', start + 2);
				} else if (contents.charAt(start) == 'n') {
					start = parseNumbers('n', start + 2);
				}
				
				//Invalid type in parseNumbers()
				if (start == -1)
					return false;
			} else if (contents.charAt(start) == 'f') {
				start = parseNumbers('f', start + 2);
			} else {
				++start;
			}
		}
		
		return true;
	}
	
	//Returns starting index of next tag
	private int parseNumbers(char type, int index) {
		int maxElements, numElements = 0;
		StringBuilder currNum = new StringBuilder();
		
		if (type == 'v' || type == 'n' || type == 'f')
			maxElements = 3;
		else if (type == 't')
			maxElements = 2;
		else
			return -1;
		
		//To account for ++index below
		--index;
		while (numElements < maxElements) {
			++index;
			if (contents.charAt(index) == '/') {
				++index;
				currNum.append('/');
				//In case textures were not used, // will appear in OBJ file
				if (contents.charAt(index) == '/') {
					++index;
					currNum.append('/');
				}
			}
			
			if (contents.charAt(index) >= '-' && contents.charAt(index) <= '9') {
				currNum.append(contents.charAt(index));
			} else {
				//Encountered a space, start of new tag, or garbage
				if (currNum.length() != 0) {
					appendNumber(type, currNum.toString());
					++numElements;
					currNum.setLength(0);
				}
			}
		}
		
		return index;
	}
	
	//Returns false if type was invalid
	private boolean appendNumber(char type, String data) {
		System.out.println(data);
		float vertice;
		
		switch (type)
		{
		case 'v':
			if (tempVert.isEmpty())
				tempVert.add(new Vec3<Float>());
			Vec3<Float> tempV = tempVert.get(tempVert.size() - 1);
			vertice = Float.parseFloat(data);
			if (tempV.x == null) {
				tempV.x = vertice;
			} else if (tempV.y == null) {
				tempV.y = vertice;
			} else if (tempV.z == null) {
				tempV.z = vertice;
			} else {
				tempV = new Vec3<Float>();
				tempV.x = vertice;
				tempVert.add(tempV);
			}
			break;
		case 't':
			if (tempUV.isEmpty())
				tempUV.add(new Vec2<Float>());
			Vec2<Float> tempT = tempUV.get(tempUV.size() - 1);
			vertice = Float.parseFloat(data);
			if (tempT.x == null) {
				tempT.x = vertice;
			} else if (tempT.y == null) {
				tempT.y = vertice;
			} else {
				tempT = new Vec2<Float>();
				tempT.x = vertice;
				tempUV.add(tempT);
			}
		break;
		case 'n':
			if (tempNorm.isEmpty())
				tempNorm.add(new Vec3<Float>());
			Vec3<Float> tempN = tempNorm.get(tempNorm.size() - 1);
			vertice = Float.parseFloat(data);
			if (tempN.x == null) {
				tempN.x = vertice;
			} else if (tempN.y == null) {
				tempN.y = vertice;
			} else if (tempN.z == null) {
				tempN.z = vertice;
			} else {
				tempN = new Vec3<Float>();
				tempN.x = vertice;
				tempNorm.add(tempN);
			}
			break;
		case 'f':
			//Should come in as v/t/n or v//n
			int firstSlash = data.indexOf('/');
			vertIndices.add(Integer.parseInt((data.substring(0, firstSlash))));
			if (data.charAt(firstSlash + 1) != '/') {
				int secondSlash = data.indexOf('/', firstSlash + 1);
				uvIndices.add(Integer.parseInt(data.substring(firstSlash + 1, secondSlash)));
				normIndices.add(Integer.parseInt(data.substring(secondSlash + 1, data.length())));
			} else {
				normIndices.add(Integer.parseInt(data.substring(firstSlash + 2, data.length())));
			}
			break;
		default:
			return false;
		}
		
		return true;
	}
}
