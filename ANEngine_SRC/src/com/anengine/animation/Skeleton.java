package com.anengine.animation;

import java.util.ArrayList;

public class Skeleton {
	
	public ArrayList<Joint> joints;
	public int numJoints = 0;
	
	public Skeleton()
	{
		this.joints = new ArrayList<Joint>();
	}
	
	public Skeleton clone()
	{
		Skeleton c = new Skeleton();
		c.numJoints = joints.size();
		for(int i=0;i<c.numJoints;i++)
			c.joints.add(joints.get(i).clone());
		return c;
	}
	
	public Joint getJointByJointName(String name)
	{
		for(Joint joint:joints)
		{
			if(joint.name.equals(name))
			{
				return joint;
			}
		}
		return null;
	}
	
	public int getIndexByJointName(String name)
	{
		int i = -1;
		int c = 0;
		for(Joint joint:joints)
		{
			if(joint.name.equals(name))
			{
				i = c;
				break;
			}
			c++;
		}
		return i;
	}
}
