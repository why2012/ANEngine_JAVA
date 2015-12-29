package com.anengine.materials;

import com.anengine.core.renderer.RenderData;

public interface IMaterial {
	public void prepareMaterial(RenderData renderData);
	public void beforeRender(RenderData renderData);
	public void afterRender(RenderData renderData);
}
