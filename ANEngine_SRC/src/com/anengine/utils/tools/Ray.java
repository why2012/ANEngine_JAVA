package com.anengine.utils.tools;

import android.opengl.Matrix;

import com.anengine.core.Camera3D;
import com.anengine.core.Scene3D;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.Vector3D;

public class Ray {
	
	public Vector3D origin = null;//起点
	public Vector3D direction = null;//方向
	
	public Ray()
	{
		origin = new Vector3D();
		direction = new Vector3D();
	}
	
	public Ray(Vector3D origin,Vector3D direction)
	{
		this.origin = origin;
		this.direction = direction.normalize();
	}
	
	public Ray(float[] origin,float[] dir)
	{
		this.origin = new Vector3D(origin);
		this.direction = new Vector3D(dir).normalize();
	}
	
	//由屏幕坐标获取世界坐标系中的一条射线,ANE坐标系,转换为opengl坐标系调用
	//resize(1/UNIT_SIZE)或resize(1/UNIT_SIZE2D)
	public static Ray getRayFromScreen(float x,float y,float[] viewPort,Camera3D camera3d)
	{
		float[] position = unproject(x,y,0,viewPort,camera3d);
		float[] direction = unproject(x,y,1,viewPort,camera3d);
		direction[0] = direction[0] - position[0];
		direction[1] = direction[1] - position[1];
		direction[2] = direction[2] - position[2];
		Ray ray = new Ray(position,direction);
		return ray;
	}
	
	//得到的坐标基于opengl世界坐标系，并没有考虑scaleFactor的影响，为了得到ANE坐标，需要resize(1/UNIT_SIZE)或resize(1/UNIT_SIZE2D)
	public static float[] unproject(float x,float y,float z,float[] viewPort,Camera3D camera3d)
	{
		float[] INVPM = new float[16];
		float[] vec4 = new float[4];
		Matrix.multiplyMM(INVPM, 0, camera3d.getProjectMatrix(), 0, camera3d.getCamera3DMatrix(), 0);
		INVPM = MatrixOperation.reverse(INVPM);
		x = x-viewPort[0];
		y = viewPort[3]-(y-viewPort[1]);
		x = (2*(float)x/viewPort[2]-1);
		y = (2*(float)y/viewPort[3]-1);
		float[] glScreenPos = new float[]{x,y,z,1};
		Matrix.multiplyMV(vec4, 0, INVPM, 0, glScreenPos, 0);
		vec4[0] /= vec4[3];
		vec4[1] /= vec4[3];
		vec4[2] /= vec4[3];
		return new float[]{vec4[0],vec4[1],vec4[2]};
	}
	
	public void resize(float factor)
	{
		origin.nx *= factor;
		origin.ny *= factor;
		origin.nz *= factor;
	}
	
	public Vector3D sphereIntersection(float[] pos,float radius)
	{
		Vector3D vec3 = null;
		Vector3D e = new Vector3D(pos[0]-origin.nx,pos[1]-origin.ny,pos[2]-origin.nz);
		float a = e.product(direction);
		float e2 = e.product(e);
		float t = a - (float)Math.sqrt(radius*radius-e2+a*a);
		vec3 = new Vector3D(origin.nx+t*direction.nx,origin.ny+t*direction.ny,origin.nz+t*direction.nz);
		
		return vec3;
	}
	
	//若射线无长度判定，设置minLen=maxLen=0
	public Vector3D triangleIntersection(float[] p1,float[] p2,float[] p3,float minLen,float maxLen)
	{
		Vector3D vec3 = null;
		Vector3D vector1 = new Vector3D(p2[0]-p1[0],p2[1]-p1[1],p2[2]-p1[2]);
		Vector3D vector2 = new Vector3D(p3[0]-p1[0],p3[1]-p1[1],p3[2]-p1[2]);
		Vector3D normal = vector1.crossProduct(vector2);
		float dot = this.direction.product(normal);
		if(dot>=0)return null;
		float d = normal.product(new Vector3D(p1[0],p1[1],p1[2]));
		float t = d - normal.product(origin);
		if(t>0)return null;
		t /= dot;
		if(t<0)return null;
		if(minLen>0&&t<minLen)return null;
		if(maxLen>0&&t>maxLen)return null;
		vec3 = new Vector3D(origin.nx+t*direction.nx,origin.ny+t*direction.ny,origin.nz+t*direction.nz);
		float u0,u1,u2;
		float v0,v1,v2;
		if(Math.abs(normal.nx)>Math.abs(normal.ny))
		{
			if(Math.abs(normal.nx)>Math.abs(normal.nz))
			{
				u0 = vec3.ny - p1[1];
				u1 = p2[1] - p1[1];
				u2 = p3[1] - p1[1];
				v0 = vec3.nz - p1[2];
				v1 = p2[2] - p1[2];
				v2 = p3[2] - p1[2];
			}
			else
			{
				u0 = vec3.nx - p1[0];
				u1 = p2[0] - p1[0];
				u2 = p3[0] - p1[0];
				v0 = vec3.ny - p1[1];
				v1 = p2[1] - p1[1];
				v2 = p3[1] - p1[1];
			}
		}
		else
		{
			if(Math.abs(normal.ny)>Math.abs(normal.nz))
			{
				u0 = vec3.nx - p1[0];
				u1 = p2[0] - p1[0];
				u2 = p3[0] - p1[0];
				v0 = vec3.nz - p1[2];
				v1 = p2[2] - p1[2];
				v2 = p3[2] - p1[2];
			}
			else
			{
				u0 = vec3.nx - p1[0];
				u1 = p2[0] - p1[0];
				u2 = p3[0] - p1[0];
				v0 = vec3.ny - p1[1];
				v1 = p2[1] - p1[1];
				v2 = p3[1] - p1[1];
			}
		}
		
		float tmp = u1*v2 - v1*u2;
		if(tmp==0)return null;
		tmp = 1/tmp;
		float alpha = (u0*v2-v0*u2)*tmp;
		if(alpha<0)return null;
		float beta = (u1*v0-v1*u0)*tmp;
		if(beta<0)return null;
		float gamma = 1 - alpha - beta;
		if(gamma<0)return null;
	
		return vec3;
	}
	
	public Vector3D planeIntersection(float[] p1,float[] p2,float[] p3)
	{
		Vector3D vec3 = null;
		Vector3D vector1 = new Vector3D(p2[0]-p1[0],p2[1]-p1[1],p2[2]-p1[2]);
		Vector3D vector2 = new Vector3D(p3[0]-p1[0],p3[1]-p1[1],p3[2]-p1[2]);
		Vector3D normal = vector1.crossProduct(vector2);
		float d = normal.product(new Vector3D(p1[0],p1[1],p1[2]));
		float m = this.direction.product(normal);
		if(m>=0)return null;
		float n = this.origin.product(normal);
		float t = (d-n)/m;
		vec3 = new Vector3D(origin.nx+t*direction.nx,origin.ny+t*direction.ny,origin.nz+t*direction.nz);
		return vec3;
	}
	
	public Vector3D boxIntersection(float[] lefttop,float[] rightbottom)
	{
		Vector3D vec3 = null;
		return vec3;
	}
}
