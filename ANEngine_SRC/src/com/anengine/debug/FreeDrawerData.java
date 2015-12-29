package com.anengine.debug;

import java.util.ArrayList;

import com.anengine.debug.MetaElement.IMetaElement;

public class FreeDrawerData {

	public ArrayList<IMetaElement> elements= new ArrayList<IMetaElement>();
	public boolean enableDepthTest = true;
	
	public void render(float[] finalMatrix)
	{
		for(IMetaElement e:elements)
		{
			e.prepare(finalMatrix);
			e.draw();
		}
	}
}
