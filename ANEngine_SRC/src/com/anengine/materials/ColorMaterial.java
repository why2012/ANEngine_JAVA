package com.anengine.materials;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.ShaderWrapper;

public class ColorMaterial extends SimpleMaterial {
	
	private float[] rgba = new float[]{0f,0f,1.0f,1.0f};
	
	protected int s_uColor;
	
	public ColorMaterial(float r,float g,float b,float a)
	{
		super();
		this.rgba[0] = r;
		this.rgba[1] = g;
		this.rgba[2] = b;
		this.rgba[3] = a;
		this.s_uColor = ShaderWrapper.glGetUniformLocation(this.shader.program, "uColor");
		this.materialType = 1;
	}
	
	public ColorMaterial clone()
	{
		return new ColorMaterial(rgba[0],rgba[1],rgba[2],rgba[3]);
	}
	
	public void prepareMaterial(RenderData renderData)
	{
		super.prepareMaterial(renderData);
		ShaderWrapper.glUniform4fv(this.s_uColor, 1, rgba, 0);
	}
}
