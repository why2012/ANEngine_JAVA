package com.anengine.samples.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.anengine.animation.Animator;
import com.anengine.core.Camera3D;
import com.anengine.core.Config;
import com.anengine.core.Scene3D;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_math.Vector3D;
import com.anengine.materials.ColorMaterial;
import com.anengine.primitives.Box;
import com.anengine.utils.tools.Ray;

public class MD5Test extends GLSurfaceView {

	private Context context;
	private float mPreviousX,mPreviousY;
	private SceneRenderer scener = new SceneRenderer();
	public boolean camFlag = true;
	 
	public MD5Test(Context context) {
		super(context);
		
		this.context = context;
		this.setEGLContextClientVersion(2);
		this.setRenderer(scener);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		if(!scener.initialized)
			return true;
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
				//MatrixOperation.rotateP(scener.camera.XYZ, new float[]{0,0,0}, dy/150,1, 0, 0);
				//MatrixOperation.rotateP(scener.camera.XYZ, new float[]{0,0,0}, dx/150,0, 1, 0);
				//MatrixState.printMatrix(scener.camera.XYZ,3);
				break;
			case MotionEvent.ACTION_UP:
				camFlag = true;
				break;
			case MotionEvent.ACTION_DOWN:
				Ray touchray = scener.scene.getRayFromScreen(x, y);
				Vector3D target = touchray.origin;
				//scener.box2.XYZ = target.asFloat();
				float[] pos = scener.box.XYZ;
				float width2 = scener.box.width()/2;
				Vector3D p = touchray.triangleIntersection(
						new float[]{pos[0]-width2,pos[1]+width2,pos[2]+width2}, 
						new float[]{pos[0]-width2,pos[1]-width2,pos[2]+width2}, 
						new float[]{pos[0]+width2,pos[1]-width2,pos[2]+width2}, 0, 0);
				Vector3D p2 = touchray.triangleIntersection(
						new float[]{pos[0]+width2,pos[1]-width2,pos[2]+width2},
						new float[]{pos[0]+width2,pos[1]+width2,pos[2]+width2}, 
						new float[]{pos[0]-width2,pos[1]+width2,pos[2]+width2}, 0, 0);
				System.out.println(p+" | "+p2);
				if(p!=null||p2!=null)
					System.out.println("clicked");
				camFlag = false;
				break;
		}
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		private Config config = new Config();
		public Scene3D scene = null;
		private Animator animator = null;
		public Camera3D camera = new Camera3D();
		public boolean initialized = false;
		public Box box = null;
		public Box box2 = null;
		
		public SceneRenderer()
		{
		}
		
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			try {
				//animator.update(scene.deltaTime);
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
			camera.lookAt(0, 0, -3);
			camera.setPosition(0f, 0, 18.0f);
			
			box = new Box(0,0,0,1,1,1);
			box.material = new ColorMaterial(0,1,0,1);
			scene.addObject3D(box);
			
			box2 = new Box(0,0,0,1,1,1);
			box2.material = new ColorMaterial(1,0,0,1);
			scene.addObject3D(box2);
			
			/*TextSprite2D board = new TextSprite2D(new String[]{"ANEngine"});
			board.XYZ[0] -= board.width/3;
			board.XYZ[1] -= board.height;
			scene.uiManagers.get(0).addSprite2D(board);*/
			
			/*MD5Parser parser = new MD5Parser(ctx_src);
			ObjectContainer3D con = parser.parseMesh("bob_lamp/bob_lamp_update_export.md5mesh");
			con.frontFaceOrder(Constant.GL_CW);
			Animation anim1 = parser.parseAnim("bob_lamp/bob_lamp_update_export.md5anim");
			animator = parser.getAnimator();
			animator.add("anim1", anim1);
			animator.play("anim1");
			scene.addObject3D(con);*/
			
			initialized = true;
		}
	}
}
