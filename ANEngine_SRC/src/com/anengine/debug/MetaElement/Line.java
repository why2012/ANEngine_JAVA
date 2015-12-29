package com.anengine.debug.MetaElement;

import android.opengl.GLES20;

import com.anengine.core.Shader;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_math.Vector3D;

public class Line extends MetaBase{
	
	public float width = 1.0f;
	
	public Line(Vector3D pos1,Vector3D pos2,Shader shader)
	{
		super(shader);
		
		this.DrawMode = Constant.GL_LINES;
		
		vertices = new float[6];
		vertices[0] = pos1.nx;
		vertices[1] = pos1.ny;
		vertices[2] = pos1.nz;
		
		vertices[3] = pos2.nx;
		vertices[4] = pos2.ny;
		vertices[5] = pos2.nz;
	}
	
	public Line(Vector3D pos1,Vector3D pos2,float[] color,Shader shader)
	{
		super(shader);
		
		this.DrawMode = Constant.GL_LINES;
		
		vertices = new float[6];
		vertices[0] = pos1.nx;
		vertices[1] = pos1.ny;
		vertices[2] = pos1.nz;
		
		vertices[3] = pos2.nx;
		vertices[4] = pos2.ny;
		vertices[5] = pos2.nz;
		
		this.color = color;
	}
	
	public void draw()
	{
		GLWrapper.glLineWidth(width);
		super.draw();
	}
}
