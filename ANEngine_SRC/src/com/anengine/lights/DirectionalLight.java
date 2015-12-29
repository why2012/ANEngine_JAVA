package com.anengine.lights;

public class DirectionalLight extends LightBase {
	
	public DirectionalLight(float dx,float dy,float dz)
	{
		XYZ[0] = dx;
		XYZ[1] = dy;
		XYZ[2] = dz;
		this.lighttype = 3;
	}
}
