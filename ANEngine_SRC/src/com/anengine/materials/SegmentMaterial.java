package com.anengine.materials;

import android.opengl.GLES20;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.ShaderWrapper;

public class SegmentMaterial extends MaterialBase {

	private float thickness = .1f;
	private float[] rgba = new float[]{0f,0f,1.0f,1.0f};
	
	protected int s_uColor;
	protected int s_thickness;
	
	public SegmentMaterial(float thickness,float r,float g,float b,float a)
	{
		super();
		
		this.thickness = thickness;
		this.rgba[0] = r;
		this.rgba[1] = g;
		this.rgba[2] = b;
		this.rgba[3] = a;
		this.addVar();
	}
	
	public SegmentMaterial(float thickness)
	{
		super();
		
		this.thickness = thickness;
		this.addVar();
	}
	
	private void addVar()
	{
		this.s_thickness = ShaderWrapper.glGetUniformLocation(this.shader.program, "uThickness");
		this.s_uColor = ShaderWrapper.glGetUniformLocation(this.shader.program, "uColor");
		this.materialType = 2;
	}
	
	public SegmentMaterial clone()
	{
		return new SegmentMaterial(this.thickness,rgba[0],rgba[1],rgba[2],rgba[3]);
	}
	
	public void prepareMaterial(RenderData renderData)
	{
		super.prepareMaterial(renderData);
		renderData.o3d.DrawMode = Constant.GL_LINES;
		GLES20.glLineWidth(thickness);
		ShaderWrapper.glUniform4fv(this.s_uColor, 1, rgba, 0);
	}
	
	public void beforeRender(RenderData renderData)
	{
		super.beforeRender(renderData);
		renderData.subMesh.enableWireframe(false);
	}
}
