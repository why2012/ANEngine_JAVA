package com.anengine.utils.tools;

import java.util.HashMap;

import android.util.Log;

import com.anengine.animation.Animation;
import com.anengine.animation.Animator;
import com.anengine.core.ObjectContainer3D;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_gl.Constant;
import com.anengine.utils.parsers.MD5Parser;
import com.anengine.utils.parsers.OBJParser;

public class SceneLoader {
	public LoaderListener loaderListener = null;
	/*
	 * [[type,path,scale,texOffset,texScale]]
	 * [[type,path,scale,texOffset,texScale,x|y|z,rotX|rotY|rotZ]]
	 * [[type,path|animPath1|animPath2...,scale,texOffset,texScale]]
	 */
	public String[][] objects = null;
	public ObjectContainer3D[] result = null;
	public HashMap<ObjectContainer3D,Animator> animators = null;
	public ContextResources ctx_src = null;
	
	public static String OBJ = "OBJ";
	public static String MD5 = "MD5";
	public static String DAE = "DAE";
	
	public SceneLoader(String[][] objects,ContextResources ctx_src)
	{
		this.objects = objects;
		this.ctx_src = ctx_src;
	}
	
	public SceneLoader(ContextResources ctx_src)
	{
		this.ctx_src = ctx_src;
	}
	
	public void load(String[][] objects)
	{
		this.objects = objects;
		this.load();
	}
	
	public void load()
	{
		int totalCount = objects.length;
		int cur = 0,c = 0;
		result = new ObjectContainer3D[totalCount];
		animators = new HashMap<ObjectContainer3D,Animator>();
		if(loaderListener!=null)
		{
			loaderListener.onstart();
		}
		for(String[] item:objects)
		{
			ObjectContainer3D model = null;
			if(item[0].equals(OBJ))
			{
				String items[] = item[1].split("[|]+");
				OBJParser parser = new OBJParser(ctx_src);
				if(items.length>=2)
				{
					if(items[1].equals("reverset"))
					{
						parser.reverseT = true;
					}
				}
				model = parser.parse(items[0]);
			}
			else if(item[0].equals(MD5))
			{
				String[] pathes = item[1].split("[|]+");
				MD5Parser parser = new MD5Parser(ctx_src);
				Animator animator = new Animator();
				model = parser.parseMesh(pathes[0]);
				if(pathes.length>1)
				{
					for(int i=1;i<pathes.length;i++)
					{
						Animation anim = parser.parseAnim(pathes[i]);
						animator.add(pathes[i].split(".")[0], anim);
					}
					animators.put(model, animator);
				}
			}
			else if(item[0].equals(DAE))
			{
				
			}
			else
			{
				Log.e(Constant.ANENGINE_ERROR_TAG, "SceneLoader--unsupport type:"+item[0]);
			}
			if(model!=null)
			{
				if(item.length>=3)
				{
					float scale = Float.parseFloat(item[2]);
					model.scaleXYZ = new float[]{scale,scale,scale};
				}
				
				float offset=0,scale=1;
				float[] XYZ = new float[3];
				float[] rotXYZ = new float[3];
				if(item.length>=4)
				{
					offset = Float.parseFloat(item[3]);
				}
				if(item.length>=5)
				{
					scale = Float.parseFloat(item[4]);
				}
				if(item.length>=6)
				{
					String[] xyz = item[5].split("[|]+");
					XYZ[0] = Float.parseFloat(xyz[0]);
					XYZ[1] = Float.parseFloat(xyz[1]);
					XYZ[2] = Float.parseFloat(xyz[2]);
				}
				if(item.length>=7)
				{
					String[] rotxyz = item[6].split("[|]+");
					rotXYZ[0] = Float.parseFloat(rotxyz[0]);
					rotXYZ[1] = Float.parseFloat(rotxyz[1]);
					rotXYZ[2] = Float.parseFloat(rotxyz[2]);
				}
				model.setMaterialOffsetScale(offset, offset, scale, scale);
				model.XYZ = XYZ;
				model.rotXYZ = rotXYZ;
				result[c++] = model;
			}
			cur++;
			if(loaderListener!=null)
			{
				loaderListener.onprogress((float)cur/totalCount*100);
			}
		}
		if(loaderListener!=null)
		{
			loaderListener.onend();
		}
	}
	
	public interface LoaderListener
	{
		public void onstart();
		//progress 0-100
		public void onprogress(float progress);
		public void onend();
	}
}
