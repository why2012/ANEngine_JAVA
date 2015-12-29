package com.anengine.collisions;

import com.anengine.core.ObjectContainer3D;
import com.anengine.entity.Mesh;

public class RagidBody {

	public AABB3 aabb = null;
	
	public RagidBody(Mesh mesh)
	{
		
	}
	
	public RagidBody(ObjectContainer3D objCon)
	{
		
	}
	
	public RagidBody(AABB3 box)
	{
		aabb = box;
	}
	
	public RagidBody(AABB3[] boxes)
	{
		aabb =new AABB3();
		for(AABB3 box:boxes)
			aabb.addAABB3(box);
	}
}
