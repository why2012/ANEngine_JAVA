package com.anengine.Controller;

import com.anengine.core.Camera3D;

public interface IController {
	
	//transformAxis 父容器的变换矩阵
	public void update(Camera3D camera,float[] transformAxis);
}
