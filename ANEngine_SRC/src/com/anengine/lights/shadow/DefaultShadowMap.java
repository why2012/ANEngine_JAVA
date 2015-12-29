package com.anengine.lights.shadow;

import com.anengine.core.Scene3D;
import com.anengine.independence.base_data.ANE_Buffer;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;
import com.anengine.lights.LightBase;

public class DefaultShadowMap extends ShadowMapBase {
	protected int framebufferId = 0;//自定义帧缓冲id
	public int shadowTexId = 0;//阴影纹理id
	protected int renderDepthBufferId = 0;//深度缓冲渲染对象id
	protected int shadowTexWidth=1024,shadowTexHeight=1024;
	protected boolean renderDepthBufferInited = false;
	public LightBase light;//光源位置
	public int s_finalMatrix,s_transformMatrix,s_vPosition,s_lightLocation;
	
	private Scene3D _scene;
	
	public DefaultShadowMap(LightBase light)
	{
		this.light = light;
		if(light.type()==3)//directional light
		{
			this.shadowTexWidth = shadowTexHeight = 512;
		}
		else if(light.type()==1)//pointlight
		{
			this.shadowTexWidth = shadowTexHeight = 2048;
		}
		else if(light.type()==4)//spotlight
		{
			this.shadowTexWidth = shadowTexHeight = 512;
		}
		this.s_finalMatrix = ShaderWrapper.glGetUniformLocation(this.shadow_shader.program, "finalMatrix");
		this.s_transformMatrix = ShaderWrapper.glGetUniformLocation(this.shadow_shader.program, "transformMatrix");
		this.s_vPosition = ShaderWrapper.glGetAttribLocation(this.shadow_shader.program, "vPosition");
		this.s_lightLocation = ShaderWrapper.glGetUniformLocation(this.shadow_shader.program, "lightLocation");
	}
	
	//must be called before use shadowmap
	public void initBuffers()
	{
		int[] ids = new int[1];
		GLWrapper.glGenFramebuffers(1, ids, 0);
		framebufferId = ids[0];
		if(!this.renderDepthBufferInited)
		{
			GLWrapper.glGenRenderbuffers(1, ids, 0);
			renderDepthBufferId = ids[0];
			GLWrapper.glBindRenderbuffers(renderDepthBufferId);
			GLWrapper.glRenderBufferStorage(Constant.GL_DEPTH_COMPONENT16, shadowTexWidth, shadowTexHeight);
			this.renderDepthBufferInited = true;
		}
		GLWrapper.glGenTextures(1, ids, 0);
		shadowTexId = ids[0];
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, shadowTexId);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER, Constant.GL_NEAREST);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MAG_FILTER, Constant.GL_NEAREST);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_S, Constant.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_T, Constant.GL_CLAMP_TO_EDGE);
	}
		
	public void prepare(Scene3D scene)
	{
		_scene = scene;
		GLWrapper.glViewport(0, 0, this.shadowTexWidth, this.shadowTexWidth);
		GLWrapper.glBindFramebuffer(framebufferId);
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.shadowTexId);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_MIN_FILTER,Constant.GL_LINEAR);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D,Constant.GL_TEXTURE_MAG_FILTER,Constant.GL_LINEAR);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_S,Constant.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameterf(Constant.GL_TEXTURE_2D, Constant.GL_TEXTURE_WRAP_T,Constant.GL_CLAMP_TO_EDGE); 
		GLWrapper.aneInitShadowBuffer(shadowTexId, renderDepthBufferId, shadowTexWidth, shadowTexHeight);
		GLWrapper.glClear(Constant.GL_DEPTH_BUFFER_BIT|Constant.GL_COLOR_BUFFER_BIT);
	}
	
	public void beforeDraw(float[] finalMatrix,float[] transformMatrix,ANE_Buffer vertexBuffer)
	{
		GLWrapper.glUseProgram(this.shadow_shader.program);
		ShaderWrapper.glUniformMatrix4fv(s_finalMatrix, 1, false, finalMatrix, 0);
		ShaderWrapper.glUniformMatrix4fv(s_transformMatrix,1,false,transformMatrix,0);
		ShaderWrapper.glVertexAttribPointer(s_vPosition, 3, Constant.GL_FLOAT, false, 12, vertexBuffer.buffer);
		ShaderWrapper.glUniform3fv(s_lightLocation, 1, new float[]{light.XYZ[0]*Constant.UNIT_SIZE, light.XYZ[1]*Constant.UNIT_SIZE,
				light.XYZ[2]*Constant.UNIT_SIZE}, 0);
		GLWrapper.glEnableVertexAttribArray(s_vPosition);
	}
		
	public void toDefault(Scene3D scene)
	{
		GLWrapper.glBindFramebuffer(0);
		GLWrapper.glViewport((int)scene.config.viewPort[0], (int)scene.config.viewPort[1], (int)scene.config.viewPort[2], (int)scene.config.viewPort[3]);
		//GLWrapper.glClear(Constant.GL_DEPTH_BUFFER_BIT|Constant.GL_COLOR_BUFFER_BIT);
	}
		
	public void clear()
	{
		GLWrapper.glDeleteFrameBuffers(1, new int[]{framebufferId}, 0);
		GLWrapper.glDeleteTextures(1, new int[]{shadowTexId}, 0);
		framebufferId = 0;
		shadowTexId = 0;
	}
}
