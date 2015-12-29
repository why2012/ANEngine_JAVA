package com.anengine.entity;

import java.util.ArrayList;

import com.anengine.animation.Animator;
import com.anengine.core.Camera3D;
import com.anengine.core.Object3D;
import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.materials.IMaterial;

public class Mesh extends Object3D {
	public IMaterial material;
	public ArrayList<SubMesh> subMeshes = new ArrayList<SubMesh>();
	
	public int DrawMode = Constant.GL_TRIANGLES;//绘制模式
	public boolean enableCullFace = true;
	public int frontFace = Constant.GL_CCW;
	public Animator animator = null;
	
	public Mesh()
	{
		super();
	}
	
	public float[] getFinalMatrix(Camera3D camera,float[] transformAxis)
	{
		return MatrixState.getFinalMatrix(camera.getCamera3DMatrix(), camera.getProjectMatrix(), this.transformMatrix, transformAxis);
	}
	
	public int faces()
	{
		int faces = 0;
		for(SubMesh subMesh:subMeshes)
		{
			faces += subMesh.faces();
		}
		return faces;
	}
	
	public Mesh clone()
	{
		Mesh tmp = (Mesh)super.clone();
		tmp.subMeshes = this.subMeshes;
		tmp.material = this.material;
		tmp.animator = animator.clone();
		return tmp;
	}
	
	//每帧均执行
	public void update(float[] transformAxis,Camera3D camera,ArrayList<RenderData> renderDataList)
	{
		super.update(transformAxis,camera,renderDataList);
		RenderData renderData = new RenderData();
		renderData.o3d = this;
		float finalM[];
		if(transformAxis!=null)
		{
			MatrixOperation.MultiplyMM(renderData.object3d_trasnformMatrix, transformAxis, this.transformMatrix);
			renderData.transformAxis = transformAxis;
			finalM = getFinalMatrix(camera,renderData.transformAxis);
		}
		else
		{
			finalM = getFinalMatrix(camera,null);
			renderData.object3d_trasnformMatrix = this.transformMatrix;
		}
		renderData.object3d_finalMatrix = finalM;
		renderData.camera = camera;
		
		if(animator!=null)
			for(int i=0;i<subMeshes.size();i++)
				subMeshes.get(i).prepareSkeletonMesh();
		
		renderDataList.add(renderData);
	}
	
	public void prepare()
	{
		if(this.enableCullFace)
		{
			GLWrapper.glEnable(Constant.GL_CULL_FACE);
		}
		else
		{
			GLWrapper.glDisable(Constant.GL_CULL_FACE);
		}
		GLWrapper.glFrontFace(this.frontFace);
	}
}
