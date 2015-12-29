package com.anengine.samples.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.anengine.core.Camera3D;
import com.anengine.core.Config;
import com.anengine.core.Object3D;
import com.anengine.core.ObjectContainer3D;
import com.anengine.core.Scene3D;
import com.anengine.core.renderer.RenderData;
import com.anengine.core.renderer.RenderOperation;
import com.anengine.core.renderer.ShadowRenderer;
import com.anengine.entity.Mesh;
import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.lights.AmbientLight;
import com.anengine.lights.DirectionalLight;
import com.anengine.lights.LightBase;
import com.anengine.lights.PointLight;
import com.anengine.lights.SpotLight;
import com.anengine.lights.shadow.DefaultShadowMap;
import com.anengine.materials.ColorMaterial;
import com.anengine.materials.MaterialBase;
import com.anengine.materials.SegmentMaterial;
import com.anengine.materials.SimpleMaterial;
import com.anengine.materials.TextureMaterial;
import com.anengine.primitives.Box;
import com.anengine.primitives.Plane;
import com.anengine.primitives.PlaneBox;
import com.anengine.samples_dae.R;
import com.anengine.textures.BitmapTexture;
import com.anengine.utils.parsers.OBJParser;

public class View3D extends GLSurfaceView {

	private Context context;
	private float mPreviousX,mPreviousY;
	private SceneRenderer scener = new SceneRenderer();
	public boolean camFlag = true;
	
	public View3D(Context context) {
		super(context);
		
		this.context = context;
		this.setEGLContextClientVersion(2);
		this.setRenderer(scener);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		float y = e.getY(),x = e.getX();
		switch(e.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				if(Math.abs(dx)>50)
					dx = 0;
				if(Math.abs(dy)>50)
					dy = 0;
				mPreviousX = x;
				mPreviousY = y;
				MatrixOperation.rotateP(scener.camera.XYZ, new float[]{0,0,0}, dy/150,1, 0, 0);
				MatrixOperation.rotateP(scener.camera.XYZ, new float[]{0,0,0}, dx/150,0, 1, 0);
				//MatrixState.printMatrix(scener.camera.XYZ,3);
				break;
			case MotionEvent.ACTION_UP:
				camFlag = true;
				break;
			case MotionEvent.ACTION_DOWN:
				camFlag = false;
				break;
		}
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		private Config config = new Config();
		private Scene3D scene = null;
		public Camera3D camera = new Camera3D();
		private Object3D o3d = null;
		private Object3DThread o3dthread = null;
		private Camera3DThread camThread = null;
		
		public SceneRenderer()
		{
		}
		
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			try {
				scene.render();
				} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onSurfaceChanged(GL10 arg0, int width, int height) {
			// TODO Auto-generated method stub
			scene.changeViewPort(0, 0, width, height);
			float ratio = (float)width/height;
			camera.setViewBox(-ratio, ratio, -1, 1, 1, 100);
		}

		@Override
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub

			ContextResources ctx_src = new ContextResources();
			ctx_src.ctx_src = context.getResources();
			config.ctx_src = ctx_src;
			scene = new Scene3D(config);
			scene.camera3d = camera;
			scene.addObject3D(camera);
			camera.lookAt(0, -3, 0);
			camera.setPosition(0f, 3, 12.0f);
			ColorMaterial material_blue = new ColorMaterial(0,0,1f,1f);
			ColorMaterial material_red = new ColorMaterial(1f,0,0,1f);
			SegmentMaterial segMaterial = new SegmentMaterial(1f);
			TextureMaterial bitmapM = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.bitmap1)));
			TextureMaterial bitmapM2 = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.ghxp)));
			TextureMaterial bitmapIron = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.iron)));
			
			Box box = new Box(3,4,0,2,2,2);
			box.rotXYZ[1] = 45;
			box.material = bitmapIron;
			scene.addObject3D(box);
			new Object3DThread(box).start();
			
			PlaneBox skybox = new PlaneBox(0,0,0,40,40,40,true);
			
			TextureMaterial front = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_south)));
			TextureMaterial back = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_north)));
			TextureMaterial right = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_east)));
			TextureMaterial left = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_west)));
			TextureMaterial up = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_up)));
			TextureMaterial down = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.glacier_down)));
			skybox.frontFace().material = front;
			skybox.backFace().material = back;
			skybox.rightFace().material = right;
			skybox.leftFace().material = left;
			skybox.topFace().material = up;
			skybox.bottomFace().material = down;
			skybox.enableCullFace(false);
			skybox.lightoff(true);
			scene.addObject3D(skybox);
			
			Plane floor = new Plane(20,20);
			floor.rotXYZ[0] = -90;
			floor.XYZ[1] = -3.8f;
			floor.material = bitmapIron;
			bitmapIron.normalMap = new BitmapTexture(new ANE_Bitmap(ctx_src,R.drawable.normalmap2));
			((MaterialBase)floor.material).renderOperation = new RenderOpeFloor();
			scene.addObject3D(floor);
			
			ObjectContainer3D dragon = new OBJParser(ctx_src).parse("dragon/dragon.obj");
			dragon.enableCullFace(false);
			dragon.rotXYZ[2] = 90;
			dragon.rotXYZ[0] = -90;
			dragon.XYZ[0] = -5;
			dragon.XYZ[1] = 3;
			dragon.scaleXYZ = new float[]{.1f,.1f,.1f};
			scene.addObject3D(dragon);
			
			ObjectContainer3D obj_model2 = new OBJParser(ctx_src).singleParse("ch.obj");
			obj_model2.enableCullFace(false);
			obj_model2.XYZ[1] = -3;
			obj_model2.attachMaterial(bitmapM2);
			obj_model2.scaleXYZ = new float[]{.1f,.1f,.1f};
			//obj_model2.lightoff(true);
			new ObjectContainer3DThread(obj_model2,true).start();
			
			ObjectContainer3D obj_model2_mirror = obj_model2.clone();
			obj_model2_mirror.XYZ[1] = -5;
			obj_model2_mirror.rotXYZ[0] = 180;
			obj_model2_mirror.scaleXYZ = new float[]{.1f,.1f,.1f};
			((Mesh)obj_model2_mirror.getObject3D(0)).material = bitmapM2.clone();
			((MaterialBase)((Mesh)obj_model2_mirror.getObject3D(0)).material).renderOperation = new RenderOpeBody();
			new ObjectContainer3DThread(obj_model2_mirror,-1).start();
			scene.addObject3D(obj_model2_mirror);
			scene.addObject3D(obj_model2);
			
			PointLight pointLight = new PointLight(0,0,0);
			pointLight.Attenuation_Linear = .1f;
			pointLight.AttenuationBegin = 0;
			DirectionalLight dLight = new DirectionalLight(-0.4f,10f,-6.7f);
			AmbientLight ambientLight = new AmbientLight();
			ambientLight.lightStrength = 2;
			dLight.lightStrength = .4f;
			SpotLight sLight = new SpotLight(0,10,-7);
			scene.addLight(dLight);
			/*scene.addLight(pointLight);*/
			scene.addLight(ambientLight);
			scene.addLight(sLight);
			
			DefaultShadowMap smap = new DefaultShadowMap(sLight);
			ShadowRenderer srenderer = new ShadowRenderer(smap);
			scene.renderer = srenderer;
			((SimpleMaterial)floor.material).receiveShadow = true;
		}
	}
	
	class RenderOpeFloor implements RenderOperation
	{

		@Override
		public void beforeRender(RenderData renderData) {
			// TODO Auto-generated method stub
			com.anengine.independence.base_gl.GLWrapper.glClear(Constant.GL_STENCIL_BUFFER_BIT);
			com.anengine.independence.base_gl.GLWrapper.glEnable(Constant.GL_STENCIL_TEST);
			com.anengine.independence.base_gl.GLWrapper.glStencilFunc(Constant.GL_ALWAYS, 1, 1);
			com.anengine.independence.base_gl.GLWrapper.glStencilOp(Constant.GL_KEEP, Constant.GL_KEEP, Constant.GL_REPLACE);
		}

		@Override
		public void afterRender(RenderData renderData) {
			// TODO Auto-generated method stub
			//com.anengine.independence.base_gl.GLWrapper.glDisable(Constant.GL_STENCIL_TEST);
		}
		
	}
	
	class RenderOpeBody implements RenderOperation
	{

		@Override
		public void beforeRender(RenderData renderData) {
			// TODO Auto-generated method stub
			//com.anengine.independence.base_gl.GLWrapper.glEnable(Constant.GL_STENCIL_TEST);
			com.anengine.independence.base_gl.GLWrapper.glStencilFunc(Constant.GL_EQUAL, 1, 1);
			com.anengine.independence.base_gl.GLWrapper.glStencilOp(Constant.GL_KEEP, Constant.GL_KEEP, Constant.GL_KEEP);
		}

		@Override
		public void afterRender(RenderData renderData) {
			// TODO Auto-generated method stub
			com.anengine.independence.base_gl.GLWrapper.glDisable(Constant.GL_STENCIL_TEST);
		}
		
	}
	
	class Object3DThread extends Thread
	{
		private Object3D obj = null;
		private int neg = 1;
		
		public Object3DThread(Object3D object)
		{
			obj = object;
		}
		
		public Object3DThread(Object3D object,int neg)
		{
			obj = object;
			this.neg = neg;
		}
		 
		public void run()
		{
			while(true)
			{
				obj.rotXYZ[0] += neg*1.5f;
				obj.rotXYZ[1] += neg*1.5f;
				try
				{
					Thread.sleep(20);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	class ObjectContainer3DThread extends Thread
	{
		private Object3D obj = null;
		private int neg = 1;
		private boolean backforward = false;
		
		public ObjectContainer3DThread(Object3D object,int neg)
		{
			obj = object;
			this.neg = neg;
		}
		
		public ObjectContainer3DThread(Object3D object,boolean backforward)
		{
			obj = object;
			this.backforward = backforward;
		}
		
		public ObjectContainer3DThread(Object3D object)
		{
			obj = object;
		}
		
		public void run()
		{
			while(true)
			{
				//scener.camera.rotXYZ[2] += 0.3f;
				obj.rotXYZ[1] -= neg*1f;
				//obj.rotXYZ[0] -= 1f;
				if(backforward)
				{
					obj.XYZ[1]+= neg*0.01;
					if(obj.XYZ[1]>=4.5)
						neg = -1;
					else if(obj.XYZ[1]<-3)
						neg = 1;
				}
				try
				{
					Thread.sleep(20);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	class Camera3DThread extends Thread
	{
		private Camera3D cam = null;
		
		public Camera3DThread(Camera3D camera)
		{
			cam = camera;
		}
		
		public void run()
		{
			
			while(View3D.this.camFlag)
			{
				MatrixOperation.rotateP(cam.XYZ, new float[]{0,0,0}, (float)(Math.PI/200),0, 0, 1);
				try
				{
					Thread.sleep(20);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	class LightThread extends Thread
	{
		private LightBase light = null;
		private Object3D lightball = null;
		
		public LightThread(LightBase light)
		{
			this.light = light;
		}
		
		public LightThread(LightBase light,Object3D lightball)
		{
			this.light = light;
			this.lightball = lightball;
		}
		
		public void run()
		{
			while(true)
			{
				MatrixOperation.rotateP(light.XYZ, new float[]{0,0,0}, .01f, 1, 0, 0);
				if(lightball!=null)
				{
					lightball.XYZ[0] = light.XYZ[0];
					lightball.XYZ[1] = light.XYZ[1];
					lightball.XYZ[2] = light.XYZ[2];
				}
				try
				{
					Thread.sleep(20);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
