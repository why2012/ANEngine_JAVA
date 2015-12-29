package com.anengine.core.renderer;

import java.util.ArrayList;

import com.anengine.core.Scene3D;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;

public class RendererBase {
	
	public void render(Scene3D scene,ArrayList<RenderData> renderDataList,Scene3D.LightsData lightsData)
	{
		for(RenderData renderData:renderDataList)
		{
			renderData.lightsData = lightsData;
			renderData.o3d.prepare();
			for(SubMesh subMesh:renderData.o3d.subMeshes)
			{
				if(subMesh.material==null)
					subMesh.material = renderData.o3d.material;
				if(subMesh.material==null)
					continue;
				renderData.material = subMesh.material;
				renderData.subMesh = subMesh;
				//prepare
				renderData.material.beforeRender(renderData);
				renderData.material.prepareMaterial(renderData);
				//draw mesh
				drawMesh(renderData);
				renderData.material.afterRender(renderData);
			}
		}
	}
	
	protected void drawMesh(RenderData renderData)
	{
		renderData.subMesh.prepare(renderData);
		if(renderData.subMesh.drawMode==1)
			GLWrapper.glDrawElements(renderData.o3d.DrawMode, renderData.iCount, Constant.GL_UNSIGNED_SHORT, renderData.indicesBuffer.buffer);	
		else
			GLWrapper.glDrawArrays(renderData.o3d.DrawMode, 0, renderData.vCount);
	}
}
