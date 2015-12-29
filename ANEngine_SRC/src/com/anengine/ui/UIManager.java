package com.anengine.ui;

import java.util.ArrayList;

import android.util.Log;

import com.anengine.core.Camera3D;
import com.anengine.core.Scene3D;
import com.anengine.independence.base_gl.Constant;

public class UIManager {
	
	private ArrayList<Sprite2D> sprites = new ArrayList<Sprite2D>();
	public Camera3D camera = new Camera3D();
	public Scene3D scene = null;
	
	public UIManager()
	{
		
	}
	
	public Sprite2D getSprite2D(int index)
	{
		return sprites.get(index);
	}
	
	public void addSprite2D(Sprite2D sprite)
	{
		sprite.uiManager = this;
		sprites.add(sprite);
	}
	
	public Sprite2D removeSprite2D(Sprite2D sprite)
	{
		sprites.remove(sprite);
		return sprite;
	}
	
	public Sprite2D removeSprite2D(int index)
	{
		Sprite2D sprite = sprites.get(index);
		sprites.remove(index);
		return sprite;
	}
	
	public void render()
	{
		if(camera==null)
		{
			Log.e(Constant.ANENGINE_ERROR_TAG,"UIManage:camera cat not be null");
		}
		camera.update(null, null, null);
		for(Sprite2D sprite:sprites)
		{
			sprite.update(camera);
			sprite.draw();
		}
	}
}
