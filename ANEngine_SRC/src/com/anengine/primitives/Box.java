package com.anengine.primitives;

import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_gl.Constant;

public class Box extends Mesh {
	private float width=3*Constant.UNIT_SIZE,height=3*Constant.UNIT_SIZE,depth=3*Constant.UNIT_SIZE;
	private int segmentW=1,segmentH=1,segmentD=1;
	private SubMesh subMesh;
	
	public Box(float x,float y,float z,float width,float height,float depth)
	{
		this.XYZ[0] = x;
		this.XYZ[1] = y;
		this.XYZ[2] = z;
		this.width = width*Constant.UNIT_SIZE;
		this.height = height*Constant.UNIT_SIZE;
		this.depth = depth*Constant.UNIT_SIZE;
		prepareVertices();
	}
	
	public Box(float x,float y,float z,float width,float height,float depth,int segment)
	{
		this.XYZ[0] = x;
		this.XYZ[1] = y;
		this.XYZ[2] = z;
		this.width = width*Constant.UNIT_SIZE;
		this.height = height*Constant.UNIT_SIZE;
		this.depth = depth*Constant.UNIT_SIZE;
		this.segmentW = segment;
		this.segmentH = segment;
		this.segmentD = segment;
		prepareVertices();
	}
	
	public float width()
	{
		return this.width/Constant.UNIT_SIZE;
	}
	
	public float height()
	{
		return this.height/Constant.UNIT_SIZE;
	}
	
	public float depth()
	{
		return this.depth/Constant.UNIT_SIZE;
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
		vCount = (segmentW+1)*(segmentH+1)*6;
		iCount = segmentW*segmentH*6*6;
		vertices = new float[vCount*3];
		normal = new float[vCount*3];
		indices = new short[iCount];
		texCoord = new float[vCount*2];
		
		float unitW = this.width/segmentW;
		float unitH = this.height/segmentH;
		
		int v = 0,i = 0,t = 0,offset = 0;
		float texUnitS = 1f/(segmentW*4);
		float texUnitT = 1f/(segmentW*3);
		//front
		float sx = this.width/2;
		float sy = this.height/2;
		float sz = this.depth/2;
		float texS = 1/4f;
		float texT = 1/3f;
		
		for(int row=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx-col*unitW;
				vertices[v+1] = sy-row*unitH;
				vertices[v+2] = sz;
				normal[v] = 0;
				normal[v+1] = 0;
				normal[v+2] = 1;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
		
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)((row+1)*(segmentW+1)+col+1);
				indices[i+1] = (short)((row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(row*(segmentW+1)+col);
				indices[i+3] = (short)(row*(segmentW+1)+col);
				indices[i+4] = (short)(row*(segmentW+1)+col+1);
				indices[i+5] = (short)((row+1)*(segmentW+1)+col+1);
				i+=6;
			}
		}
		offset = v/3;
		
		//back
		sx = -this.width/2;
		sy = this.height/2;
		sz = -this.depth/2;
		texS = 3/4f;
		texT = 1/3f;
				
		for(int row=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx+col*unitW;
				vertices[v+1] = sy-row*unitH;
				vertices[v+2] = sz;
				normal[v] = 0;
				normal[v+1] = 0;
				normal[v+2] = -1;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
				
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+1] = (short)(offset+(row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(offset+row*(segmentW+1)+col);
				indices[i+3] = (short)(offset+row*(segmentW+1)+col);
				indices[i+4] = (short)(offset+row*(segmentW+1)+col+1);
				indices[i+5] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				i+=6;
			}
		}
		offset = v/3;
		
		//left
		sx = this.width/2;
		sy = this.height/2;
		sz = -this.depth/2;
		texS = 0;
		texT = 1/3f;
				
		for(int row=0;row<segmentW+1;row++)
		{ 
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx;
				vertices[v+1] = sy-row*unitH;
				vertices[v+2] = sz+col*unitW;
				normal[v] = 1;
				normal[v+1] = 0;
				normal[v+2] = 0;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
				
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+1] = (short)(offset+(row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(offset+row*(segmentW+1)+col);
				indices[i+3] = (short)(offset+row*(segmentW+1)+col);
				indices[i+4] = (short)(offset+row*(segmentW+1)+col+1);
				indices[i+5] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				i+=6;
			}
		}
		offset = v/3;
		
		//right
		sx = -this.width/2;
		sy = this.height/2;
		sz = this.depth/2;
		texS = 2/4f;
		texT = 1/3f;
						
		for(int row=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx;
				vertices[v+1] = sy-row*unitH;
				vertices[v+2] = sz-col*unitW;
				normal[v] = -1;
				normal[v+1] = 0;
				normal[v+2] = 0;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
						
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+1] = (short)(offset+(row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(offset+row*(segmentW+1)+col);
				indices[i+3] = (short)(offset+row*(segmentW+1)+col);
				indices[i+4] = (short)(offset+row*(segmentW+1)+col+1);
				indices[i+5] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				i+=6;
			}
		}
		offset = v/3;
		
		//top
		sx = -this.width/2;
		sy = this.height/2;
		sz = this.depth/2;
		texS = 1/4f;
		texT = 0;
								
		for(int row=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx+row*unitH;
				vertices[v+1] = sy;
				vertices[v+2] = sz-col*unitW;
				normal[v] = 0;
				normal[v+1] = 1;
				normal[v+2] = 0;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
								
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(offset+row*(segmentW+1)+col);
				indices[i+1] = (short)(offset+(row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+3] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+4] = (short)(offset+row*(segmentW+1)+col+1);
				indices[i+5] = (short)(offset+row*(segmentW+1)+col);
				i+=6;
			}
		}
		offset = v/3;
		
		//bottom
		sx = -this.width/2;
		sy = -this.height/2;
		sz = -this.depth/2;
		texS = 1/4f;
		texT = 2/3f;
										
		for(int row=0;row<segmentW+1;row++)
		{
			for(int col=0;col<segmentH+1;col++)
			{
				vertices[v] = sx+row*unitH;
				vertices[v+1] = sy;
				vertices[v+2] = sz+col*unitW;
				normal[v] = 0;
				normal[v+1] = -1;
				normal[v+2] = 0;
				v+=3;
				texCoord[t] = texS+col*texUnitS;
				texCoord[t+1] = texT+row*texUnitT;
				t+=2;
			}
		}
										
		for(int row=0;row<segmentW;row++)
		{
			for(int col=0;col<segmentH;col++)
			{
				indices[i] = (short)(offset+row*(segmentW+1)+col);
				indices[i+1] = (short)(offset+(row+1)*(segmentW+1)+col);
				indices[i+2] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+3] = (short)(offset+(row+1)*(segmentW+1)+col+1);
				indices[i+4] = (short)(offset+row*(segmentW+1)+col+1);
				indices[i+5] = (short)(offset+row*(segmentW+1)+col);
				i+=6;
			}
		}
		offset = v/3;
		
		this.subMesh.setVertices(vertices);
		this.subMesh.setTexCoord(texCoord);
		this.subMesh.setIndices(indices);
		this.subMesh.setNormal(normal);
	}
}
