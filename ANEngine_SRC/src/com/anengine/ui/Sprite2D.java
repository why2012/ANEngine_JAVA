package com.anengine.ui;

import java.nio.Buffer;

import com.anengine.core.Camera3D;
import com.anengine.core.Scene3D;
import com.anengine.core.Shader;
import com.anengine.independence.base_data.PointsDataManager;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_gl.ShaderWrapper;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.textures.BitmapTexture;
import com.anengine.utils.tools.Ray;

public class Sprite2D{	
	private String DefaultVertexShader = "shaders/vertex_2d.sh";
	private String DefaultFragmentShader = "shaders/frag_2d.sh";
	private float[] vertices = new float[12]; 
	private short[] indices = new short[]{0,1,2,2,3,0};
	private float[] texCoords = new float[8];
	private int s_vertices;
	private int s_texCoords;
	private int s_transformMatrix;
	private int s_finalMatrix;
	private int s_offsetscaleST;
	private int s_texture01_id;

	protected Shader shader = null;
	protected Buffer vertices_buffer = null;
	protected Buffer indices_buffer = PointsDataManager.getBufferfromShort(indices);
	protected Buffer texCoords_buffer = null;
	protected float[] cameraAttachMatrix = new float[16];
	protected float[] transformMatrix = new float[16];
	protected float[] finalMatrix = new float[16];
	
	public UIManager uiManager = null;
	public BitmapTexture bitmap = null;
	public float[] offsetscaleST = new float[]{0,0,1,1};
	public float width=3,height=3;
	public float[] XYZ = new float[]{0f,0f,0f};
	public float rotation = 0;
	private float[] scaleXY = new float[]{1f,1f};
	
	public Sprite2D()
	{
		shader = Shader.getOne(Shader.PATH,this.DefaultVertexShader,this.DefaultFragmentShader);
		this.prepareVertices();
		addVar();
	}
	
	public Sprite2D(String vpath,String fpath)
	{
		shader = Shader.getOne(Shader.PATH,vpath,fpath);
		this.prepareVertices();
		addVar();
	}
	
	public void update(Camera3D camera)
	{
		scaleXY[0] = width/2;
		scaleXY[1] = height/2;
		MatrixState.setIdentityM(transformMatrix, 0);
		MatrixState.translate(transformMatrix, XYZ[0]*Constant.UNIT_SIZE, XYZ[1]*Constant.UNIT_SIZE, XYZ[2]*Constant.UNIT_SIZE);
		MatrixState.rotate(transformMatrix, rotation,0,0,1);
		MatrixState.scale(transformMatrix, scaleXY[0], scaleXY[1], 1.0f);
		this.finalMatrix = this.getFinalMatrix(camera, null);
	}
	
	public boolean pointIn(float x,float y)
	{
		if(uiManager!=null)
		{
			Ray ray = Ray.getRayFromScreen(x, y, uiManager.scene.config.viewPort,uiManager.scene.camera3d);
		}
		return false;
	}
	
	public void draw()
	{
		GLWrapper.glDisable(Constant.GL_CULL_FACE);
		GLWrapper.glUseProgram(this.shader.program);
		ShaderWrapper.glUniformMatrix4fv(s_transformMatrix, 1, false, transformMatrix, 0);
		ShaderWrapper.glUniformMatrix4fv(s_finalMatrix, 1, false, finalMatrix, 0);
		ShaderWrapper.glUniform4fv(s_offsetscaleST, 1, offsetscaleST, 0);
		ShaderWrapper.glUniform1i(s_texture01_id, 0);
		ShaderWrapper.glVertexAttribPointer(s_vertices, 3, Constant.GL_FLOAT, false, 12, vertices_buffer);
		GLWrapper.glEnableVertexAttribArray(s_vertices);
		ShaderWrapper.glVertexAttribPointer(s_texCoords, 2, Constant.GL_FLOAT, false, 8, texCoords_buffer);
		GLWrapper.glEnableVertexAttribArray(s_texCoords);
		if(this.bitmap!=null)
		{
			GLWrapper.glActiveTexture(Constant.GL_TEXTURE0);
			GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.bitmap.textureId);
		}
		GLWrapper.glDrawElements(Constant.GL_TRIANGLES, 6, Constant.GL_UNSIGNED_SHORT, indices_buffer);
	}
	
	private void addVar()
	{
		this.s_vertices = ShaderWrapper.glGetAttribLocation(shader.program, "vPosition");
		this.s_texCoords = ShaderWrapper.glGetAttribLocation(shader.program, "vtexCoord");
		this.s_transformMatrix = ShaderWrapper.glGetUniformLocation(shader.program, "transformMatrix");
		this.s_finalMatrix = ShaderWrapper.glGetUniformLocation(shader.program, "finalMatrix");
		this.s_offsetscaleST = ShaderWrapper.glGetUniformLocation(shader.program, "offsetscaleST");
		this.s_texture01_id = ShaderWrapper.glGetUniformLocation(shader.program, "texture01");
	}
	
	private float[] getFinalMatrix(Camera3D camera,float[] transformAxis)
	{
		return MatrixState.getFinalMatrix(camera.getCamera3DMatrix(), camera.getProjectMatrix(), this.transformMatrix, transformAxis);
	}
	
	private void prepareVertices()
	{
		float hw = width/2*Constant.UNIT_SIZE_2D,hh = height/2*Constant.UNIT_SIZE_2D;
		vertices[0] = -hw;
		vertices[1] = hh;
		vertices[2] = 0;
		
		vertices[3] = -hw;
		vertices[4] = -hh;
		vertices[5] = 0;
		
		vertices[6] = hw;
		vertices[7] = -hh;
		vertices[8] = 0;
		
		vertices[9] = hw;
		vertices[10] = hh;
		vertices[11] = 0;
		
		this.vertices_buffer = PointsDataManager.getBufferfromFloat(vertices);
		
		texCoords[0] = 0;
		texCoords[1] = 0;
		
		texCoords[2] = 0;
		texCoords[3] = 1;
		
		texCoords[4] = 1;
		texCoords[5] = 1;
		
		texCoords[6] = 1;
		texCoords[7] = 0;
		
		this.texCoords_buffer = PointsDataManager.getBufferfromFloat(texCoords);
	}
}
