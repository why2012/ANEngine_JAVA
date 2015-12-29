package com.anengine.core;

import android.util.Log;

import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;

public class Config {
	public float[] backgroundcolor = new float[]{0,0,0,1.0f};
	public boolean enableDepthTest = true;
	public boolean enableCullFace = true;
	//卷绕方向
	public int frontFace = Constant.GL_CCW;//1逆时针,2顺时针
	
	public float[] viewPort = new float[]{0,0,720,1280};//视口 x,y,width,height
	public float viewRatio = .5f;
	public ContextResources ctx_src = null;
	
	public Config(){}
	
	public void startup() throws Exception
	{
		//OpenGL绘制配置
		GLWrapper.glClearColor(backgroundcolor[0], backgroundcolor[1], backgroundcolor[2], backgroundcolor[3]);
		int glEnable = 0;
		if(enableDepthTest)
			glEnable |= Constant.GL_DEPTH_TEST;
		GLWrapper.glEnable(glEnable);
		
		//资源配置
		if(ctx_src==null)
		{
			Log.e(Constant.ANENGINE_ERROR_TAG,"ctx_src must not be null");
			throw new Exception("ctx_src must not be null");
		}
		GLWrapper.startup(ctx_src);
	}
}
