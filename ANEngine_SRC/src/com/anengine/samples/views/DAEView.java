package com.anengine.samples.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.MotionEvent;

import com.anengine.animation.Animator;
import com.anengine.core.ObjectContainer3D;
import com.anengine.debug.FreeDrawer;
import com.anengine.independence.base_math.Vector3D;
import com.anengine.utils.parsers.DAEParser;

public class DAEView extends ViewBase {
	
	private SceneRenderer sceneRenderer;
	
	public DAEView(Context ctx)
	{
		super(ctx);
		
		sceneRenderer = new SceneRenderer();
		this.setRenderer(sceneRenderer);
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		this.rotate(e);
		return true;
	}
	
	private class SceneRenderer extends ViewBase.SceneRendererBase
	{	
		private ObjectContainer3D model = null;
		private Animator anim = null;
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			anim.update(scene.deltaTime);
			super.onDrawFrame(arg0);
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
			camera.setPosition(0f, 0, 10.0f);
			
			DAEParser parser = new DAEParser(config.ctx_src);
			parser.reverseT = true;
			model = parser.parse("boneTes02/boneTes02.DAE");
			//model.pivotXYZ[1] = 5;
			//model.pivotXYZ[2] = -5;
			//model.scaleXYZ = new float[]{0.01f,0.01f,0.01f};
			anim = parser.getAnimator();
			anim.play(Animator.DEFAULT_ANIMATION);
			//anim.stop();
			
			scene.addObject3D(model);
		}
	}
}
