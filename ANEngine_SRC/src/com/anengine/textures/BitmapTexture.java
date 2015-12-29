package com.anengine.textures;

import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;

public class BitmapTexture extends TextureBase {
	
	private ANE_Bitmap bitmap;
	
	public BitmapTexture(ANE_Bitmap bitmap)
	{
		super();
		this.bitmap = bitmap;
		this.loadBitmap();
	}
	
	public void setBitmap(ANE_Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.loadBitmap();
	}
	
	private void loadBitmap()
	{
		GLWrapper.glBindTexture(Constant.GL_TEXTURE_2D, this.textureId);
		GLWrapper.gluTexImage2D(Constant.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.bitmap.recycle();
	}
}
