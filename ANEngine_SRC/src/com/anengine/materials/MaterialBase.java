package com.anengine.materials;

import com.anengine.core.Shader;
import com.anengine.core.renderer.RenderData;
import com.anengine.core.renderer.RenderOperation;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;

public class MaterialBase implements IMaterial{
	protected Shader shader;
	protected int materialType;
	
	protected int s_vPosition;
	protected int s_vNormal;
	protected int s_finalMatrix;
	protected int s_materialType;
	protected int s_transformMatrix;
	protected int s_rough;
	protected int s_lightCount;
	protected int s_cameraLoc;
	protected int s_lightPosition;
	protected int s_lightColor;
	protected int s_lightAmbient;
	protected int s_lightStrength;
	protected int s_lightAttenuation;
	protected int s_lightAttenuationRange;
	protected int s_spotlightAngleCosDirection;
	protected int s_Diffuse;
	protected int s_Specular;
	protected int s_Emission;
	protected int s_lightOff;
	protected int s_offsetscaleST;
	
	protected String defaultVertexShader = "shaders/vertex.sh";
	protected String defaultFragmentShader = "shaders/frag.sh";
	public boolean lightoff = false;
	public float diffuse = .8f;
	public float specular = .7f;
	public float emission = 0;
	public RenderOperation renderOperation;
	
	public MaterialBase(String vertexShaderPath,String fragmentShaderPath)
	{
		shader = Shader.getOne(Shader.PATH,vertexShaderPath,fragmentShaderPath);
		this.addVar();
	}
	
	public MaterialBase()
	{
		shader = Shader.getOne(Shader.PATH,defaultVertexShader,defaultFragmentShader);
		this.addVar();
	}
	
	public MaterialBase(Shader shader,int materialType)
	{
		this.shader = shader;
		this.materialType = materialType;
		this.addVar();
	}
	
	private void addVar()
	{
		this.s_vPosition = ShaderWrapper.glGetAttribLocation(this.shader.program, "vPosition");
		this.s_vNormal = ShaderWrapper.glGetAttribLocation(this.shader.program, "vNormal");
		this.s_materialType = ShaderWrapper.glGetUniformLocation(this.shader.program, "materialType");
		this.s_finalMatrix = ShaderWrapper.glGetUniformLocation(this.shader.program, "finalMatrix");
		this.s_transformMatrix = ShaderWrapper.glGetUniformLocation(this.shader.program, "transformMatrix");
		this.s_rough = ShaderWrapper.glGetUniformLocation(this.shader.program, "rough");
		
		this.s_lightCount = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightCount");
		this.s_cameraLoc = ShaderWrapper.glGetUniformLocation(this.shader.program, "cameraLoc");
		this.s_lightPosition = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightPosition");
		this.s_lightColor = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightColor");
		this.s_lightAmbient = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightAmbient");
		this.s_lightStrength = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightStrength");
		this.s_lightAttenuation = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightAttenuation");
		this.s_lightAttenuationRange = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightAttenuationRange");
		this.s_spotlightAngleCosDirection = ShaderWrapper.glGetUniformLocation(this.shader.program, "spotlightAngleCosDirection");
		this.s_Diffuse = ShaderWrapper.glGetUniformLocation(this.shader.program, "mdiffuse");
		this.s_Specular = ShaderWrapper.glGetUniformLocation(this.shader.program, "mspecular");
		this.s_Emission = ShaderWrapper.glGetUniformLocation(this.shader.program, "memission");
		this.s_lightOff = ShaderWrapper.glGetUniformLocation(this.shader.program, "lightOff");
		this.s_offsetscaleST = ShaderWrapper.glGetUniformLocation(this.shader.program, "offsetscaleST");	
	}
	
	private void applyLights(RenderData renderData)
	{		
		ShaderWrapper.glUniform1i(this.s_lightCount, renderData.lightsData.lightCount);
		ShaderWrapper.glUniform3fv(this.s_lightPosition, renderData.lightsData.lightCount, renderData.lightsData.lightPos, 0);
		ShaderWrapper.glUniform4fv(this.s_lightColor, renderData.lightsData.lightCount, renderData.lightsData.lightColor, 0);
		ShaderWrapper.glUniform1fv(this.s_lightAmbient, renderData.lightsData.lightCount, renderData.lightsData.lightAmbient, 0);
		ShaderWrapper.glUniform1fv(this.s_lightStrength,renderData.lightsData.lightCount, renderData.lightsData.lightStrength, 0);
		ShaderWrapper.glUniform3fv(this.s_lightAttenuation, renderData.lightsData.lightCount, renderData.lightsData.lightAttenuation, 0);
		ShaderWrapper.glUniform2fv(this.s_lightAttenuationRange, renderData.lightsData.lightCount, renderData.lightsData.lightAttenuationRange, 0);
		ShaderWrapper.glUniform4fv(this.s_spotlightAngleCosDirection, renderData.lightsData.lightCount, renderData.lightsData.spotlightAngleCosDirection, 0);
		ShaderWrapper.glUniform1f(this.s_Diffuse, ((MaterialBase)renderData.material).diffuse);
		ShaderWrapper.glUniform1f(this.s_Specular, ((MaterialBase)renderData.material).specular);
		ShaderWrapper.glUniform1f(this.s_Emission, ((MaterialBase)renderData.material).emission);
		ShaderWrapper.glUniform1i(this.s_lightOff, lightoff?1:0);
		ShaderWrapper.glUniform3fv(this.s_cameraLoc, 1, renderData.camera.XYZ, 0);
	}
	
	public void prepareMaterial(RenderData renderData)
	{
		GLWrapper.glUseProgram(shader.program);
		if(!(renderData.material instanceof SegmentMaterial)||renderData.subMesh.drawMode==1)
			ShaderWrapper.glVertexAttribPointer(this.s_vPosition, 3, Constant.GL_FLOAT, false, 3*4, renderData.subMesh.verticesBuffer.buffer);
		else
			ShaderWrapper.glVertexAttribPointer(this.s_vPosition, 3, Constant.GL_FLOAT, false, 3*4, renderData.subMesh.wireframe_verticesBuffer.buffer);
		if(renderData.subMesh.normalBuffer!=null)
			ShaderWrapper.glVertexAttribPointer(this.s_vNormal, 3, Constant.GL_FLOAT, false, 3*4, renderData.subMesh.normalBuffer.buffer);
		ShaderWrapper.glUniform1i(this.s_materialType, this.materialType);
		ShaderWrapper.glUniformMatrix4fv(this.s_finalMatrix, 1, false, renderData.object3d_finalMatrix, 0);
		ShaderWrapper.glUniformMatrix4fv(this.s_transformMatrix, 1, false, renderData.object3d_trasnformMatrix, 0);
		ShaderWrapper.glUniform4fv(s_offsetscaleST, 1,renderData.subMesh.offsetscaleST,0);
		GLWrapper.glEnableVertexAttribArray(this.s_vPosition);
		GLWrapper.glEnableVertexAttribArray(this.s_vNormal);
		
		this.applyLights(renderData);
	}
	
	public void beforeRender(RenderData renderData)
	{
		if(renderOperation!=null)
			this.renderOperation.beforeRender(renderData);
	}
	
	public void afterRender(RenderData renderData)
	{
		if(renderOperation!=null)
			this.renderOperation.afterRender(renderData);
	}
}
