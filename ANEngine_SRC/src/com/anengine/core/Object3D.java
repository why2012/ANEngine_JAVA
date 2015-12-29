package com.anengine.core;

import java.util.ArrayList;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.MatrixState;

public class Object3D {
	
	public float[] transformMatrix = new float[16];
	public float[] XYZ = new float[]{0f,0f,0f};
	public float[] rotXYZ = new float[]{0f,0f,0f};
	public float[] scaleXYZ = new float[]{1f,1f,1f};
	public float[] pivotXYZ = new float[]{0f,0f,0f};
	public boolean useLocalPivot = true;
	
	protected Object3D parent = null;
	
	public Object3D()
	{
		MatrixState.initTranformMatrix(transformMatrix);
	}
	
	public void setParent(Object3D op)
	{
		this.parent = op;
	}
	
	public void update(float[] transformAxis,Camera3D camera,ArrayList<RenderData> renderData)
	{
		float[] pivot = this.pivotXYZ.clone();
		if(useLocalPivot)
		{
			pivot[0] += XYZ[0];
			pivot[1] += XYZ[1];
			pivot[2] += XYZ[2];
		}
		MatrixState.setIdentityM(transformMatrix, 0);

		float[] pivotTrans = new float[]{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
		float[] pivotTrans2 = new float[]{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
		//定位中心点，生成合适的旋转轴
		MatrixState.translate(pivotTrans, (XYZ[0]-pivot[0])*Constant.UNIT_SIZE, (XYZ[1]-pivot[1])*Constant.UNIT_SIZE, (XYZ[2]-pivot[2])*Constant.UNIT_SIZE);
		//撤销位移
		MatrixState.translate(pivotTrans2, pivot[0]*Constant.UNIT_SIZE, pivot[1]*Constant.UNIT_SIZE, pivot[2]*Constant.UNIT_SIZE);
		
		MatrixState.rotate(transformMatrix, rotXYZ[0],1,0,0);
		MatrixState.rotate(transformMatrix, rotXYZ[1],0,1,0);
		MatrixState.rotate(transformMatrix, rotXYZ[2],0,0,1);
		MatrixState.scale(pivotTrans, scaleXYZ[0], scaleXYZ[1], scaleXYZ[2]);
		
		//应用位移
		MatrixOperation.MultiplyMM(transformMatrix, transformMatrix.clone(), pivotTrans);
		//应用位移撤销
		MatrixOperation.MultiplyMM(transformMatrix, pivotTrans2, transformMatrix.clone());
	}
	
	public float[] getRotateAndTranlateMatrix()
	{
		float[] rotateAndTranslate = new float[16];
		MatrixState.setIdentityM(rotateAndTranslate, 0);
		MatrixState.translate(rotateAndTranslate, XYZ[0]*Constant.UNIT_SIZE, XYZ[1]*Constant.UNIT_SIZE, XYZ[2]*Constant.UNIT_SIZE);
		MatrixState.rotate(rotateAndTranslate, rotXYZ[0],1,0,0);
		MatrixState.rotate(rotateAndTranslate, rotXYZ[1],0,1,0);
		MatrixState.rotate(rotateAndTranslate, rotXYZ[2],0,0,1);
		return rotateAndTranslate;
	}
	
	public Object3D clone()
	{
		return this.clone();
	}
}
