package com.anengine.core;

import java.util.ArrayList;

import com.anengine.core.renderer.RenderData;
import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.materials.MaterialBase;

public class ObjectContainer3D extends Object3D{
	
	protected ArrayList<Object3D> objContainer = new ArrayList<Object3D>();
	
	public ObjectContainer3D(){}
	
	public ObjectContainer3D(float x,float y,float z)
	{
		XYZ[0] = x;
		XYZ[1] = y;
		XYZ[2] = z;
	}
	
	public int faces()
	{
		int faces = 0;
		for(Object3D obj:objContainer)
		{
			if(obj instanceof Mesh)
				faces += ((Mesh)obj).faces();
		}
		return faces;
	}
	
	public boolean addObject3D(Object3D obj)
	{
		obj.setParent(this);
		return this.objContainer.add(obj);
	}
	
	public Object3D getObject3D(int index)
	{
		return this.objContainer.get(index);
	}
	
	public boolean removeObject3D(Object3D obj)
	{
		obj.setParent(null);
		return this.objContainer.remove(obj);
	}
	
	public void setAll(ArrayList<Object3D> objContainer)
	{
		this.objContainer = objContainer;
	}
	
	public Object3D removeObject3D(int index)
	{
		this.objContainer.get(index).setParent(null);
		return this.objContainer.remove(index);
	}
	
	public void attachMaterial(MaterialBase material)
	{
		for(Object3D curO3D:objContainer)
		{
			if(curO3D instanceof Mesh)
				((Mesh)curO3D).material = material;
		}
	}
	
	public void setMaterialOffsetScale(float offX,float offY,float scaleX,float scaleY)
	{
		for(Object3D curO3D:objContainer)
		{
			if(curO3D instanceof Mesh)
			{
				Mesh mesh = (Mesh)curO3D;
				for(SubMesh subMesh:mesh.subMeshes)
				{
					subMesh.offsetscaleST[0] = offX;
					subMesh.offsetscaleST[1] = offY;
					subMesh.offsetscaleST[2] = scaleX;
					subMesh.offsetscaleST[3] = scaleY;
				}
			}
		}
	}
	
	public void enableCullFace(boolean e)
	{
		for(Object3D curO3D:objContainer)
		{	
			if(curO3D instanceof Mesh)
				((Mesh)curO3D).enableCullFace = e;
		}
	}
	
	public void frontFaceOrder(int type)
	{
		for(Object3D curO3D:objContainer)
		{
			if(curO3D instanceof Mesh)
				((Mesh)curO3D).frontFace = type;
		}
	}
	
	public void lightoff(boolean l)
	{
		for(Object3D curO3D:objContainer)
		{
			if(curO3D instanceof Mesh&&((Mesh)curO3D).material!=null&&((Mesh)curO3D).material instanceof MaterialBase)
				((MaterialBase)((Mesh)curO3D).material).lightoff = l;;
		}
	}
	
	public int length()
	{
		return this.objContainer.size();
	}
	
	public ObjectContainer3D clone()
	{
		ObjectContainer3D tmp = new ObjectContainer3D();
		tmp.setAll(objContainer);
		return tmp;
	}
	
	public void update(float[] transformAxis,Camera3D camera,ArrayList<RenderData> renderData)
	{
		super.update(transformAxis,camera,renderData);
		
		if(transformAxis!=null)	
		{
			float[] arr = new float[16];
			MatrixOperation.MultiplyMM(arr, transformAxis, this.transformMatrix);
			transformAxis = arr;
		}
		else
			transformAxis = this.transformMatrix.clone();
		for(Object3D curO3D:objContainer)
		{
			curO3D.update(transformAxis,camera,renderData);
		}
	}
}
