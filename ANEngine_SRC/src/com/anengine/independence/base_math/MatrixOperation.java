package com.anengine.independence.base_math;

import android.opengl.Matrix;

public class MatrixOperation {
	
	/*
	 * 1*3向量vec绕1*3向量axis旋转
	 */
	public static void rotateVec(float[] vec,float[] axis,float a)
	{
		
	}
	
	/*
	 * 1*3向量vec绕点rotP旋转
	 */
	public static void rotateP(float[] vec,float[] rotP,float a,int x,int y,int z)
	{
		//重新映射坐标原点
		vec[0] -= rotP[0];
		vec[1] -= rotP[1];
		vec[2] -= rotP[2];
		
		float[] vect = vec.clone();
		if(x==1)
		{
			vec[1] = (float)(vect[1]*Math.cos(a)-vect[2]*Math.sin(a));
			vec[2] = (float)(vect[1]*Math.sin(a)+vect[2]*Math.cos(a));
		}
		if(y==1)
		{
			vec[0] = (float)(vect[0]*Math.cos(a)+vect[2]*Math.sin(a));
			vec[2] = (float)(-vect[0]*Math.sin(a)+vect[2]*Math.cos(a));
		}
		if(z==1)
		{
			vec[0] = (float)(vect[0]*Math.cos(a)-vect[1]*Math.sin(a));
			vec[1] = (float)(vect[0]*Math.sin(a)+vect[1]*Math.cos(a));
		}
		
		//映射回原坐标原点
		vec[0] += rotP[0];
		vec[1] += rotP[1];
		vec[2] += rotP[2];
	}
	
	/*
	 * 1*3向量vec绕点原点旋转
	 */
	public static void rotate(float[] vec,float a,int x,int y,int z)
	{
		float[] vect = vec.clone();
		if(x==1)
		{
			vec[1] = (float)(vect[1]*Math.cos(a)-vect[2]*Math.sin(a));
			vec[2] = (float)(vect[1]*Math.sin(a)+vect[2]*Math.cos(a));
		}
		if(y==1)
		{
			vec[0] = (float)(vect[0]*Math.cos(a)+vect[2]*Math.sin(a));
			vec[2] = (float)(-vect[0]*Math.sin(a)+vect[2]*Math.cos(a));
		}
		if(z==1)
		{
			vec[0] = (float)(vect[0]*Math.cos(a)-vect[1]*Math.sin(a));
			vec[1] = (float)(vect[0]*Math.sin(a)+vect[1]*Math.cos(a));
		}
	}
	
	//列向量
	public static void MultiplyMM44(float[] r,float[] m1,float[] m2)
	{
		float[] result = new float[16];
		for(int i=0;i<4;i++)
		{
			result[i*4] = m1[i*4]*m2[0] + m1[i*4+1]*m2[4] + m1[i*4+2]*m2[8] + m1[i*4+3]*m2[12];
			result[i*4+1] = m1[i*4]*m2[1] + m1[i*4+1]*m2[5] + m1[i*4+2]*m2[9] + m1[i*4+3]*m2[13];
			result[i*4+2] = m1[i*4]*m2[2] + m1[i*4+1]*m2[6] + m1[i*4+2]*m2[10] + m1[i*4+3]*m2[14];
			result[i*4+3] = m1[i*4]*m2[3] + m1[i*4+1]*m2[7] + m1[i*4+2]*m2[11] + m1[i*4+3]*m2[15];
		}
		for(int i=0;i<16;i++)
			r[i] = result[i];
	}
	
	//列向量
	public static void MultiplyMV41(float[] result,float[] m,float[] v)
	{
		for(int i=0;i<4;i++)
		{
			result[i] = m[i*4]*v[0]+m[i*4+1]*v[1]+m[i*4+2]*v[2]+m[i*4+3]*v[3];
		}
	}
	
	public static void MultiplyMM(float[] result,float[] m1,float[] m2)
	{
		Matrix.multiplyMM(result, 0, m1, 0, m2, 0);
	}
	
	public static void MultiplyMV(float[] result,float[] m,float[] v)
	{
		float[] tmpv = new float[]{v[0],v[1],v[2],1f};
		float[] tmpr = new float[4];
		Matrix.multiplyMV(tmpr, 0, m, 0, tmpv, 0);
		result[0] = tmpr[0];
		result[1] = tmpr[1];
		result[2] = tmpr[2];
	}
	
	public static void MultiplyMV(float[] result,float[] m,float[] v,int offset)
	{
		float[] tmpv = new float[]{v[offset],v[offset+1],v[offset+2],1f};
		float[] tmpr = new float[4];
		Matrix.multiplyMV(tmpr, 0, m, 0, tmpv, 0);
		result[0] = tmpr[0];
		result[1] = tmpr[1];
		result[2] = tmpr[2];
		if(result.length==4)
			result[3] = tmpr[3];
	}
	
	public static void copy(float[] arrFrom,int offsetF,float[] arrTo,int offsetT)
	{
		for(int i=0;i<arrFrom.length-offsetF;i++)
			arrTo[i+offsetT] = arrFrom[i+offsetF];
	}
	
	//4*4转置
	public static float[] transport(float[] matrix)
	{
		float[] result = new float[16];
		result[0] = matrix[0];
		result[1] = matrix[4];
		result[2] = matrix[8];
		result[3] = matrix[12];
		
		result[4] = matrix[1];
		result[5] = matrix[5];
		result[6] = matrix[9];
		result[7] = matrix[13];
		
		result[8] = matrix[2];
		result[9] = matrix[6];
		result[10] = matrix[10];
		result[11] = matrix[14];
		
		result[12] = matrix[3];
		result[13] = matrix[7];
		result[14] = matrix[11];
		result[15] = matrix[15];
		
		return result;
	}
	
	//4*4 matrix
	public static float[] reverse(float[] matrix)   
	{
		float[] result = new float[16];
		float[] m = new float[9];

		//计算伴随矩阵
		for(int _i=0;_i<4;_i++)
		{
			for(int _j=0;_j<4;_j++)
			{
				float factor = (_i+_j)%2==0?1:-1;
				float determinant;
				int c = 0;
				for(int i=0;i<4;i++)
				{
					if(i==_i)
						continue;
					for(int j=0;j<4;j++)
					{
						if(j==_j)
							continue;
						m[c++] = matrix[i*4+j];
					}
			}
			determinant = m[0]*m[4]*m[8]+m[3]*m[7]*m[2]+m[6]*m[1]*m[5]
						 -m[2]*m[4]*m[6]-m[5]*m[7]*m[0]-m[8]*m[1]*m[3];
			result[_j*4+_i] = determinant*factor;
			}
		}

		//计算行列式
		float determinant = 
		 matrix[0]*matrix[5]*matrix[10]*matrix[15]-matrix[0]*matrix[5]*matrix[11]*matrix[14]-matrix[0]*matrix[6]*matrix[9]*matrix[15]+matrix[0]*matrix[6]*matrix[11]*matrix[13]
		+matrix[0]*matrix[7]*matrix[9]*matrix[14]-matrix[0]*matrix[7]*matrix[10]*matrix[13]-matrix[1]*matrix[4]*matrix[10]*matrix[15]+matrix[1]*matrix[4]*matrix[11]*matrix[14]
		+matrix[1]*matrix[6]*matrix[8]*matrix[15]-matrix[1]*matrix[6]*matrix[11]*matrix[12]-matrix[1]*matrix[7]*matrix[8]*matrix[14]+matrix[1]*matrix[7]*matrix[10]*matrix[12]
		+matrix[2]*matrix[4]*matrix[9]*matrix[15]-matrix[2]*matrix[4]*matrix[11]*matrix[13]-matrix[2]*matrix[5]*matrix[8]*matrix[15]+matrix[2]*matrix[5]*matrix[11]*matrix[12]
		+matrix[2]*matrix[7]*matrix[8]*matrix[13]-matrix[2]*matrix[7]*matrix[9]*matrix[12]-matrix[3]*matrix[4]*matrix[9]*matrix[14]+matrix[3]*matrix[4]*matrix[10]*matrix[13]
		+matrix[3]*matrix[5]*matrix[8]*matrix[14]-matrix[3]*matrix[5]*matrix[10]*matrix[12]-matrix[3]*matrix[6]*matrix[8]*matrix[13]+matrix[3]*matrix[6]*matrix[9]*matrix[12];
		
		//计算逆矩阵
		for(int i=0;i<16;i++)
			result[i] /= determinant;

		return result;
	}
	
	public static Vector3D getTranslation(float[] m)
	{
		return new Vector3D(m[3],m[7],m[11]);
	}
	
	public static float[] getIdentityMatrix44()
	{
		return new float[]{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	}
}
