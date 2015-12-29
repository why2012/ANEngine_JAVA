package com.anengine.samples.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.anengine.core.Camera3D;
import com.anengine.core.Config;
import com.anengine.core.Scene3D;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_math.Vector3D;

public class ViewBase extends GLSurfaceView {
	
	protected Context context = null;
	protected Config config = new Config();
	protected Scene3D scene = null;
	public Camera3D camera = new Camera3D();
	protected boolean initialized = false;
	protected float SCREEN_WIDTH,SCREEN_HEIGHT;
	protected WindowManager windowManager = null;
	protected LinearLayout data_display = null;
	protected Handler sceneHandle = null;
	private float mPreviousX,mPreviousY;
	private float TOUCH_SCALE_FACTOR = 1;
	private float xAngle=0,yAngle=0;
	
	public ViewBase(Context context,LinearLayout data_display,Handler sceneHandle)
	{
		super(context);
		
		this.context = context;
		this.data_display = data_display;
		this.sceneHandle = sceneHandle;
		this.setEGLContextClientVersion(2);
		
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		
		this.SCREEN_WIDTH = (float)dm.widthPixels;
		this.SCREEN_HEIGHT = (float)dm.heightPixels;
		TOUCH_SCALE_FACTOR = 180f/SCREEN_WIDTH;
	}
	
	public ViewBase(Context context)
	{
		super(context);
		
		this.context = context;
		this.setEGLContextClientVersion(2);
		
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		
		this.SCREEN_WIDTH = (float)dm.widthPixels;
		this.SCREEN_HEIGHT = (float)dm.heightPixels;
		TOUCH_SCALE_FACTOR = 180f/SCREEN_WIDTH;
	}
	
	protected void initScene()
	{
		ContextResources ctx_src = new ContextResources();
		ctx_src.ctx_src = context.getResources();
		config.ctx_src = ctx_src;
		scene = new Scene3D(config);
		scene.camera3d = camera;
		scene.addObject3D(camera);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		initialized = true;
	}
	
	protected boolean rotate(MotionEvent e)
	{
		if(!this.initialized)return true;
		float x = e.getX(),y = e.getY();
		switch(e.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				xAngle -= dy*TOUCH_SCALE_FACTOR;
				yAngle -= dx*TOUCH_SCALE_FACTOR;
				
				float distance = new Vector3D(camera.XYZ).length();
				camera.XYZ[0] = distance*(float)(Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle)));
				camera.XYZ[2] = distance*(float)(Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle)));
				camera.XYZ[1] = distance*(float)(Math.sin(Math.toRadians(xAngle)));
				break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
	
	protected class SceneRendererBase implements GLSurfaceView.Renderer
	{

		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			try
			{
				scene.render();
			}
			catch(Exception e)
			{
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
			ViewBase.this.initScene();
		}
	}
}
