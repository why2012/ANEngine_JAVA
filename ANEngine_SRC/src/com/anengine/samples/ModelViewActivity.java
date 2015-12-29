package com.anengine.samples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anengine.samples.views.ModelView;
import com.anengine.samples.views.ViewBase;
import com.anengine.samples_dae.R;

public class ModelViewActivity extends Activity {
	
	public GLSurfaceView view = null;
	public LayoutInflater inflater = null;
	public LinearLayout scene_layout = null;
	public LinearLayout camera_control_layout = null;
	public LinearLayout data_display_layout = null;	
	public SceneHandle sceneHandle = new SceneHandle(Looper.myLooper());
	public ProgressDialog progressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		inflater = (LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		scene_layout = (LinearLayout)inflater.inflate(R.layout.scene, null, false);
		camera_control_layout = (LinearLayout)inflater.inflate(R.layout.scene_controller, null, false);
		data_display_layout = (LinearLayout)inflater.inflate(R.layout.data_dislpay, null, false);
		
		view = new ModelView(this,scene_layout,sceneHandle);
		
		setContentView(view);
		addContentView(scene_layout, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		scene_layout.addView(camera_control_layout, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		scene_layout.addView(data_display_layout, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	
		this.addCameraTextChange();
		this.initProgressDialog();
	}
	
	public void addCameraTextChange()
	{
		EditText camera_x,camera_y,camera_z;
		camera_x = (EditText)scene_layout.findViewById(R.id.camera_x);
		camera_y = (EditText)scene_layout.findViewById(R.id.camera_y);
		camera_z = (EditText)scene_layout.findViewById(R.id.camera_z);
		
		camera_x.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(arg0==null||arg0.toString().equals(""))
					((ViewBase)view).camera.XYZ[0] = 0;
				else
					((ViewBase)view).camera.XYZ[0] = Integer.parseInt(arg0.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		
		camera_y.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(arg0==null||arg0.toString().equals(""))
					((ViewBase)view).camera.XYZ[1] = 0;
				else
					((ViewBase)view).camera.XYZ[1] = Integer.parseInt(arg0.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		camera_z.addTextChangedListener(new TextWatcher() {
	
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(arg0==null||arg0.toString().equals(""))
					((ViewBase)view).camera.XYZ[2] = 0;
				else
					((ViewBase)view).camera.XYZ[2] = Integer.parseInt(arg0.toString());
			}
	
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
	
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
	}
	
	public void initProgressDialog()
	{
		this.progressDialog = new ProgressDialog(this);
		this.progressDialog.setCancelable(false);
		this.progressDialog.setCanceledOnTouchOutside(false);
		this.progressDialog.setMessage("模型加载中...");
		this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.progressDialog.setMax(100);
		this.progressDialog.show();
		
	}
	
	public class SceneHandle extends Handler
	{
		public static final int SET_TEXT = 1;
		public static final int SET_PROGRESS = 2;
		
		public SceneHandle(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			  case SET_TEXT:
				  if(msg.obj==null)return;
				  TextView fps = (TextView)msg.obj;
				  fps.setText(String.valueOf(msg.arg1));
				  break;
			  case SET_PROGRESS:
				  if(ModelViewActivity.this.progressDialog==null)return;
				  ModelViewActivity.this.progressDialog.setProgress(msg.arg1);
				  if(msg.arg1>=100) ModelViewActivity.this.progressDialog.cancel();
				  break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*public void onPause()
	{
		super.onPause();
		view.onPause();
	}
	
	public void onResume()
	{
		super.onResume();
		view.onResume();
	}*/
}
