package com.anengine.independence.base_math;

import android.opengl.Matrix;

public class MatrixState {
	
	/*
	 * cmatrix:摄像机状态矩阵
	 * cx,cy,cz:摄像机位置
	 * tx,ty,tz:摄像机目标点位置
	 * upx,upy,upz:摄像机顶端指向
	 */
	public static void setCamera(float[] cmatrix,
			float cx,float cy,float cz,
			float tx,float ty,float tz,
			float upx,float upy,float upz)
	{
		Matrix.setLookAtM(cmatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
	}
	
	/*
	 * 设置正交投影
	 */
	public static void setProjectOrtho(float[] projmatrix,
			float left,float right,
			float bottom,float top,
			float near,float far)
	{
		Matrix.orthoM(projmatrix, 0, left, right, bottom, top, near, far);
	}
	
	/*
	 * 设置透视投影
	 */
	public static void setProjectFrustum(float[] projmatrix,
			float left,float right,
			float bottom,float top,
			float near,float far)
	{
		Matrix.frustumM(projmatrix, 0, left, right, bottom, top, near, far);
	}
	
	/*public static void setPerspective(float[] projmatrix,float fovy, float aspect, float zNear, float zFar)
	{
		Matrix.perspectiveM(projmatrix, 0, fovy, aspect, zNear, zFar);
	}*/
	
	/*
	 * 初始化方位矩阵
	 */
	public static void initTranformMatrix(float[] transformM)
	{
		Matrix.setRotateM(transformM, 0, 0, 0, 1, 0);
	}
	
	/*
	 * 位置改变
	 */
	public static void translate(float[] transformM,float x,float y,float z)
	{
		Matrix.translateM(transformM, 0, x, y, z);
	}
	
	/*
	 * 方向改变
	 */
	public static void rotate(float[] transformM,float a,float x,float y,float z)
	{
		Matrix.rotateM(transformM, 0, a, x, y, z);
	}
	
	public static void scale(float[] transformM,float x,float y,float z)
	{
		Matrix.scaleM(transformM, 0, x, y, z);
	}
	
	public static void setIdentityM(float[] matrix,int offset)
	{
		Matrix.setIdentityM(matrix, offset);
	}
	
	public static void setIdentityV(float[] vector,int offset)
	{
		double len_d = 0;
		float len_f = 0;
		for(int i=offset;i<vector.length;i++)
			len_d += vector[i]*vector[i];
		len_f = (float)Math.sqrt(len_d);
		for(int i=offset;i<vector.length;i++)
			vector[i] /= len_f;
	}
	
	/*
	 * 得到最终变换矩阵 投影矩阵*(摄像机矩阵*方位矩阵)
	 * cmatrix：摄像机矩阵
	 * projectMatrix：投影矩阵
	 * transformM：方位矩阵
	 */
	public static float[] getFinalMatrix(float[] cmatrix,float[] projectMatrix,float[] transformM,float[] transformAxis)
	{
		float[] fm = new float[16];
		if(transformAxis!=null)
		{
			Matrix.multiplyMM(fm, 0, transformAxis, 0, transformM, 0);
			Matrix.multiplyMM(fm, 0, cmatrix, 0, fm, 0);
		}
		else
			Matrix.multiplyMM(fm, 0, cmatrix, 0, transformM, 0);
		Matrix.multiplyMM(fm, 0, projectMatrix, 0, fm, 0);
		return fm;
	}
	
	public static void printMatrix(float[] matrix,int unitCount)
	{
		System.out.print("-");
		for(int row=0;row<matrix.length;row+=unitCount)
		{
			System.out.print("| ");
			for(int i=0;i<unitCount;i++)
				System.out.print(matrix[row+i]+" , ");
			System.out.print(" |\n");
		}
	}
}
