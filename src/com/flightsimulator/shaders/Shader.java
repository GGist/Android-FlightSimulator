package com.flightsimulator.shaders;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import android.content.Context;
import android.util.Log;

import com.flightsimulator.utility.LoggerStatus;
import com.flightsimulator.utility.TextLoader;

public class Shader {
	private static final String TAG = "Shader";
	
	protected final int program;
	private int vertexShaderId;
	private int fragmentShaderId;
	
	public Shader(Context context, int vertexSourceId, int fragmentSourceId) {
		program = buildProgram(TextLoader.getTextFromFile(context, vertexSourceId), 
				TextLoader.getTextFromFile(context, fragmentSourceId));
	}

	public void setProgramActive() {
		glUseProgram(program);
	}
	
	private final int buildProgram(String vertexSource, String fragmentSource) {
		final int programId;
		
		//Get the vertex and fragment ids from compilation
		vertexShaderId = compileShader(vertexSource, GL_VERTEX_SHADER);
		fragmentShaderId = compileShader(fragmentSource, GL_FRAGMENT_SHADER);
		
		//Link the compiled vertex and fragment shaders
		programId = linkShaders(vertexShaderId, fragmentShaderId);
		
		return programId;
	}
	
	private int compileShader(String source, int shaderType) {
		//Create new shader object
        final int shaderId = glCreateShader(shaderType);
        
        //Verify successful creation
        if (shaderId == 0) {
            if (LoggerStatus.ON)
                Log.w(TAG, "Could not create new shader.");
            
            return 0;
        }
        
        //Pass in source and compile it
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        //Check compilation status
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        
        //Log the compile results
        if (LoggerStatus.ON) {
            Log.v(TAG, "Results of compiling source:" + "\n" + source
                + "\n:" + glGetShaderInfoLog(shaderId));
        }

        //Check if the compilation results were valid
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            if (LoggerStatus.ON)
                Log.w(TAG, "Compilation of shader failed.");

            return 0;
        }

        return shaderId;
	}

	private int linkShaders(int vertexId, int fragmentId) {
		//Create new program object
        final int programId = glCreateProgram();

        //Verify successful creation
        if (programId == 0) {
            if (LoggerStatus.ON)
                Log.w(TAG, "Could not create new program");
            
            return 0;
        }
        
        //Pass in vertex and fragment shader ids
        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);

        //Link the shaders
        glLinkProgram(programId);

        //Check link status
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        
        //Log link results
        if (LoggerStatus.ON) {
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programId));
        }

        //Check if the link results were valid
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            if (LoggerStatus.ON)
                Log.w(TAG, "Linking of program failed.");

            return 0;
        }

        return programId;
	}
}
