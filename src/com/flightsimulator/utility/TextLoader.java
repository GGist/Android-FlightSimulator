package com.flightsimulator.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextLoader {
	private static final String TAG = "TextLoader";
	
	public static String getTextFromFile(Context context, int fileId) {
		StringBuilder fileContents = new StringBuilder();
		
		//Setup reading objects
		InputStream input = context.getResources().openRawResource(fileId);
		InputStreamReader inputReader = new InputStreamReader(input);
		BufferedReader bufferReader = new BufferedReader(inputReader);
		
		try {
			while (bufferReader.ready()) {
				fileContents.append(bufferReader.readLine());
			}
		} catch (IOException e) {
			throw new RuntimeException(TAG +
					": Error Reading Text Resource");
		} catch (Resources.NotFoundException n) {
			throw new RuntimeException(TAG +
					": Could Not Find Resource ID " + fileId);
		}
		
		return fileContents.toString();
	}
}
