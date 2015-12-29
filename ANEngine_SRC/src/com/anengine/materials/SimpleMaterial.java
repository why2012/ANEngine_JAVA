package com.anengine.materials;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;
import com.anengine.textures.TextureBase;

public class SimpleMaterial extends MaterialBase {
	public float rough = 30.0f;//粗糙度
	protected int s_receiveShadow;
	protected int s_shadowlight;
	protected int s_lightFinalMatrix;
	protected int s_shadow_texture;
	public float[] light_shadow;
	public float[] lightFinalMatrix;
	public int shadow_tex_id = -1;
	
	public boolean receiveShadow = false;//接受被投射阴影
	public boolean castShadow = true;//是否投射阴影
	public TextureBase specularMap = null;
	public TextureBase normalMap = null;
	private int specularmap_id;
	private int normalmap_id;
	private int enable_specularmap;
	private int enable_normalmap;
	private int normalmap_tangent;
	
	public SimpleMaterial()
	{
		super();
		
		this.s_shadowlight = ShaderWrapper.glGetUniformLocation(this.shader.program,"shadowLight");
		this.s_lightFinalMatrix = ShaderWrapper.glGetUniformLocation(this.shader.program,"lightFinalMatrix");
		this.s_shadow_texture = ShaderWrapper.glGetUniformLocation(this.shader.program,"shadow_texture");
		this.s_receiveShadow = ShaderWrapper.glGetUniformLocation(this.shader.program,"receiveShadow");
		this.specularmap_id = ShaderWrapper.glGetUniformLocation(this.shader.program, "texture_specular");
		this.normalmap_id = ShaderWrapper.glGetUniformLocation(this.shader.program, "texture_normal");
		this.enable_specularmap = ShaderWrapper.glGetUniformLocation(this.shader.program, "enable_specularmap");
		this.enable_normalmap = ShaderWrapper.glGetUniformLocation(this.shader.program, "enable_normalmap");
		this.normalmap_tangent = ShaderWrapper.glGetAttribLocation(this.shader.program, "tangent");
	}
	
	public void prepareMaterial(RenderData renderData)
	{
		super.prepareMaterial(renderData);
		ShaderWrapper.glUniform1f(this.s_rough, this.rough);
		
		ShaderWrapper.glUniform1i(s_receiveShadow, 0);
		if(this.receiveShadow&&shadow_tex_id!=-1)
		{
			ShaderWrapper.glUniform1i(s_receiveShadow, 1);
			ShaderWrapper.glUniform3fv(s_shadowlight, 1, light_shadow,0);
			ShaderWrapper.glUniformMatrix4fv(s_lightFinalMatrix, 1, false, this.lightFinalMatrix, 0);
			GLWrapper.glActiveTexture(Constant.GL_TEXTURE0);
			GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.shadow_tex_id);
			ShaderWrapper.glUniform1i(s_shadow_texture, 0);
		}
		
		ShaderWrapper.glUniform1i(this.enable_specularmap, 0);
		if(this.specularMap!=null)
		{
			GLWrapper.glActiveTexture(Constant.GL_TEXTURE1);
			GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.specularMap.textureId);
			ShaderWrapper.glUniform1i(specularmap_id, 1);
			ShaderWrapper.glUniform1i(this.enable_specularmap, 1);
		}
		
		ShaderWrapper.glUniform1i(this.enable_normalmap, 0);
		if(this.normalMap!=null)
		{
			GLWrapper.glActiveTexture(Constant.GL_TEXTURE2);
			GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.normalMap.textureId);
			ShaderWrapper.glUniform1i(normalmap_id, 2);
			ShaderWrapper.glUniform1i(this.enable_normalmap, 1);
			renderData.subMesh.cacuTangent(false);
			ShaderWrapper.glVertexAttribPointer(normalmap_tangent, 3, Constant.GL_FLOAT, false, 12, renderData.subMesh.tangentBuffer.buffer);
			GLWrapper.glEnableVertexAttribArray(normalmap_tangent);
		}
	}
}
