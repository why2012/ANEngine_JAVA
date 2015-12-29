package com.anengine.collisions;

import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.Vector3D;
import com.anengine.utils.tools.Ray;

public class AABB3 {
	public Vector3D boundMin = null;
	public Vector3D boundMax = null;
	
	public AABB3()
	{
		boundMin = new Vector3D(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
		boundMax = new Vector3D(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
	}
	
	public AABB3(float minX,float minY,float minZ,float maxX,float maxY,float maxZ)
	{
		boundMin = new Vector3D(minX,minY,minZ);
		boundMax = new Vector3D(maxX,maxY,maxZ);
	}
	
	public AABB3(Vector3D min,Vector3D max)
	{
		boundMin = min;
		boundMax = max;
	}
	
	public AABB3 clone()
	{
		AABB3 tmp = new AABB3(boundMin.clone(),boundMax.clone());
		return tmp;
	}
	
	public void addVertices(float[] vertices)
	{
		for(int i=0;i<vertices.length;i+=3)
		{
			if(vertices[i]<boundMin.nx)boundMin.nx=vertices[i];
			if(vertices[i]>boundMax.nx)boundMax.nx=vertices[i];
			if(vertices[i+1]<boundMin.ny)boundMin.ny=vertices[i+1];
			if(vertices[i+1]>boundMax.ny)boundMax.ny=vertices[i+1];
			if(vertices[i+2]<boundMin.nz)boundMin.nz=vertices[i+2];
			if(vertices[i+2]>boundMax.nz)boundMax.nz=vertices[i+2];
		}
	}
	
	public void addAABB3(AABB3 box)
	{
		if(box.boundMin.nx<boundMin.nx)boundMin.nx=box.boundMin.nx;
		if(box.boundMax.nx>boundMax.nx)boundMax.nx=box.boundMax.nx;
		if(box.boundMin.ny<boundMin.ny)boundMin.ny=box.boundMin.ny;
		if(box.boundMax.ny>boundMax.ny)boundMax.ny=box.boundMax.ny;
		if(box.boundMin.nz<boundMin.nz)boundMin.nz=box.boundMin.nz;
		if(box.boundMax.nz>boundMax.nz)boundMax.nz=box.boundMax.nz;
	}
	
	public void empty()
	{
		boundMin = new Vector3D(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
		boundMax = new Vector3D(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
	}
	
	public boolean isEmpty()
	{
		return (boundMin.nx>boundMax.nx)||(boundMin.ny>boundMax.ny)||(boundMin.nz>boundMax.nz);
	}
	
	//由一组点获取其在xyz轴上的最小值与最大值
	public float[] getMinMax(float[] vertices)
	{
		float[] result = new float[6];
		result[0] = Float.POSITIVE_INFINITY;result[1] = Float.NEGATIVE_INFINITY;
		result[2] = Float.POSITIVE_INFINITY;result[3] = Float.NEGATIVE_INFINITY;
		result[4] = Float.POSITIVE_INFINITY;result[5] = Float.NEGATIVE_INFINITY;
		for(int i=0;i<vertices.length;i+=3)
		{
			if(vertices[i]<result[0])result[0]=vertices[i];
			if(vertices[i]>result[1])result[1]=vertices[i];
			
			if(vertices[i+1]<result[2])result[2]=vertices[i+1];
			if(vertices[i+1]>result[3])result[3]=vertices[i+1];
			
			if(vertices[i+2]<result[4])result[4]=vertices[i+2];
			if(vertices[i+2]>result[5])result[5]=vertices[i+2];
		}
		
		return result;
	}
	
	//平移与旋转 
	//m为平移与旋转矩阵
	public void transform(float[] m)
	{
		if(this.isEmpty())
		{
			empty();
			return;
		}
		float[][] vertices = new float[][]{
			{boundMin.nx,boundMin.ny,boundMin.nz},
			{boundMax.nx,boundMin.ny,boundMin.nz},
			{boundMin.nx,boundMax.ny,boundMin.nz},
			{boundMin.nx,boundMin.ny,boundMax.nz},
			{boundMax.nx,boundMax.ny,boundMin.nz},
			{boundMax.nx,boundMin.ny,boundMax.nz},
			{boundMin.nx,boundMax.ny,boundMax.nz},
			{boundMax.nx,boundMax.ny,boundMax.nz}
		};
		float[] new_vertices = new float[24];
		int c = 0;
		for(int i=0;i<vertices.length;i++)
		{
			float[] result = new float[3];
			MatrixOperation.MultiplyMV(result, m, vertices[i]);
			new_vertices[c++] = result[0];
			new_vertices[c++] = result[1];
			new_vertices[c++] = result[2];
		}
		float[] minmax = getMinMax(new_vertices);
		boundMin.nx = minmax[0];
		boundMax.nx = minmax[1];
		boundMin.ny = minmax[2];
		boundMax.ny = minmax[3];
		boundMin.nz = minmax[4];
		boundMax.nz = minmax[5];
	}
	
	//newPos为移动方向及距离
	public void translate(float[] newPos)
	{
		boundMin.nx += newPos[0];
		boundMax.nx += newPos[0];
		boundMin.ny += newPos[1];
		boundMax.ny += newPos[1];
		boundMin.nz += newPos[2];
		boundMax.nz += newPos[2];
	}
	
	//静态AABB相交测试，boxIntersect为重叠部分，为null则不计算
	public boolean intersectAABBs(AABB3 box,AABB3 boxIntersect)
	{
		if(boundMin.nx>box.boundMax.nx)return false;
		if(boundMax.nx<box.boundMin.nx)return false;
		if(boundMin.ny>box.boundMax.ny)return false;
		if(boundMax.ny<box.boundMin.ny)return false;
		if(boundMin.nz>box.boundMax.nz)return false;
		if(boundMax.nz<box.boundMin.nz)return false;
		
		if(boxIntersect!=null)
		{
			boxIntersect.boundMin.nx = Math.max(boundMin.nx, box.boundMin.nx);
			boxIntersect.boundMax.nx = Math.min(boundMax.nx, box.boundMax.nx);
			boxIntersect.boundMin.ny = Math.max(boundMin.ny, box.boundMin.ny);
			boxIntersect.boundMax.ny = Math.min(boundMax.ny, box.boundMax.ny);
			boxIntersect.boundMin.nz = Math.max(boundMin.nz, box.boundMin.nz);
			boxIntersect.boundMax.nz = Math.min(boundMax.nz, box.boundMax.nz);
		}
		return true;
	}
	
	//动态AABB相交测试,d为移动方向及距离,不相交返回-1，相交返回相交点系数(0-1)
	public float intersectMovingAABB(AABB3 staticBox,float[] d)
	{
		float tEnter = 0;
		float tLeave = 1;
		
		if(d[0]==0)
		{
			if((staticBox.boundMin.nx>=boundMax.nx)||(staticBox.boundMax.nx<=boundMin.nx))
				return -1;
		}
		else
		{
			float oneOverD = 1.0f/d[0];
			float xEnter = (staticBox.boundMin.nx-boundMax.nx)/oneOverD;
			float xLeave = (staticBox.boundMax.nx-boundMin.nx)/oneOverD;
			if(xEnter>xLeave)
			{
				float t = xEnter;
				xEnter = xLeave;
				xLeave = t;
			}
			if(xEnter>tEnter)tEnter=xEnter;
			if(xLeave<tLeave)tLeave=xLeave;
			if(tEnter>tLeave)return -1;
		}
		
		if(d[1]==0)
		{
			if((staticBox.boundMin.ny>=boundMax.ny)||(staticBox.boundMax.ny<=boundMin.ny))
				return -1;
		}
		else
		{
			float oneOverD = 1.0f/d[1];
			float yEnter = (staticBox.boundMin.ny-boundMax.ny)/oneOverD;
			float yLeave = (staticBox.boundMax.ny-boundMin.ny)/oneOverD;
			if(yEnter>yLeave)
			{
				float t = yEnter;
				yEnter = yLeave;
				yLeave = t;
			}
			if(yEnter>tEnter)tEnter=yEnter;
			if(yLeave<tLeave)tLeave=yLeave;
			if(tEnter>tLeave)return -1;
		}
		
		if(d[2]==0)
		{
			if((staticBox.boundMin.nz>=boundMax.nz)||(staticBox.boundMax.nz<=boundMin.nz))
				return -1;
		}
		else
		{
			float oneOverD = 1.0f/d[2];
			float zEnter = (staticBox.boundMin.nz-boundMax.nz)/oneOverD;
			float zLeave = (staticBox.boundMax.nz-boundMin.nz)/oneOverD;
			if(zEnter>zLeave)
			{
				float t = zEnter;
				zEnter = zLeave;
				zLeave = t;
			}
			if(zEnter>tEnter)tEnter=zEnter;
			if(zLeave<tLeave)tLeave=zLeave;
			if(tEnter>tLeave)return -1;
		}
		
		return tEnter;
	}
	
	//len为射线长度
	public Vector3D rayIntersect(Ray ray,float len)
	{
		Vector3D vec3 = null;
		
		ray.direction = ray.direction.normalize().scale(len).add(ray.origin);
		boolean inside = true;
		//检查点在矩形边界框内的情况，并计算到每个面的距离
		float xt,xn=0;
		if(ray.origin.nx<boundMin.nx)
		{
			xt = boundMin.nx-ray.origin.nx;
			if(xt>ray.direction.nx)return null;
			xt /= ray.direction.nx;
			inside = false;
			xn = -1.0f;
		}
		else if(ray.origin.nx>boundMax.nx)
		{
			xt = boundMax.nx-ray.origin.nx;
			if(xt<ray.direction.nx)return null;
			xt /= ray.direction.nx;
			inside = false;
			xn = 1.0f;
		}
		else
		{
			xt = -1.0f;
		}
		
		float yt,yn=0;
		if(ray.origin.ny<boundMin.ny)
		{
			yt = boundMin.ny-ray.origin.ny;
			if(yt>ray.direction.ny)return null;
			yt /= ray.direction.ny;
			inside = false;
			yn = -1.0f;
		}
		else if(ray.origin.ny>boundMax.ny)
		{
			yt = boundMax.ny-ray.origin.ny;
			if(yt<ray.direction.ny)return null;
			yt /= ray.direction.ny;
			inside = false;
			yn = 1.0f;
		}
		else
		{
			yt = -1.0f;
		}
		
		float zt,zn=0;
		if(ray.origin.nz<boundMin.nz)
		{
			zt = boundMin.nz-ray.origin.nz;
			if(zt>ray.direction.nz)return null;
			zt /= ray.direction.nz;
			inside = false;
			zn = -1.0f;
		}
		else if(ray.origin.nz>boundMax.nz)
		{
			zt = boundMax.nz-ray.origin.nz;
			if(zt<ray.direction.nz)return null;
			zt /= ray.direction.nz;
			inside = false;
			zn = 1.0f;
		}
		else
		{
			zt = -1.0f;
		}
		
		if(inside)
		{
			vec3 = ray.direction.negative().normalize();
			return vec3;
		}
		
		int which = 0;
		float t = xt;
		if(yt>t)
		{
			which=1;
			t = yt;
		}
		if(zt>t)
		{
			which = 2;
			t = zt;
		}
		
		switch(which)
		{
			case 0:
			{
				float y = ray.origin.ny+ray.direction.ny*t;
				if(y<boundMin.ny||y>boundMax.ny)return null;
				float z = ray.origin.nz+ray.direction.nz*t;
				if(z<boundMin.nz||z>boundMax.nz)return null;
				vec3 = new Vector3D();
				vec3.nx = xn;
				vec3.ny = 0;
				vec3.nz = 0;
			}
				break;
			case 1:
			{
				float x = ray.origin.nx+ray.direction.nx*t;
				if(x<boundMin.nx||x>boundMax.nx)return null;
				float z = ray.origin.nz+ray.direction.nz*t;
				if(z<boundMin.nz||z>boundMax.nz)return null;
				vec3 = new Vector3D();
				vec3.nx = 0;
				vec3.ny = yn;
				vec3.nz = 0;
			}
				break;
			case 2:
			{
				float x = ray.origin.nx+ray.direction.nx*t;
				if(x<boundMin.nx||x>boundMax.nx)return null;
				float y = ray.origin.ny+ray.direction.ny*t;
				if(y<boundMin.ny||y>boundMax.ny)return null;
				vec3 = new Vector3D();
				vec3.nx = 0;
				vec3.ny = 0;
				vec3.nz = zn;
			}
				break;
		}
		
		return vec3;
	}
}
