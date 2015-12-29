package com.anengine.core.renderer;

public interface RenderOperation
{
	void beforeRender(RenderData renderData);
	void afterRender(RenderData renderData);
}