package com.anengine.independence.base_math;

import java.util.Set;

public class Vector3D {
	public static final float DIFF = 0.0000001f;
	public float nx=0,ny=0,nz=0;
	
	public Vector3D(){}
	
	public Vector3D(float nx,float ny,float nz)
	{
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}
	
	public Vector3D(float[] xyz)
	{
		this.nx = xyz[0];
		this.ny = xyz[1];
		this.nz = xyz[2];
	}
	
	public Vector3D clone()
	{
		return new Vector3D(nx,ny,nz);
	}
	
	public Vector3D scale(float factor)
	{
		return new Vector3D(nx*factor,ny*factor,nz*factor);
	}
	
	public Vector3D negative()
	{
		return new Vector3D(-nx,-ny,-nz);
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Vector3D)
		{
			Vector3D tn = (Vector3D)o;
			if(Math.abs(nx-tn.nx)<DIFF&&
			   Math.abs(ny-tn.ny)<DIFF&&
			   Math.abs(nz-tn.nz)<DIFF)
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public int hashCode()
	{
		return 1;
	}

	public Vector3D normalize()
	{
		float len = (float)Math.sqrt(nx*nx+ny*ny+nz*nz);
		return new Vector3D(nx/len,ny/len,nz/len);
	}
	
	public Vector3D add(Vector3D vector)
	{
		return new Vector3D(nx+vector.nx,ny+vector.ny,nz+vector.nz);
	}
	
	public Vector3D minus(Vector3D vector)
	{
		return new Vector3D(nx-vector.nx,ny-vector.ny,nz-vector.nz);
	}
	
	public float product(Vector3D vector)
	{
		return nx*vector.nx+ny*vector.ny+nz*vector.nz;
	}
	
	public Vector3D crossProduct(Vector3D vector)
	{
		Vector3D result = new Vector3D();
		
		result.nx = ny*vector.nz-vector.ny*nz;
		result.ny = nz*vector.nx-vector.nz*nx;
		result.nz = nx*vector.ny-vector.nx*ny;
		
		return result;
	}
	
	public float[] asFloat()
	{
		return new float[]{nx,ny,nz};
	}
	
	public float length()
	{
		return (float)Math.sqrt(nx*nx+ny*ny+nz*nz);
	}
	
	//规格化
	public static float[] vectorNormal(float[] vector)
	{
		float module = (float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}
	
	//点乘
	public static float getProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		return x1*x2+y1*y2+z1*z2;
	}
	
	//叉积
	public static Vector3D getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		Vector3D result = new Vector3D();
		
		result.nx = y1*z2-y2*z1;
		result.ny = z1*x2-z2*x1;
		result.nz = x1*y2-x2*y1;
		
		return result;
	}
	
	public static float[] getAverage(Set<Vector3D> sn)
	{
		float[] result = new float[3];
		for(Vector3D n:sn)
		{
			result[0] += n.nx;
			result[1] += n.ny;
			result[2] += n.nz;
		}
		
		return vectorNormal(result);
	}
	
	public String toString()
	{
		return "Vector3D( "+nx+" , "+ny+" , "+nz+" )";
	}
}
