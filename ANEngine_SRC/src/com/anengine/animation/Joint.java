package com.anengine.animation;

import com.anengine.independence.base_math.Quaternion;

public class Joint
{
	public String name;
	public int parent_index;
	public float[] pos;//位置
	public Quaternion orient;//方向
	public float[] scaleXYZ;//缩放
	public float[] invBindMatrix = null;//反向绑定矩阵
	
	public Joint(String name,int pid,float[] pos,Quaternion qua)
	{
		this.name = name;
		this.parent_index = pid;
		this.pos = pos;
		this.orient = qua;
		this.scaleXYZ = new float[]{1.0f,1.0f,1.0f};
	}
	
	public Joint(String name,int pid,float[] pos,float[] scaleXYZ)
	{
		this.name = name;
		this.parent_index = pid;
		this.pos = pos;
		this.scaleXYZ = scaleXYZ;
	}
	
	public Joint clone()
	{
		Joint c = new Joint(name,parent_index,pos.clone(),orient.clone());
		c.scaleXYZ = this.scaleXYZ.clone();
		if(this.invBindMatrix!=null)
			c.invBindMatrix = this.invBindMatrix.clone();
		return c;
	}
}
