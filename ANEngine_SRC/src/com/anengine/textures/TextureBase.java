package com.anengine.textures;

import java.util.ArrayList;

import android.opengl.GLES20;

import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;

public class TextureBase {
	
	public int textureId = 0;
	protected boolean isMipmap = false;
	protected int min,mag,s,t;
	private static ArrayList<Integer> textureIds = new ArrayList<Integer>();
	
	public TextureBase()
	{
		this.min = Constant.GL_NEAREST;
		this.mag = Constant.GL_LINEAR;
		this.s = Constant.GL_CLAMP_TO_EDGE;
		this.t = Constant.GL_CLAMP_TO_EDGE;
		
		int[] textures = new int[1];
		GLWrapper.glGenTextures(1, textures, 0);
		this.textureId = textures[0];
		textureIds.add(this.textureId);
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.textureId);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER, Constant.GL_NEAREST);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MAG_FILTER, Constant.GL_LINEAR);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_S, Constant.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_T, Constant.GL_CLAMP_TO_EDGE);
	}
	
	public TextureBase(int min,int mag,int s,int t)
	{
		this.min = min;
		this.mag = mag;
		this.s = s;
		this.t = t;
		int[] textures = new int[1];
		GLWrapper.glGenTextures(1, textures, 0);
		this.textureId = textures[0];
		textureIds.add(this.textureId);
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.textureId);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER, min);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MAG_FILTER, mag);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_S, s);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_T, t);
	}
	
	public void mipmap(boolean m)
	{
		if(m)
		{
			if(!isMipmap)
			{
				GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.textureId);
				GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER, Constant.GL_LINEAR_MIPMAP_NEAREST);
				GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MAG_FILTER, Constant.GL_LINEAR_MIPMAP_LINEAR);
				GLWrapper.glGenerateMipmap(Constant.GL_TEXTURE_2D);
				isMipmap = true;
			}
		}
		else
		{
			if(isMipmap)
			{
				GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.textureId);
				GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER, this.min);
				GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MAG_FILTER, this.mag);
				isMipmap = false;
			}
		}
	}
	
	public void release()
	{
		GLES20.glDeleteTextures(1, new int[]{this.textureId}, 0);
	}
	
	public static void releaseAll()
	{
		int[] textures = new int[textureIds.size()];
		int i = 0;
		for(int id:textureIds)
		{
			textures[i] = textureIds.get(i);
			i++;
		}
		GLES20.glDeleteTextures(i, textures, 0);
	}
}
