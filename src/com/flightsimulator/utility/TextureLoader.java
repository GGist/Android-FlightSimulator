package com.flightsimulator.utility;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureLoader {
	public static final String TAG = "TextureHelper";
	
	public static int loadTexture(Context context, int resourceId) {
		final int[] textureObjectId = new int[1];
		glGenTextures(textureObjectId.length, textureObjectId, 0);
		
		if (textureObjectId[0] == 0) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
		
			return 0;
		}
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inScaled = false;
		
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		
		if (bitmap == null) {
			if (LoggerStatus.ON)
				Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
			
			glDeleteTextures(textureObjectId.length, textureObjectId, 0);
			return 0;
		}
		
		glBindTexture(GL_TEXTURE_2D, textureObjectId[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		//Unbind from texture
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return textureObjectId[0];
	}
}
