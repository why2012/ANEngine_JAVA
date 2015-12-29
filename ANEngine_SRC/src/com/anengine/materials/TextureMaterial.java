package com.anengine.materials;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;
import com.anengine.textures.TextureBase;

public class TextureMaterial extends SimpleMaterial {
	public TextureBase texture;
	public boolean mipmap = false;
	public boolean reverseTexT = false;//一些模型T轴朝上，默认朝下
	
	private int texture_id;
	
	protected int s_vtexCoord;
	protected int s_reverseTexT;
	
	public TextureMaterial(TextureBase texture)
	{
		super();
		this.texture = texture;
		this.s_vtexCoord = ShaderWrapper.glGetAttribLocation(this.shader.program, "vtexCoord");
		this.s_reverseTexT = ShaderWrapper.glGetUniformLocation(this.shader.program, "reverseTexT");
		this.texture_id = ShaderWrapper.glGetUniformLocation(this.shader.program, "texture01");
		this.materialType = 3;
	}
	
	public TextureMaterial clone()
	{
		TextureMaterial tmp = new TextureMaterial(this.texture);
		tmp.specularMap = specularMap;
		tmp.normalMap = normalMap;
		return tmp;
	}
	
	public void prepareMaterial(RenderData renderData)
	{
		super.prepareMaterial(renderData);
		ShaderWrapper.glVertexAttribPointer(this.s_vtexCoord, 2, Constant.GL_FLOAT, false, 2*4, renderData.subMesh.texCoordBuffer.buffer);
		ShaderWrapper.glUniform1i(s_reverseTexT, this.reverseTexT?1:0);
		GLWrapper.glEnableVertexAttribArray(this.s_vtexCoord);
		if(this.mipmap)
		{
			this.texture.mipmap(mipmap);
		}
		GLWrapper.glActiveTexture(Constant.GL_TEXTURE3);
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.texture.textureId);
		ShaderWrapper.glUniform1i(texture_id, 3);
	}
}
