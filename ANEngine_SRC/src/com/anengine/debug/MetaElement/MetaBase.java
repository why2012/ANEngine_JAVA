package com.anengine.debug.MetaElement;

import com.anengine.core.Shader;
import com.anengine.independence.base_data.PointsDataManager;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;

public class MetaBase implements IMetaElement {
	protected float[] vertices = null;
	protected int DrawMode = Constant.GL_LINES;
	protected int s_vertices;
	protected int s_finalMatrix;
	protected int s_color;
	protected Shader shader = null;
	public float[] color = new float[]{0,0,1,1};
	
	public MetaBase(Shader shader)
	{
		this.shader = shader;
		this.s_vertices = ShaderWrapper.glGetAttribLocation(shader.program, "vPosition");
		this.s_finalMatrix = ShaderWrapper.glGetUniformLocation(shader.program, "finalMatrix");
		this.s_color = ShaderWrapper.glGetUniformLocation(shader.program, "color");
	}
	
	@Override
	public void prepare(float[] finalMatrix) {
		// TODO Auto-generated method stub
		ShaderWrapper.glVertexAttribPointer(s_vertices, 3, Constant.GL_FLOAT, false, 12, PointsDataManager.getBufferfromFloat(vertices));
		ShaderWrapper.glUniformMatrix4fv(s_finalMatrix, 1, false, finalMatrix, 0);
		ShaderWrapper.glUniform4fv(s_color, 1, color, 0);
		GLWrapper.glEnableVertexAttribArray(s_vertices);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		GLWrapper.glDrawArrays(DrawMode, 0, this.vertices.length/3);
	}

}
