package com.anengine.primitives;

import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_gl.Constant;

public class Plane extends Mesh{
	
	private float width,height;
	int segmentW=1,segmentH=1;
	private SubMesh subMesh;
	
	public Plane(float width,float height)
	{
		this.width = width*Constant.UNIT_SIZE;
		this.height = height*Constant.UNIT_SIZE;
		this.prepareVertices();
	}
	
	public Plane(float width,float height,int segmentW,int segmentH)
	{
		this.width = width*Constant.UNIT_SIZE;
		this.height = height*Constant.UNIT_SIZE;
		this.segmentW = segmentW;
		this.segmentH = segmentH;
		this.prepareVertices();
	}
	
	private void prepareVertices()
	{
		float[] vertices = null;
		float[] texCoord = null;//纹理坐标
		short[] indices = null;
		float[] normal = null;//法向量
		int vCount = 0;//顶点数目
		int iCount = 0;//索引数目
		
		this.subMesh = new SubMesh();
		this.subMeshes.add(subMesh);
		
		vCount = (segmentW+1)*(segmentH+1);
		iCount = segmentW*segmentH*6;
		vertices = new float[vCount*3];
		normal = new float[vCount*3];
		indices = new short[iCount];
		texCoord = new float[vCount*2];
		
		float sx = -this.width/2;
		float sy = this.height/2;
		float unitW = this.width/segmentW;
		float unitH = this.height/segmentH;
		float unitTexS = 1f/segmentW;
		float unitTexT = 1f/segmentH;
		
		for(int row=0,i=0,t=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[i] = sx+col*unitW;
				vertices[i+1] = sy-row*unitH;
				vertices[i+2] = 0;
				normal[i] = 0;
				normal[i+1] = 0;
				normal[i+2] = 1;
				texCoord[t] = col*unitTexS;
				texCoord[t+1] = row*unitTexT;
				i+=3;
				t+=2;
			}
		}
		
		for(int row=0,i=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(row*(segmentW+1)+col);
				indices[i+1] = (short)((row+1)*(segmentW+1)+col);
				indices[i+2] = (short)((row+1)*(segmentW+1)+col+1);
				indices[i+3] = (short)((row+1)*(segmentW+1)+col+1);
				indices[i+4] = (short)(row*(segmentW+1)+col+1);
				indices[i+5] = (short)(row*(segmentW+1)+col);
				i+=6;
			}
		}
		
		this.subMesh.setVertices(vertices);
		this.subMesh.setTexCoord(texCoord);
		this.subMesh.setIndices(indices);
		this.subMesh.setNormal(normal);
	}
}
