package com.anengine.lights;

/*
 * light type : 1
 */
public class PointLight extends SimpleLight {
	
	public PointLight(float x,float y,float z)
	{
		this.XYZ[0] = x;
		this.XYZ[1] = y;
		this.XYZ[2] = z;
		
		this.lighttype = 1;
	}
}
