package com.anengine.primitives;

import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_data.ANE_ArrayList;
import com.anengine.independence.base_gl.Constant;

public class Sphere extends Mesh {
	private float R = 1*Constant.UNIT_SIZE;
	private float angleSpan = (float)Math.PI/15;
	private int type = 1;//1:于极点汇拢   2：平滑
	private SubMesh subMesh;
	
	public Sphere(float x,float y,float z,float R)
	{
		this.init(x, y, z, R, (float)Math.PI/15, 1);
		this.prepareVertices();
	}
	
	public Sphere(float x,float y,float z,float R,int type)
	{
		this.init(x, y, z, R, (float)Math.PI/15, type);
		this.prepareVertices();
	}
	
	public Sphere(float x,float y,float z,float R,float angleSpan)
	{
		this.init(x, y, z, R, angleSpan, 1);
		this.prepareVertices();
	}
	
	public Sphere(float x,float y,float z,float R,float angleSpan,int type)
	{
		this.init(x, y, z, R, angleSpan, type);
		this.prepareVertices();
	}
	
	private void init(float x,float y,float z,float R,float angleSpan,int type)
	{
		this.XYZ[0] = x;
		this.XYZ[1] = y;
		this.XYZ[2] = z;
		this.R = R;
		this.angleSpan = angleSpan;
		this.type = type;
	}
	
	private void prepareVertices()
	{
		float[] _vertices = null;
		float[] texCoord = null;//纹理坐标
		short[] _indices = null;
		float[] normal = null;//法向量
		int vCount,iCount;
		
		this.subMesh = new SubMesh();
		this.subMeshes.add(subMesh);
		
		ANE_ArrayList<Float> vertices = new ANE_ArrayList<Float>();
		ANE_ArrayList<Short> indices = new ANE_ArrayList<Short>();
		ANE_ArrayList<Short> texCoords = new ANE_ArrayList<Short>();
		float unitR = R*Constant.UNIT_SIZE;
		int bw = 0;
		int bh = 0;
		if(type==1)
		{
			short i = 0;
			for(float vAngle=(float)-Math.PI/2;vAngle<Math.PI/2;vAngle+=angleSpan,bh++)
			{
				for(float hAngle=0;hAngle<=Math.PI*2;hAngle+=angleSpan,bw++)
				{
					float x0 = (float)(unitR*Math.cos(vAngle)*Math.cos(hAngle));
					float y0 = (float)(unitR*Math.cos(vAngle)*Math.sin(hAngle));
					float z0 = (float)(unitR*Math.sin(vAngle));
					
					float x1 = (float)(unitR*Math.cos(vAngle)*Math.cos(hAngle+angleSpan));
					float y1 = (float)(unitR*Math.cos(vAngle)*Math.sin(hAngle+angleSpan));
					float z1 = (float)(unitR*Math.sin(vAngle));
					
					float x2 = (float)(unitR*Math.cos(vAngle+angleSpan)*Math.cos(hAngle+angleSpan));
					float y2 = (float)(unitR*Math.cos(vAngle+angleSpan)*Math.sin(hAngle+angleSpan));
					float z2 = (float)(unitR*Math.sin(vAngle+angleSpan));
					
					float x3 = (float)(unitR*Math.cos(vAngle+angleSpan)*Math.cos(hAngle));
					float y3 = (float)(unitR*Math.cos(vAngle+angleSpan)*Math.sin(hAngle));
					float z3 = (float)(unitR*Math.sin(vAngle+angleSpan));
					
					vertices.add(x0);vertices.add(y0);vertices.add(z0);
					vertices.add(x1);vertices.add(y1);vertices.add(z1);
					vertices.add(x2);vertices.add(y2);vertices.add(z2);
					vertices.add(x3);vertices.add(y3);vertices.add(z3);
					
					indices.add((short)(i+1));indices.add((short)(i+3));indices.add((short)(i+0));
					indices.add((short)(i+1));indices.add((short)(i+2));indices.add((short)(i+3));
					i+=4;
				}
			}
			
			texCoord = new float[bw*4*2];
			int wh = bw/bh;
			float sizew = 1.0f/wh;
			float sizeh = 1.0f/bh;
			int c = 0;
			for(int n=0;n<bh;n++)
			{
				for(int j=0;j<wh;j++)
				{
					float s = j*sizew;
					float t = n*sizeh;
					texCoord[c++] = s;
					texCoord[c++] = t;
					
					texCoord[c++] = s;
					texCoord[c++] = t+sizeh;
					
					texCoord[c++] = s+sizew;
					texCoord[c++] = t+sizeh;
					
					texCoord[c++] = s+sizew;
					texCoord[c++] = t;					
				}
			}
		
			vCount = vertices.length()/3;
			iCount = indices.length();
			
			_vertices = new float[vCount*3];
			_indices = new short[iCount];
			//this.normal = new float[vCount*3];
			
			for(int n=0;n<vCount*3;n++)
				_vertices[n] = vertices.get(n);
			
			for(int n=0;n<iCount;n++)
				_indices[n] = indices.get(n);
			
			normal = _vertices.clone();
			
			this.subMesh.setVertices(_vertices);
			this.subMesh.setTexCoord(texCoord);
			this.subMesh.setIndices(_indices);
			this.subMesh.setNormal(normal);
			
			vertices.clear();
			indices.clear();
		}
	}
}
