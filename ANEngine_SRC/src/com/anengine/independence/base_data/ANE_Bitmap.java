package com.anengine.independence.base_data;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.anengine.independence.base_file.ContextResources;

public class ANE_Bitmap {
	
	public Bitmap bitmap;
	
	public ANE_Bitmap(ContextResources ctx_src,int id)
	{
		InputStream ins = ctx_src.ctx_src.openRawResource(id);
		bitmap = BitmapFactory.decodeStream(ins);
		try {
			ins.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ANE_Bitmap(ContextResources ctx_src,String path)
	{
		try {
			InputStream ins = ctx_src.ctx_src.getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(ins);
			ins.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ANE_Bitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	
	public void release()
	{
		bitmap.recycle();
	}
}
