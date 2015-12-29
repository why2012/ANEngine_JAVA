package com.anengine.lights;

/*
 * light type : 2
 */
public class AmbientLight extends LightBase{
	public float ambient = .15f;
	
	public AmbientLight()
	{
		this.lighttype = 2;
	}
	
	public AmbientLight(float ambient)
	{
		this.ambient = ambient;
		this.lighttype = 2;
	}
}
