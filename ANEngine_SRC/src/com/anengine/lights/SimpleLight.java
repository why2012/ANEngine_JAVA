package com.anengine.lights;

import com.anengine.independence.base_gl.Constant;

public class SimpleLight extends LightBase {
	/*
	 * 衰减计算
	 * FStrength = Strength/(1+Linear*Distance+Exp*Distance^2) 
	 */
	public float Attenuation_Linear = .1f;
	public float Attenuation_Exp = 0;
	//Distance from which falloff starts
	public float AttenuationBegin = 5*Constant.UNIT_SIZE;
	//Distance from at which falloff is complete
	public float AttenuationEnd = 50*Constant.UNIT_SIZE;
}
