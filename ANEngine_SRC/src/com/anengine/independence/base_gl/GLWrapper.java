package com.anengine.independence.base_gl;

import java.nio.Buffer;

import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_file.ContextResources;

//封装OpenGL ES2.0 的常用操作
public class GLWrapper{
	public static ContextResources ctx_src = null;
	
	//初始化GLWrapper
	public static void startup(ContextResources ctx_src)
	{
		GLWrapper.ctx_src = ctx_src;
	}
	
	//设置着色器
	public static int initShader(String vertexShaderPath,String fragmentShaderPath)
	{
		return ShaderWrapper.initShader(vertexShaderPath, fragmentShaderPath, ctx_src);
	}
	
	//启用顶点数据
	public static void glEnableVertexAttribArray(int varid)
	{
		GLES20.glEnableVertexAttribArray(varid);
	}
	
	//禁用顶点数据
	public static void glDisableVertexAttribArray(int varid)
	{
		GLES20.glDisableVertexAttribArray(varid);
	}
	
	//根据顶点数据绘制
	public static void glDrawArrays(int mode,int first,int count)
	{
		GLES20.glDrawArrays(mode, first, count);
	}
	
	//根据索引数据绘制
	public static void glDrawElements(int mode,int count,int type,Buffer indices)
	{
		GLES20.glDrawElements(mode, count, type, indices);
	}
	
	public static void glClear(int type)
	{
		GLES20.glClear(type);
	}
	
	public static void glClearColor(float r,float g,float b,float a)
	{
		GLES20.glClearColor(r, g, b, a);
	}
	
	public static void glClearDepthf(int type)
	{
		GLES20.glClearDepthf(type);
	}
	
	public static void glClearStencil(int type)
	{
		GLES20.glClearStencil(type);
	}
	
	public static void glEnable(int type)
	{
		GLES20.glEnable(type);
	}
	
	public static void glDisable(int mode)
	{
		GLES20.glDisable(mode);
	}
	
	public static void glFrontFace(int mode)
	{
		GLES20.glFrontFace(mode);
	}
	
	public static void glViewport(int x,int y,int width,int height)
	{
		GLES20.glViewport(x, y, width, height);
	}
	
	public static void glUseProgram(int program)
	{
		GLES20.glUseProgram(program);
	}
	
	public static void glGenTextures(int num, int[] textures, int offset)
	{
		GLES20.glGenTextures(num, textures, offset);
	}
	
	public static void glTexParameterf(int textType,int filterType,int mode)
	{
		GLES20.glTexParameterf(textType, filterType, mode);
	}
	
	public static void glActiveTexture(int index)
	{
		GLES20.glActiveTexture(index);
	}
	
	public static void glBindTexture(int type,int id)
	{
		GLES20.glBindTexture(type, id);
	}
	
	public static void gluTexImage2D(int type,int mode,ANE_Bitmap bitmap,int bordersize)
	{
		GLUtils.texImage2D(type, mode, bitmap.bitmap, bordersize);
	}
	
	public static void glGenerateMipmap(int type)
	{
		GLES20.glGenerateMipmap(type);
	}
	
	public static void glStencilFunc(int func,int ref,int mask)
	{
		GLES20.glStencilFunc(func, ref, mask);
	}
	
	public static void glStencilOp(int sfail,int dpfail,int dppass)
	{
		GLES20.glStencilOp(sfail, dpfail, dppass);
	}
	
	public static void glBlendFunc(int sfactor,int dfactor)
	{
		GLES20.glBlendFunc(sfactor, dfactor);
	}
	
	public static void glDeleteFrameBuffers(int num,int[] buffers,int offset)
	{
		GLES20.glDeleteFramebuffers(num, buffers, offset);
	}
	
	public static void glDeleteTextures(int num,int[] textures,int offset)
	{
		GLES20.glDeleteTextures(num, textures, offset);
	}
	
	public static void glGenFramebuffers(int num,int[] buffers,int offset)
	{
		GLES20.glGenFramebuffers(num, buffers, 0);
	}
	
	public static void glGenRenderbuffers(int num,int[] buffers,int offset)
	{
		GLES20.glGenRenderbuffers(num, buffers, 0);
	}
	
	public static void glBindRenderbuffers(int buffer)
	{
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, buffer);
	}
	
	public static void glBindFramebuffer(int buffer)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, buffer);
	}
	
	public static void aneInitShadowBuffer(int shadowTexId,int renderDepthBufferId,int width,int height)
	{
		GLES20.glFramebufferTexture2D
        (
        	GLES20.GL_FRAMEBUFFER, 
        	GLES20.GL_COLOR_ATTACHMENT0,
        	GLES20.GL_TEXTURE_2D, 
        	shadowTexId, 
        	0
        );     
    	
    	GLES20.glTexImage2D
    	(
    		GLES20.GL_TEXTURE_2D, 
    		0, 
    		GLES20.GL_RGB, 
    		width, 
    		height, 
    		0, 
    		GLES20.GL_RGB, 
    		GLES20.GL_UNSIGNED_SHORT_5_6_5, 
    		null
    	);
    	
    	GLES20.glFramebufferRenderbuffer
    	(
    		GLES20.GL_FRAMEBUFFER, 
    		GLES20.GL_DEPTH_ATTACHMENT,
    		GLES20.GL_RENDERBUFFER, 
    		renderDepthBufferId
    	);
	}
	
	public static void glRenderBufferStorage(int internalformat,int width,int height)
	{
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, internalformat, width, height);
	}
	
	public static void glReadPixels(int x,int y,int width,int height,int format,int type,Buffer pixel)
	{
		GLES20.glReadPixels(x, y, width, height, format, type, pixel);
	}
	
	public static void glLineWidth(float width)
	{
		GLES20.glLineWidth(width);
	}
}
