package com.anengine.independence.base_math;

public class Quaternion {
	
	public float x,y,z,w;
	
	public Quaternion()
	{
		w = 1;
		x = y = z =0;
	}
	
	public Quaternion(float x,float y,float z,float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	//单位四元数
	public Quaternion(float x,float y,float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		float t = x*x-y*y-z*z;
		if(t<0)
			this.w = 0;
		else
			this.w = -(float)Math.sqrt(1.0-x*x-y*y-z*z);
	}
	
	public Quaternion clone()
	{
		return new Quaternion(x,y,z,w);
	}
	
	public Quaternion inverse()
	{
		float len = (float)Math.sqrt(x*x+y*y+z*z+w*w);
		return new Quaternion(-x/len,-y/len,-z/len,w/len);
	}
	
	public Quaternion normalize()
	{
		float len = (float)Math.sqrt(x*x+y*y+z*z+w*w);
		x /= len;
		y /= len;
		z /= len;
		w /= len;
		return this;
	}
	
	//this*qua
	public void Multiply(Quaternion result,Quaternion qua)
	{
		float xr,yr,zr,wr;
		wr = w*qua.w-x*qua.x-y*qua.y-z*qua.z;
		xr = w*qua.x+x*qua.w+y*qua.z-z*qua.y;
		yr = w*qua.y+y*qua.w+z*qua.x-x*qua.z;
		zr = w*qua.z+z*qua.w+x*qua.y-y*qua.x;
		result.x = xr;
		result.y = yr;
		result.z = zr;
		result.w = wr;
	}
	
	//inverse_this*vec3*this
	public void Multiply(float[] vec3_r,float[] vec3)
	{
		float x2=vec3[0],y2=vec3[1],z2=vec3[2];
		float len = (float)Math.sqrt(x*x+y*y+z*z+w*w);
		float ix = -x/len,iy = -y/len,iz = -z/len,iw = w/len;
		float[] r = new float[4];
	
		r[0] = iw*x2 + iy*z2 - iz*y2;
		r[1] = iw*y2 - ix*z2 + iz*x2;
		r[2] = iw*z2 + ix*y2 - iy*x2;
		r[3] = -ix*x2 - iy*y2 - iz*z2;
		
		//wr = r[3]*w-r[0]*x-r[1]*y-r[2]*z;
		vec3_r[0] = r[3]*x+r[0]*w+r[1]*z-r[2]*y;
		vec3_r[1] = r[3]*y+r[1]*w+r[2]*x-r[0]*z;
		vec3_r[2] = r[3]*z+r[2]*w+r[0]*y-r[1]*x;
	}
	
	//由4*4列矩阵获取四元数
	public static Quaternion createFromMatrix(float[] matrix)
	{
		Quaternion quat = null;
		float m11=matrix[0],m12=matrix[4],m13=matrix[8];
		float m21=matrix[1],m22=matrix[5],m23=matrix[9];
		float m31=matrix[2],m32=matrix[6],m33=matrix[10];
		
		float w=1,x=0,y=0,z=0;
		
		float WS = m11 + m22 + m33;
		float XS = m11 - m22 - m33;
		float YS = m22 - m11 - m33;
		float ZS = m33 - m11 - m22;
		int biggestIndex = 0;
		float biggestS = WS;
		if(XS>biggestS)
		{
			biggestS = XS;
			biggestIndex = 1;
		}
		if(YS>biggestS)
		{
			biggestS = YS;
			biggestIndex = 2;
		}
		if(ZS>biggestS)
		{
			biggestS = ZS;
			biggestIndex = 3;
		}
		
		float biggestVal = (float)(Math.sqrt(biggestS+1)*0.5);
		float mult = 0.25f/biggestVal;
		
		switch(biggestIndex)
		{
			case 0:
				w = biggestVal;
				x = (m23-m32)*mult;
				y = (m31-m13)*mult;
				z = (m12-m21)*mult;
			break;
			case 1:
				x = biggestVal;
				w = (m23-m32)*mult;
				y = (m12+m21)*mult;
				z = (m31+m13)*mult;
			break;
			case 2:
				y = biggestVal;
				w = (m31-m13)*mult;
				x = (m12+m21)*mult;
				z = (m23+m32)*mult;
			break;
			case 3:
				z = biggestVal;
				w = (m12-m21)*mult;
				x = (m31+m13)*mult;
				y = (m23+m32)*mult;
			break;
		}
		
		quat = new Quaternion(x,y,z,w);
		
		return quat;
	}
	
	//球面线性插值 四元数
	public static void slerp(Quaternion result,Quaternion q1,Quaternion q2,float t)
	{
		float cosOmega = q1.x*q2.x+q1.y*q2.y+q1.z*q2.z+q1.w*q2.w;
		float x1=q2.x,y1=q2.y,z1=q2.z,w1=q2.w;
		float k0,k1;
		if(cosOmega<0.0f)
		{
			x1 = -x1;
			y1 = -y1;
			z1 = -z1;
			w1 = -w1;
			cosOmega = -cosOmega;
		}
		if(cosOmega>0.9999f)//非常接近则采用线性插值
		{
			k0 = 1 - t;
			k1 = t;
		}
		else
		{
			float sinOmega = (float)Math.sqrt(1.0f - cosOmega*cosOmega);
			float omega = (float)Math.atan2(sinOmega, cosOmega);
			float oneOverSinOmega = 1.0f/sinOmega;
			k0 = (float)Math.sin((1.0f-t)*omega)*oneOverSinOmega;
			k1 = (float)Math.sin(t*omega)*oneOverSinOmega;
		}
		
		result.x = q1.x*k0+x1*k1;
		result.y = q1.y*k0+y1*k1;
		result.z = q1.z*k0+z1*k1;
		result.w = q1.w*k0+w1*k1;
	}
	
	//球面线性插值 向量
	public static void slerp(float[] result,float[] v1,float[] v2,float t)
	{
		float x0=v1[0],y0=v1[1],z0=v1[2],w0=0;
		float x1=v2[0],y1=v2[1],z1=v2[2],w1=0;
		float cosOmega = x0*x1+y0*y1+z0*z1+w0*w1;
		
		float k0,k1;
		if(cosOmega<0.0f)
		{
			x1 = -x1;
			y1 = -y1;
			z1 = -z1;
			w1 = -w1;
			cosOmega = -cosOmega;
		}
		if(cosOmega>0.9999f)//非常接近则采用线性插值
		{
			k0 = 1 - t;
			k1 = t;
		}
		else
		{
			float sinOmega = (float)Math.sqrt(1.0f - cosOmega*cosOmega);
			float omega = (float)Math.atan2(sinOmega, cosOmega);
			float oneOverSinOmega = 1.0f/sinOmega;
			k0 = (float)Math.sin((1.0f-t)*omega)*oneOverSinOmega;
			k1 = (float)Math.sin(t*omega)*oneOverSinOmega;
		}
		
		result[0] = x0*k0+x1*k1;
		result[1] = y0*k0+y1*k1;
		result[2] = z0*k0+z1*k1;
	}
	
	public String toString()
	{
		return "Quaternion(x:"+x+" , y:"+y+" , z:"+z+" , w:"+w+" )";
	}
}
