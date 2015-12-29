package com.anengine.core;

import java.util.ArrayList;

import android.util.Log;

import com.anengine.core.renderer.DefaultRenderer;
import com.anengine.core.renderer.RenderData;
import com.anengine.core.renderer.RendererBase;
import com.anengine.debug.FreeDrawer;
import com.anengine.entity.Mesh;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.lights.AmbientLight;
import com.anengine.lights.LightBase;
import com.anengine.lights.PointLight;
import com.anengine.lights.SpotLight;
import com.anengine.textures.TextureBase;
import com.anengine.ui.UIManager;
import com.anengine.utils.tools.Ray;

public class Scene3D {
	private ArrayList<Object3D> objContainer = new ArrayList<Object3D>();
	private ArrayList<LightBase> lights = new ArrayList<LightBase>();
	private ArrayList<UIManager> uiManagers = new ArrayList<UIManager>();
	public Camera3D camera3d = null;
	public Config config = null;
	public RendererBase renderer = new DefaultRenderer();
	//前一帧时间，两帧间隔时间
	private long lastTime = System.currentTimeMillis();
	public float deltaTime=0;
	public float frameRate=0;
	
	public Scene3D(Config config)
	{
		this.config = config;
		try
		{
			this.config.startup();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		uiManagers.add(new UIManager());
		uiManagers.get(0).camera.XYZ[2] = 1;
		FreeDrawer.InitDrawer(this);//将放置于SceneManager中
		GLWrapper.glViewport((int)this.config.viewPort[0], (int)this.config.viewPort[1], (int)this.config.viewPort[2], (int)this.config.viewPort[3]);
	}
	
	public void changeViewPort(int x,int y,int width,int height)
	{
		GLWrapper.glViewport(x, y, width, height);
		this.config.viewRatio = (float)width/height;
		this.config.viewPort[0] = x;
		this.config.viewPort[1] = y;
		this.config.viewPort[2] = width;
		this.config.viewPort[3] = height;
	}
	
	public int faceCount()
	{
		int faces = 0;
		for(Object3D obj:objContainer)
		{
			if(obj instanceof Mesh)
				faces += ((Mesh)obj).faces();
			else if(obj instanceof ObjectContainer3D)
				faces += ((ObjectContainer3D)obj).faces();
		}
		return faces;
	}
	
	public boolean addObject3D(Object3D obj)
	{
		return this.objContainer.add(obj);
	}
	
	public Object3D getObject3D(int index)
	{
		return this.objContainer.get(index);
	}
	
	public boolean removeObject3D(Object3D obj)
	{
		return this.objContainer.remove(obj);
	}
	
	public Object3D removeObject3D(int index)
	{
		return this.objContainer.remove(index);
	}
	
	public boolean addLight(LightBase light)
	{
		return lights.add(light);
	}
	
	public LightBase getLight(int index)
	{
		return lights.get(index);
	}
	
	public boolean removeLight(LightBase light)
	{
		return lights.remove(light);
	}
	
	public LightBase removeLight(int index)
	{
		return this.lights.remove(index);
	}
	
	public boolean addUIManager(UIManager uiManager)
	{
		uiManager.scene = this;
		uiManager.camera.left = -config.viewRatio;
		uiManager.camera.right = config.viewRatio;
		return this.uiManagers.add(uiManager);
	}
	
	public UIManager getUIManager(int index)
	{
		return this.uiManagers.get(index);
	}
	
	public boolean removeUIManager(UIManager uiManager)
	{
		return this.uiManagers.remove(uiManager);
	}
	
	public UIManager removeUIManager(int index)
	{
		return this.uiManagers.remove(index);
	}
	
	//由屏幕坐标获取射线，ANE世界坐标系
	public Ray getRayFromScreen(float x,float y)
	{
		Ray ray = Ray.getRayFromScreen(x, y, config.viewPort, camera3d);
		ray.resize(1/Constant.UNIT_SIZE);
		return ray;
	}
	
	public float[] unproject(float x,float y,float z)
	{
		float[] pos = Ray.unproject(x, y, z, config.viewPort, camera3d);
		pos[0] /= Constant.UNIT_SIZE;
		pos[1] /= Constant.UNIT_SIZE;
		pos[2] /= Constant.UNIT_SIZE;
		return pos;
	}
	
	/*
	 * 对于环境光，lightAmbient为非负数
	 * 对于其他光源，lightAmbient为负数
	 * 聚光灯的附带参数为其光锥张角的一半
	 */
	private LightsData cacuLights()
	{		
		int lightCount = lights.size();
		int curI = 0;
		int curIP = 0;
		int curIC = 0;
		int curIA = 0;
		float[] lightPos = new float[lightCount*3];
		float[] lightColor = new float[lightCount*4];
		float[] lightAmbient = new float[lightCount];
		float[] lightStrength = new float[lightCount];
		float[] lightAttenuation = new float[lightCount*3];
		float[] lightAttenuationRange = new float[lightCount*2];
		float[] spotlightAngleCosDirection = new float[lightCount*4];
		for(LightBase curL:lights)
		{
			lightPos[curIP] = curL.XYZ[0]*Constant.UNIT_SIZE;
			lightPos[curIP+1] = curL.XYZ[1]*Constant.UNIT_SIZE;
			lightPos[curIP+2] = curL.XYZ[2]*Constant.UNIT_SIZE;
			
			lightColor[curIC] = curL.color[0];
			lightColor[curIC+1] = curL.color[1];
			lightColor[curIC+2] = curL.color[2];
			lightColor[curIC+3] = curL.color[3];
			
			//光源强度衰减系数 linear exp
			lightAttenuation[curIP] = 0;
			lightAttenuation[curIP+1] = 0;
			lightAttenuation[curIP+2] = 0;
			lightAttenuationRange[curIA] = 0;
			lightAttenuationRange[curIA+1] = 0;
			
			//only enabled to spotlight 
			spotlightAngleCosDirection[curIC] = 0;
			spotlightAngleCosDirection[curIC+1] = 0;
			spotlightAngleCosDirection[curIC+2] = 0;
			spotlightAngleCosDirection[curIC+3] = 0;
			
			/*if(curL.lightStrength>1)
				curL.lightStrength = 1;
			else if(curL.lightStrength<0)
				curL.lightStrength = 0;*/
			
			lightStrength[curI] = curL.lightStrength;
			
			//pointlight 
			if(curL.type()==1)
			{
				lightAmbient[curI] = -1;
				lightAttenuation[curIP] = ((PointLight)curL).Attenuation_Linear;
				lightAttenuation[curIP+1] = ((PointLight)curL).Attenuation_Exp;
				lightAttenuationRange[curIA] = ((PointLight)curL).AttenuationBegin;
				lightAttenuationRange[curIA+1] = ((PointLight)curL).AttenuationEnd;
			}//spot light
			if(curL.type()==4)
			{
				SpotLight sl = (SpotLight)curL;
				lightAmbient[curI] = -4;
				lightAttenuation[curIP] = sl.Attenuation_Linear;
				lightAttenuation[curIP+1] = sl.Attenuation_Exp;
				lightAttenuation[curIP+2] = sl.spotLightExp;
				lightAttenuationRange[curIA] = sl.AttenuationBegin;
				lightAttenuationRange[curIA+1] = sl.AttenuationEnd;
				spotlightAngleCosDirection[curIC] = (float)Math.cos(sl.angle);
				spotlightAngleCosDirection[curIC+1] = sl.target[0]-sl.XYZ[0];
				spotlightAngleCosDirection[curIC+2] = sl.target[1]-sl.XYZ[1];
				spotlightAngleCosDirection[curIC+3] = sl.target[2]-sl.XYZ[2];
			}//directionalLight
			else if(curL.type()==3)
			{
				lightAmbient[curI] = -3;
			}//ambientlight
			else if(curL.type()==2)
			{
				if(((AmbientLight)curL).ambient<0)
					((AmbientLight)curL).ambient = 0;
				lightAmbient[curI] = ((AmbientLight)curL).ambient;
			}
					
			curI++;
			curIA += 2;
			curIP += 3;
			curIC += 4;
		}
		
		return new LightsData(lightCount,lightPos,lightColor,
				lightAmbient,lightStrength,lightAttenuation,lightAttenuationRange,
				spotlightAngleCosDirection);
	}
	
	private void prepareBaseConfig()
	{
		GLWrapper.glEnable(Constant.GL_CULL_FACE);
		GLWrapper.glFrontFace(this.config.frontFace);
		GLWrapper.glClear(Constant.GL_DEPTH_BUFFER_BIT|Constant.GL_COLOR_BUFFER_BIT);
	}
	
	public void render() throws Exception
	{
		long curTime = System.currentTimeMillis();
		deltaTime = (curTime - lastTime)/1000.0f;
		lastTime = curTime;
		frameRate = 1/(deltaTime+0.00000001f);
		if(camera3d==null)
		{
			Log.e(Constant.ANENGINE_ERROR_TAG,"Camera can not be null");
			throw new Exception("Camera can not be null");
		}
		ArrayList<RenderData> renderDataList = new ArrayList<RenderData>();
		LightsData lightsData = this.cacuLights();
		this.prepareBaseConfig();
		//update
		for(Object3D curO3D:this.objContainer)
		{
			curO3D.update(null,this.camera3d,renderDataList);
		}
		//draw
		renderer.render(this,renderDataList,lightsData);
		renderDataList.clear();
		for(UIManager uiManager:uiManagers)
			uiManager.render();
		//Free Draw
		FreeDrawer.Drawer().render();
	}
	
	public void release()
	{
		TextureBase.releaseAll();
	}
	
	public class LightsData
	{
		public int lightCount;
		public float[] lightPos;//stride 3*n
		public float[] lightColor;//stride 4*n
		public float[] lightAmbient;//stride 1*n
		public float[] lightStrength;//stride 1*n
		public float[] lightAttenuation;//linear exp //stride 3*n
		public float[] lightAttenuationRange;//start end //stride 2*n
		public float[] spotlightAngleCosDirection;//stride 4*n
		
		public LightsData(int lightCount,float[] lightPos,float[] lightColor,
				float[] lightAmbient,float[] lightStrength,float[] lightAttenuation,float[] lightAttenuationRange,
				float[] spotlightAngleCosDirection)
		{
			this.lightCount = lightCount;
			this.lightPos = lightPos;
			this.lightColor = lightColor;
			this.lightAmbient = lightAmbient;
			this.lightStrength = lightStrength;
			this.lightAttenuation = lightAttenuation;
			this.lightAttenuationRange = lightAttenuationRange;
			this.spotlightAngleCosDirection = spotlightAngleCosDirection;
		}
	}
}
