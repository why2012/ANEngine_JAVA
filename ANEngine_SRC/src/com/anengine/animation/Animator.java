package com.anengine.animation;

import java.util.HashMap;

import com.anengine.debug.FreeDrawer;
import com.anengine.independence.base_math.Vector3D;

public class Animator {
	//默认动画
	public static String DEFAULT_ANIMATION = "Default";
	//原始骨骼
	public Skeleton skeleton = new Skeleton();
	//动画
	public HashMap<String,Animation> animations = new HashMap<String,Animation>();
	//当前使用的骨骼
	public Skeleton animatedSkeleton = skeleton;
	//当前动画
	private Animation curAnim = null;
	
	private String animName = null;
	private boolean play = true;
	
	public Animator clone()
	{
		Animator anim = new Animator();
		anim.skeleton = skeleton;
		anim.animations = animations;
		anim.animatedSkeleton = animatedSkeleton;
		return anim;
	}

	public void add(String name,Animation anim)
	{
		animations.put(name, anim);
	}
	
	public Animation curAnimation()
	{
		return this.curAnim;
	}
	
	public void play(String animName)
	{
		this.animName = animName;
		curAnim = animations.get(animName);
		play = true;
	}
	
	public void stop()
	{
		play = false;
	}
	
	public void play()
	{
		play = true;
	}
	
	//设置骨骼为默认状态
	public void setDefault()
	{
		this.animatedSkeleton = this.skeleton;
		this.animName = null;
	}
	
	public void update(float deltaTime)
	{
		if(!play||animName==null||curAnim==null)
			return;
		curAnim.update(deltaTime);
		this.animatedSkeleton = curAnim.animatedSkeleton;
		this.drawBones();
	}
	
	private void drawBones()
	{
		if(this.animatedSkeleton.joints.size()==0)return;
		Joint joint = this.animatedSkeleton.joints.get(0);
		FreeDrawer.Drawer().clear();
		FreeDrawer.Drawer().push();
		FreeDrawer.Drawer().moveTo(new Vector3D(joint.pos));
		for(int i=1;i<this.animatedSkeleton.joints.size();i++)
		{
			FreeDrawer.Drawer().lineTo(new Vector3D(this.animatedSkeleton.joints.get(i).pos));
		}
	}
}
