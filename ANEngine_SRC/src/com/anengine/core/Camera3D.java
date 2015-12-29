package com.anengine.core;

import java.util.ArrayList;

import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.Controller.IController;;

public class Camera3D extends Object3D{
	//方位矩阵
	public float[] camera3DMatrix = new float[16];
	//投影矩阵
	public float[] projectMatrix = new float[16];
	//lookAt
	public float[] tXYZ = new float[]{0,0,0};
	//up
	public float[] upXYZ = new float[]{0,1,0};
	//view box
	public float left=-1,right=1,bottom=-1,top=1,near=1,far=100;
	//投影方式 0：透视投影 1：正交投影
	public int cameraProjMode = 0;
	//控制器
	public IController controller = null;
	
	public Camera3D()
	{
		MatrixState.setCamera(camera3DMatrix, XYZ[0]*Constant.UNIT_SIZE, XYZ[1]*Constant.UNIT_SIZE, XYZ[2]*Constant.UNIT_SIZE, tXYZ[0], tXYZ[1], tXYZ[2], upXYZ[0], upXYZ[1], upXYZ[2]);
		if(cameraProjMode==0)
			MatrixState.setProjectFrustum(projectMatrix, left, right, bottom, top, near, far);
		else 
			MatrixState.setProjectOrtho(projectMatrix, left, right, bottom, top, near, far);
	}
	
	public void update(float[] transformAxis,Camera3D camera,ArrayList<RenderData> renderData)
	{
		super.update(transformAxis,camera,renderData);
		float[] newXYZ = XYZ.clone();
		float[] T = this.transformMatrix.clone();
		
		if(transformAxis!=null)
		{
			MatrixOperation.MultiplyMM(this.transformMatrix, transformAxis, T);
			MatrixOperation.MultiplyMV(newXYZ, transformAxis, XYZ);
		}
		
		//MatrixOperation.MultiplyMV(upXYZ, T, new float[]{0,1f,0});
		MatrixState.setCamera(camera3DMatrix, newXYZ[0]*Constant.UNIT_SIZE, newXYZ[1]*Constant.UNIT_SIZE, newXYZ[2]*Constant.UNIT_SIZE, tXYZ[0], tXYZ[1], tXYZ[2], upXYZ[0], upXYZ[1], upXYZ[2]);
		if(cameraProjMode==0)
			MatrixState.setProjectFrustum(projectMatrix, left, right, bottom, top, near, far);
		else 
			MatrixState.setProjectOrtho(projectMatrix, left, right, bottom, top, near, far);
	}
	
	public float[] getCamera3DMatrix()
	{
		return camera3DMatrix;
	}
	
	/*
	 * type:
	 * 		0 透视投影
	 * 		1 正交投影
	 */
	public float[] getProjectMatrix()
	{
		return projectMatrix;
	}
	
	public void setPosition(float x,float y ,float z)
	{
		XYZ[0] = x;
		XYZ[1] = y;
		XYZ[2] = z;
	}
	
	public void lookAt(float x,float y,float z)
	{
		tXYZ[0] = x;
		tXYZ[1] = y;
		tXYZ[2] = z;
	}
	
	public void setViewBox(float left,float right,
			float bottom,float top,
			float near,float far)
	{
		this.left = left;this.right = right;
		this.bottom = bottom;this.top = top;
		this.near = near;this.far = far;
	}
	
	public float[] getFinalMatrix()
	{
		float[] finalMatrix = new float[16];
		MatrixOperation.MultiplyMM(finalMatrix, projectMatrix, camera3DMatrix);
		return finalMatrix;
	}
}