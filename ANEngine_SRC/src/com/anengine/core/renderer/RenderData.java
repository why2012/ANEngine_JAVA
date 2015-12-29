package com.anengine.core.renderer;

import com.anengine.core.Camera3D;
import com.anengine.core.Scene3D;
import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_data.ANE_Buffer;
import com.anengine.materials.IMaterial;

/*
 * 存放每帧渲染所需的基本数据
 */
public class RenderData {
	public int iCount;//当前绘制对象的索引数
	public int vCount;//顶点数
	public ANE_Buffer indicesBuffer;//索引缓冲
	public Mesh o3d;//当前绘制对象
	public SubMesh subMesh;//当前绘制的包含绘制数据的对象
	public float[] object3d_trasnformMatrix = new float[16];//当前绘制对象的变换矩阵
	public float[] object3d_finalMatrix;//最终矩阵
	public IMaterial material;//当前绘制对象的材质
	public Scene3D.LightsData lightsData;//灯光
	public Camera3D camera;//摄像机
	public float[] transformAxis;//container transforms 叠加
	
	public void clear()
	{
		this.transformAxis = null;
	}
}
