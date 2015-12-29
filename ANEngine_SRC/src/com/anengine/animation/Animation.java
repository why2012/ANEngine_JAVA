package com.anengine.animation;

import java.util.ArrayList;

import com.anengine.collisions.AABB3;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.independence.base_math.Quaternion;

public class Animation {

	//每个关键帧的骨骼
	public ArrayList<Skeleton> frameSkeleton = new ArrayList<Skeleton>();
	//插值后的骨骼
	public Skeleton animatedSkeleton = null;
	//boundBoxes
	public AABB3[] bounds;
	//帧率
	public float frameRate;
	//帧数
	public int numFrame;
	//动画持续时间
	public float animDuration = 0;
	
	private float animTime = 0;
	
	public boolean useJointPos = true;//MD5动画需要叠加关节位置，DAE不需要
	
	public Animation clone()
	{
		Animation animTmp = new Animation();
		animTmp.frameSkeleton = frameSkeleton;
		animTmp.animatedSkeleton = animatedSkeleton;
		animTmp.bounds = bounds;
		animTmp.frameRate = frameRate;
		animTmp.numFrame = numFrame;
		animTmp.animDuration = animDuration;
		animTmp.animTime = animTime;
		return animTmp;
	}
	
	public void update(float deltaTime)
	{
		if(numFrame<1)return;
		animTime += deltaTime;
		while(animTime>animDuration)animTime -= animDuration;
		while(animTime<0)animTime += animDuration;
		float fnum = animTime*frameRate;
		int iframe0 = (int)Math.floor(fnum);
		int iframe1 = (int)Math.ceil(fnum);
		iframe0 = iframe0 % numFrame;
		iframe1 = iframe1 % numFrame;
		float interpolate = (animTime%animDuration)/animDuration;
		interpolateSkeleton(frameSkeleton.get(iframe0),frameSkeleton.get(iframe1),interpolate);
	}
	
	public void interpolateSkeleton(Skeleton ske1,Skeleton ske2,float interpolate)
	{
		//System.out.println("——————"+ske1.joints.size());
		for(int i=0;i<ske1.joints.size();i++)
		{
			Joint finalJ = animatedSkeleton.joints.get(i);
			Joint joint0 = ske1.joints.get(i);
			Joint joint1 = ske2.joints.get(i);
			Quaternion.slerp(finalJ.pos, joint0.pos, joint1.pos, interpolate);
			Quaternion.slerp(finalJ.orient, joint0.orient, joint1.orient, interpolate);
			//MatrixState.printMatrix(finalJ.pos, 3);
			//System.out.println(finalJ.orient);
		}
	}
	
	public String toString()
	{
		return "frameCount:"+this.numFrame+" , duration:"+this.animDuration+" , fps:"+this.frameRate;
	}
}
