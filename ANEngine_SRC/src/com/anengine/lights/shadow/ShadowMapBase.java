package com.anengine.lights.shadow;

import com.anengine.core.Shader;

/*
 * 生成阴影深度图
 */
public class ShadowMapBase {
	protected Shader shadow_shader;
	protected String vertex_shader = "shaders/shadow_vertex.sh";
	protected String fragment_shader = "shaders/shadow_frag.sh";
	
	public ShadowMapBase()
	{
		shadow_shader = Shader.getOne(Shader.PATH,vertex_shader,fragment_shader);
	}
	
	public ShadowMapBase(String vertex_shader,String fragment_shader)
	{
		this.vertex_shader = vertex_shader;
		this.fragment_shader = fragment_shader;
		shadow_shader = Shader.getOne(Shader.PATH,vertex_shader,fragment_shader);
	}
}
