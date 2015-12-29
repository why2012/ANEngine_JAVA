package com.anengine.lights;

public class SpotLight extends SimpleLight{
	
	public float angle = (float)Math.PI/6;//the angle of the light's cone
	public float[] target = new float[]{0,0,0};//light direction
	public float spotLightExp = 15;//光锥张衰减量
	
	public SpotLight(float x,float y,float z)
	{
		this.XYZ[0] = x;
		this.XYZ[1] = y;
		this.XYZ[2] = z;
		
		this.lighttype = 4;
	}
	
	public void lookAt(float x,float y,float z)
	{
		target[0] = x;
		target[1] = y;
		target[2] = z;
	}
}	
