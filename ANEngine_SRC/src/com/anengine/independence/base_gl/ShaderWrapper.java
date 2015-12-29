package com.anengine.independence.base_gl;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

import com.anengine.independence.base_data.ANE_Buffer;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_file.FileSystem;

//Shader管理
public class ShaderWrapper {
	
	//加载着色器
	public static int loadShader(int shaderType,String shaderCode)
	{
		int shader_id = GLES20.glCreateShader(shaderType);
		if(shader_id!=0)
		{
			GLES20.glShaderSource(shader_id, shaderCode);
			GLES20.glCompileShader(shader_id);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader_id, GLES20.GL_COMPILE_STATUS, compiled, 0);
			//编译失败
			if(compiled[0]==0)
			{
				String shaderT = "";
				if(shaderType==GLES20.GL_VERTEX_SHADER)
					shaderT = "VertextShader";
				else if(shaderType==GLES20.GL_FRAGMENT_SHADER);
					shaderT = "FragmentShader";
				Log.e(Constant.ES20_ERROR_TAG,"Could not compile shader "+shaderType+"("+shaderT+"):"+GLES20.glGetShaderInfoLog(shader_id));
				GLES20.glDeleteShader(shader_id);
				shader_id = 0;
			}
		}
		return shader_id;
	}
	
	//创建着色器程序
	public static int createProgram(String vertexSource,String fragmentSource)
	{
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
		if(vertexShader==0)
		{
			return 0;
		}
		
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
		if(fragmentShader==0)
		{
			return 0;
		}
		
		int program = GLES20.glCreateProgram();
		if(program!=0)
		{
			GLES20.glAttachShader(program, vertexShader);
			checkError("glAttachShader");
			GLES20.glAttachShader(program, fragmentShader);
			checkError("glAttachShader");
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if(linkStatus[0]!=GLES20.GL_TRUE)
			{
				Log.e(Constant.ES20_ERROR_TAG,"Could not link program: "+GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}
	
	//错误检测
	public static void checkError(String op)
	{
		int error;
		while((error=GLES20.glGetError())!=GLES20.GL_NO_ERROR)
		{
			Log.e(Constant.ES20_ERROR_TAG,op+":glError "+error);
			throw new RuntimeException(op+":glError "+error);
		}
	}
	
	//初始化着色器
	//return program id
	public static int initShader(String vertexShaderFileName,String fragmentShaderFileName,ContextResources ctx)
	{
		int program = createProgram(FileSystem.loadFileAsString(vertexShaderFileName, ctx),FileSystem.loadFileAsString(fragmentShaderFileName, ctx));
		return program;
	}
	
	//声明着色器attribute变量
	public static int glGetAttribLocation(int program,String variableName)
	{
		return GLES20.glGetAttribLocation(program, variableName);
	}
	
	//声明着色器uniform变量
	public static int glGetUniformLocation(int program,String variableName)
	{
		return GLES20.glGetUniformLocation(program, variableName);
	}
	
	//传送4*4矩阵至渲染管线
	public static void glUniformMatrix4fv(int varid, int count, boolean transpose, float[] arg3, int arg4)
	{
		GLES20.glUniformMatrix4fv(varid, count, transpose, arg3, arg4);
	}
	
	public static void glUniform3fv(int varid,int count,float[] vec,int offset)
	{
		GLES20.glUniform3fv(varid, count, vec, offset);
	}
	
	public static void glUniform2fv(int varid,int count,float[] vec,int offset)
	{
		GLES20.glUniform2fv(varid, count, vec, offset);
	}
	
	public static void glUniform3fv(int varid,int count,ANE_Buffer buffer)
	{
		GLES20.glUniform3fv(varid, count, (FloatBuffer)buffer.buffer);
	}
	
	public static void glUniform1fv(int varid,int count,float[] vec,int offset)
	{
		GLES20.glUniform1fv(varid, count, vec, offset);
	}
	
	public static void glUniform1f(int varid,float value)
	{
		GLES20.glUniform1f(varid, value);
	}
	
	public static void glUniform1i(int varid,int value)
	{
		GLES20.glUniform1i(varid, value);
	}
	
	public static void glUniform4fv(int varid,int count,float[] vec,int offset)
	{
		GLES20.glUniform4fv(varid, count, vec, offset);
	}
	
	//传送顶点至渲染管线
	public static void glVertexAttribPointer(int varid, int size, int type, boolean normalized, int stride, Buffer ptr)
	{
		GLES20.glVertexAttribPointer(varid, size, type, normalized, stride, ptr);
	}
}
