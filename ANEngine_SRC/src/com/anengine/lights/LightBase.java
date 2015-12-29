package com.anengine.lights;

import com.anengine.core.Object3D;

public class LightBase extends Object3D{
	public float[] color = new float[]{1f,1f,1f,1f};
	public float lightStrength = 1.0f;
	
	protected int lighttype = 0;
	
	public int type()
	{
		return lighttype;
	}
}
