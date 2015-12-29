package com.anengine.primitives;

import com.anengine.core.ObjectContainer3D;
import com.anengine.entity.Mesh;

public class PlaneBox extends ObjectContainer3D {
	private int segmentW=1,segmentH=1,segmentD=1;
	private Mesh frontFace,backFace,leftFace,rightFace,topFace,bottomFace;
	private float width=3,height=3,depth=3;

	public PlaneBox(float x,float y,float z,float width,float height,float depth,boolean skybox)
	{
		super(x,y,z);
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		this.prepareVertices(skybox);
	}
	
	public PlaneBox(float x,float y,float z,float width,float height,float depth,int segmentW,int segmentH,int segmentD,boolean skybox)
	{
		super(x,y,z);
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.segmentW = segmentW;
		this.segmentH = segmentH;
		this.segmentD = segmentD;
		
		this.prepareVertices(skybox);
	}
	
	public Mesh frontFace()
	{
		return this.frontFace;
	}
	
	public Mesh backFace()
	{
		return this.backFace;
	}
	
	public Mesh leftFace()
	{
		return this.leftFace;
	}
	
	public Mesh rightFace()
	{
		return this.rightFace;
	}
	
	public Mesh topFace()
	{
		return this.topFace;
	}
	
	public Mesh bottomFace()
	{
		return this.bottomFace;
	}
	
	private void prepareVertices(boolean skybox)
	{
		this.frontFace = new Plane(width,height,segmentW,segmentH);
		if(skybox)
			this.frontFace.rotXYZ = new float[]{0,180,0};
		this.frontFace.XYZ = new float[]{0,0,depth/2};
		this.backFace = new Plane(width,height,segmentW,segmentH);
		this.backFace.XYZ = new float[]{0,0,-depth/2};
		
		this.leftFace = new Plane(depth,height,segmentD,segmentH);
		if(!skybox)
			this.leftFace.rotXYZ = new float[]{0,-90,0};
		else
			this.leftFace.rotXYZ = new float[]{0,90,0};
		this.leftFace.XYZ = new float[]{-width/2,0,0};
		this.rightFace = new Plane(depth,height,segmentD,segmentH);
		if(!skybox)
			this.rightFace.rotXYZ = new float[]{0,90,0};
		else
			this.rightFace.rotXYZ = new float[]{0,-90,0};
		this.rightFace.XYZ = new float[]{width/2,0,0};
		
		this.topFace = new Plane(width,depth,segmentW,segmentD);
		if(!skybox)
			this.topFace.rotXYZ = new float[]{-90,0,0};
		else
			this.topFace.rotXYZ = new float[]{-90,180,0};
		this.topFace.XYZ = new float[]{0,height/2,0};
		this.bottomFace = new Plane(width,depth,segmentW,segmentD);
		if(!skybox)
			this.bottomFace.rotXYZ = new float[]{90,0,0};
		else
			this.bottomFace.rotXYZ = new float[]{90,180,0};
		this.bottomFace.XYZ = new float[]{0,-height/2,0};
		
		this.objContainer.add(this.frontFace);
		this.objContainer.add(this.backFace);
		this.objContainer.add(this.leftFace);
		this.objContainer.add(this.rightFace);
		this.objContainer.add(this.topFace);
		this.objContainer.add(this.bottomFace);
	}
}
