package com.anengine.samples.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anengine.core.ObjectContainer3D;
import com.anengine.samples.ModelViewActivity.SceneHandle;
import com.anengine.samples_dae.R;
import com.anengine.utils.tools.SceneLoader;
import com.anengine.utils.tools.SceneLoader.LoaderListener;

public class ModelView extends ViewBase {
	
	private SceneRenderer sceneRenderer;
	
	public ModelView(Context ctx,LinearLayout data_display,Handler sceneHandle)
	{
		super(ctx,data_display,sceneHandle);
		
		sceneRenderer = new SceneRenderer(data_display,sceneHandle);
		this.setRenderer(sceneRenderer);
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		this.rotate(e);
		return true;
	}
	
	private class SceneRenderer extends ViewBase.SceneRendererBase
	{
		private LinearLayout data_display = null;
		private TextView FPS = null;
		private Handler sceneHandler = null;
		
		public SceneRenderer(LinearLayout data_display,Handler handler)
		{
			this.data_display = data_display;
			this.sceneHandler = handler;
		}
		
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			super.onDrawFrame(arg0);
			Message msg = Message.obtain();
			msg.obj = FPS;
			msg.what = SceneHandle.SET_TEXT;
			msg.arg1 = (int)scene.frameRate;
			sceneHandler.sendMessage(msg);
		}

		@Override
		public void onSurfaceChanged(GL10 arg0, int width, int height) {
			// TODO Auto-generated method stub
			super.onSurfaceChanged(arg0, width, height);
		}

		@Override
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub
			super.onSurfaceCreated(arg0, arg1);
			
			camera.lookAt(0, 0, 0);
			camera.setPosition(0f, 0, 20.0f);
			
			
			FPS = (TextView)data_display.findViewById(R.id.fps);
			Message msg1 = Message.obtain();
			msg1.obj = data_display.findViewById(R.id.camera_x);
			msg1.what = SceneHandle.SET_TEXT;
			msg1.arg1 = (int)camera.XYZ[0];
			
			Message msg2 = Message.obtain();
			msg2.obj = data_display.findViewById(R.id.camera_y);
			msg2.what = SceneHandle.SET_TEXT;
			msg2.arg1 = (int)camera.XYZ[1];
			
			Message msg3 = Message.obtain();
			msg3.obj = data_display.findViewById(R.id.camera_z);
			msg3.what = SceneHandle.SET_TEXT;
			msg3.arg1 = (int)camera.XYZ[2];
			
			sceneHandler.sendMessage(msg1);
			sceneHandler.sendMessage(msg2);
			sceneHandler.sendMessage(msg3);
			
			SceneLoader sceneLoader = new SceneLoader(config.ctx_src);
			sceneLoader.loaderListener = new LoaderListener(){
				public void onstart() {}

				@Override
				public void onprogress(float progress) {
					// TODO Auto-generated method stub
					Message progressMsg = Message.obtain();
					progressMsg.arg1 = (int)progress;
					progressMsg.what = SceneHandle.SET_PROGRESS;
					sceneHandler.sendMessage(progressMsg);
				}
				
				public void onend() {}			
			};
			//type,path,scale,texOffset,texScale,xyz,rotxyz
			String[][] objects = new String[][]{
					{SceneLoader.OBJ,"actor03/actor03.obj|reverset","1","0","1","0|-17|0"},
					//{SceneLoader.OBJ,"actor04/actor04.obj|reverset","0.04","0","1","0|-10|0","-10|-90|0"},
					//{SceneLoader.OBJ,"actor05/actor05.obj|reverset","10","0","1","0|-4|0","-90|0|0"}
					//{SceneLoader.OBJ,"actor06/actor06.obj|reverset","10","0","1","0|-18|0","90|180|0"}
					//{SceneLoader.OBJ,"sphere/sphere.obj","10","0","1","0||0|0","0|0|0"}
			};
			sceneLoader.load(objects); 
			for(ObjectContainer3D objcon:sceneLoader.result)
			{
				scene.addObject3D(objcon);
			}
			
			Message msg4 = Message.obtain();
			msg4.obj = data_display.findViewById(R.id.faces);
			msg4.what = SceneHandle.SET_TEXT;
			msg4.arg1 = scene.faceCount();
			sceneHandler.sendMessage(msg4);
		}
		
	}
}
