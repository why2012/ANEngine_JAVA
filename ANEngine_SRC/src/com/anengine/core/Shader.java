package com.anengine.core;

import java.util.HashMap;

import android.util.Log;

import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;

/*
 * 统一着色器管理
 */
public class Shader {

	public int program = 0;//着色器程序
	public static int PATH = 0;
	public static int CODE = 1;
	private static HashMap<String,Shader> path_program = new HashMap<String,Shader>();
	
	public static Shader getOne(int mode,String vertexShaderCodeStringOrPath,String fragmentShaderCodeStringOrPath)
	{
		if(mode==CODE)
			return new Shader(mode,vertexShaderCodeStringOrPath,fragmentShaderCodeStringOrPath);
		else
		{
			String path = vertexShaderCodeStringOrPath.trim()+fragmentShaderCodeStringOrPath.trim();
			if(path_program.containsKey(path))
				return path_program.get(path);
			else
			{
				Shader shader_r = new Shader(mode,vertexShaderCodeStringOrPath,fragmentShaderCodeStringOrPath);
				path_program.put(path, shader_r);
				return shader_r;
			}
		}
	}
	
	/*
	 * mode:初始化着色器方式
	 * 		PATH:着色器路径
	 * 		CODE:着色器代码
	 */
	private Shader(int mode,String vertexShaderCodeStringOrPath,String fragmentShaderCodeStringOrPath)
	{
		System.out.println("Shader constructor has been called");
		if(mode==PATH)
		{
			program = GLWrapper.initShader(vertexShaderCodeStringOrPath, fragmentShaderCodeStringOrPath);
		}
		else if(mode==CODE)
		{
			program = ShaderWrapper.createProgram(vertexShaderCodeStringOrPath, fragmentShaderCodeStringOrPath);
		}
		else
		{
			Log.e(Constant.ANENGINE_ERROR_TAG,"Wrong shader init mode,must be PATH or CODE");
		}
	}
	
	public Shader(int program)
	{
		this.program = program;
	}
	
	public Shader clone()
	{
		return new Shader(program);
	}
}
