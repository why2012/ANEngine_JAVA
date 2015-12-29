package com.anengine.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.anengine.core.Camera3D;
import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.textures.BitmapTexture;

/*
 * Too Inefficient for dynamic text
 */
public class TextSprite2D extends Sprite2D {
	
	protected String[] text = null;
	protected Paint paint = new Paint();
	protected Canvas canvas = new Canvas();
	public int[] RGBA = new int[]{0,255,0,255};
	public int[] bg_RGBA = new int[]{255,255,255,255};
	public float textSize = 40;
	
	public TextSprite2D()
	{}
		
	public TextSprite2D(String[] text)
	{
		this.text = text;
		this.genBitmap();
	}
	
	public void setText(String[] text)
	{
		this.text = text;
		this.genBitmap();
	}
	
	public void update(Camera3D camera)
	{
		super.update(camera);
	}
	
	public void refresh()
	{
		this.genBitmap();
	}
	
	private void genBitmap()
	{
		paint.setTypeface(null);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Bitmap tmpB = Bitmap.createBitmap((int)width*100,(int)height*100,Bitmap.Config.ARGB_8888);
		canvas.setBitmap(tmpB);
		paint.setARGB(bg_RGBA[3], bg_RGBA[0], bg_RGBA[1], bg_RGBA[2]);
		canvas.drawRect(0, 0, width*100, height*100, paint);
		paint.setARGB(RGBA[3], RGBA[0], RGBA[1], RGBA[2]);
		paint.setTextSize(textSize);
		int i = 0;
		for(String str:text)
		{
			canvas.drawText(str, 0, textSize*(i+1), paint);
			i++;
		}		
		if(this.bitmap!=null)
			this.bitmap.release();
		this.bitmap = new BitmapTexture(new ANE_Bitmap(tmpB));
	}
}
