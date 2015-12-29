package com.anengine.animation.data;

public class Weight
{
	public int joint_id;
	public float bias;
	public float[] pos;
	
	public Weight(int jid,float bias,float[] pos)
	{
		this.joint_id = jid;
		this.bias = bias;
		this.pos = pos;
	}
}
