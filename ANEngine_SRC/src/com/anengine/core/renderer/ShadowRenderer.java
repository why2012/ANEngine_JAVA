package com.anengine.core.renderer;

import java.util.ArrayList;

import com.anengine.core.Scene3D;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.lights.shadow.DefaultShadowMap;
import com.anengine.materials.SimpleMaterial;

//生成动态和静态阴影
public class ShadowRenderer extends RendererBase {
	
	public DefaultShadowMap shadowmap;
	
	public ShadowRenderer(DefaultShadowMap shadowMap)
	{
		this.shadowmap = shadowMap;
	}
	
	public void render(Scene3D scene,ArrayList<RenderData> renderDataList,Scene3D.LightsData lightsData)
	{
		createShadow(scene,renderDataList);
		super.render(scene,renderDataList,lightsData);
		shadowmap.clear();
	}
	
	private void createShadow(Scene3D scene,ArrayList<RenderData> renderDataList)
	{
		shadowmap.initBuffers();
		shadowmap.prepare(scene);
		float[] finalMatrix = new float[16];
		float[] cameraM = new float[16];
		float[] projM = new float[16];
		MatrixState.setCamera(cameraM,shadowmap.light.XYZ[0]*Constant.UNIT_SIZE, shadowmap.light.XYZ[1]*Constant.UNIT_SIZE,
				shadowmap.light.XYZ[2]*Constant.UNIT_SIZE, 0, 0, 0, 0, 1f, 0);
		//方向光源用正交投影
		if(shadowmap.light.type()==3)
		{
			MatrixState.setProjectOrtho(projM,-1f, 1f, -1f, 1f, 1.5f, 1000);
		}
		else
		{
			MatrixState.setProjectFrustum(projM,-1f, 1f, -1f, 1f, 1.5f, 1000);
		}
		MatrixOperation.MultiplyMM(finalMatrix, projM, cameraM);
		for(RenderData renderData:renderDataList)
		{
			renderData.o3d.prepare();
			for(SubMesh subMesh:renderData.o3d.subMeshes)
			{
				if(subMesh.material==null)
					renderData.material = renderData.o3d.material;
				else
					renderData.material = subMesh.material;
				renderData.subMesh = subMesh;
				if(!(renderData.material instanceof SimpleMaterial))
					continue;
				else if(!((SimpleMaterial)renderData.material).castShadow&&!((SimpleMaterial)renderData.material).receiveShadow)
				{
					continue;
				}
				((SimpleMaterial)renderData.material).shadow_tex_id = shadowmap.shadowTexId;
				((SimpleMaterial)renderData.material).light_shadow = new float[]{shadowmap.light.XYZ[0]*Constant.UNIT_SIZE, shadowmap.light.XYZ[1]*Constant.UNIT_SIZE,
						shadowmap.light.XYZ[2]*Constant.UNIT_SIZE};
				((SimpleMaterial)renderData.material).lightFinalMatrix = finalMatrix;
				shadowmap.beforeDraw(finalMatrix, renderData.object3d_trasnformMatrix, renderData.subMesh.verticesBuffer);
				this.drawMesh(renderData);
			}
		}
		shadowmap.toDefault(scene);
	}
}
