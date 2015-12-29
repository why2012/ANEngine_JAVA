package com.anengine.utils.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

import com.anengine.animation.Animation;
import com.anengine.animation.Animator;
import com.anengine.animation.Joint;
import com.anengine.animation.Skeleton;
import com.anengine.animation.data.BaseFrame;
import com.anengine.animation.data.FrameData;
import com.anengine.animation.data.JointInfo;
import com.anengine.animation.data.Weight;
import com.anengine.collisions.AABB3;
import com.anengine.core.ObjectContainer3D;
import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_math.Quaternion;
import com.anengine.materials.TextureMaterial;
import com.anengine.textures.BitmapTexture;

public class MD5Parser extends ParserBase {

	private Animator animator = null;
	private ArrayList<JointInfo> jointsInfo = new ArrayList<JointInfo>();
	private ArrayList<BaseFrame> baseframe = new ArrayList<BaseFrame>();
	private ArrayList<FrameData> frames = new ArrayList<FrameData>();
	private int numAnimatedComponents = 0;
	private int numJoints = 0;
	
	public String textureExtName = ".jpg";
	
	public MD5Parser(ContextResources ctx_src)
	{
		super(ctx_src);
		animator = new Animator();
	}
	
	public Animator getAnimator()
	{
		return animator;
	}
	
	public void reset()
	{
		animator = new Animator();
		jointsInfo.clear();
		baseframe.clear();
		frames.clear();
		numAnimatedComponents = 0;
		numJoints = 0;
	}
	
	public ObjectContainer3D parseMesh(String meshpath)
	{
		ObjectContainer3D con = new ObjectContainer3D();
		String parentPath;
		
		try
		{
			parentPath = new File(meshpath).getParent();
			if(parentPath!=null)
				parentPath += "\\";
			else
				parentPath = "";
			parentPath = parentPath.replaceAll("\\\\","/");
		
			InputStream meshIns = ctx_src.ctx_src.getAssets().open(meshpath);
			InputStreamReader meshReader = new InputStreamReader(meshIns);
			BufferedReader meshBReader = new BufferedReader(meshReader);
			
			String tmps;
			System.out.println("MD5 Mesh parse start");
			//parse md5 mesh
			while((tmps=meshBReader.readLine())!=null)
			{
				String[] tmpSArr = tmps.split("[ ]+");
				if(tmpSArr[0].equals("MD5Version"))
				{
					//check version 
					if(!tmpSArr[1].equals("10"))
					{
						Log.e(Constant.ANENGINE_ERROR_TAG,"MD5 version must be 10,cur:"+tmpSArr[1]);
						return con;
					}
				}
				else if(tmpSArr[0].equals("numJoints"))
				{
					numJoints = Integer.parseInt(tmpSArr[1]);
				}
				//parse joints tag
				else if(tmpSArr[0].equals("joints"))
				{
					System.out.println("MD5 Joint parse start");
					ArrayList<Joint> joints = new ArrayList<Joint>();
					int i = 0;
					try{
						for(i=0;i<numJoints;i++)
						{
							tmps=meshBReader.readLine();
							tmpSArr = tmps.trim().split("[ ]+|[	]+");
							Joint joint = new Joint(tmpSArr[0],Integer.parseInt(tmpSArr[1]),
								new float[]{Float.parseFloat(tmpSArr[3]),Float.parseFloat(tmpSArr[4]),Float.parseFloat(tmpSArr[5])},
								new Quaternion(Float.parseFloat(tmpSArr[8]),Float.parseFloat(tmpSArr[9]),Float.parseFloat(tmpSArr[10])));
							joints.add(joint);
						}
					}
					catch(NumberFormatException e)
					{
						e.printStackTrace();
						System.out.println("Error : Joint("+i+")\n"+tmps);
						for(i=0;i<tmpSArr.length;i++)
							System.out.println(i+" : "+tmpSArr[i]+" ");
						System.out.flush();
						return null;
					}
					tmps=meshBReader.readLine();//for }
					animator.skeleton.joints = joints;
					System.out.println("MD5 Joint parse end");
				}
				//parse mesh tag
				else if(tmpSArr[0].equals("mesh"))
				{
					Mesh mesh = new Mesh();
					SubMesh subMesh = new SubMesh();
					mesh.subMeshes.add(subMesh);
					con.addObject3D(mesh);
					subMesh.parent = mesh;
					mesh.animator = animator;
					int[] verticesW = null;
					float[] ST = null;
					short[] indices = null;
					ArrayList<Weight[]> weights = new ArrayList<Weight[]>();
					Weight[] weights_f = null;
					while(!(tmps=meshBReader.readLine()).equals("}"))
					{
						tmpSArr = tmps.split("[ ]+");
						
						if(tmpSArr[0].trim().equals("shader"))
						{
							tmpSArr[1] = tmpSArr[1].trim().substring(1, tmpSArr[1].length()-1);
							subMesh.material = this.getTextureMaterial(parentPath+tmpSArr[1]);
						}
						else if(tmpSArr[0].trim().equals("numverts"))
						{
							int count = Integer.parseInt(tmpSArr[1]);
							subMesh.setVertices(new float[count*3]);
							verticesW = new int[count*2];
							ST = new float[count*2];
							int i = 0;
							while(count--!=0&&(tmps=meshBReader.readLine())!=null)
							{
								tmpSArr = tmps.split("[ ]+");

								ST[i*2] = Float.parseFloat(tmpSArr[3]);
								ST[i*2+1] = Float.parseFloat(tmpSArr[4]);
								
								verticesW[i*2] = Integer.parseInt(tmpSArr[6]);
								verticesW[i*2+1] = Integer.parseInt(tmpSArr[7]);
								i++;
							}
						}
						else if(tmpSArr[0].trim().equals("numtris"))
						{
							int count = Integer.parseInt(tmpSArr[1]);
							indices = new short[count*3];
							int i = 0;
							while(count--!=0&&(tmps=meshBReader.readLine())!=null)
							{
								tmpSArr = tmps.split("[ ]+");
								indices[i*3] = Short.parseShort(tmpSArr[2]);
								indices[i*3+1] = Short.parseShort(tmpSArr[3]);
								indices[i*3+2] = Short.parseShort(tmpSArr[4]);
								i++;
							}
						}
						else if(tmpSArr[0].trim().equals("numweights"))
						{
							int count = Integer.parseInt(tmpSArr[1]);
							weights_f = new Weight[count];
							int i = 0;
							while(count--!=0&&(tmps=meshBReader.readLine())!=null)
							{
								tmpSArr = tmps.split("[ ]+");
								Weight w = new Weight(
										Integer.parseInt(tmpSArr[2]),
										Float.parseFloat(tmpSArr[3]),
										new float[]{
											Float.parseFloat(tmpSArr[5]),
											Float.parseFloat(tmpSArr[6]),
											Float.parseFloat(tmpSArr[7])});
								weights_f[i] = w;
								i++;
							}
						}
					}
					
					subMesh.setTexCoord(ST);
					subMesh.setIndices(indices);
					for(int i=0;i<verticesW.length;i+=2)
					{
						int start = verticesW[i];
						int count = verticesW[i+1];
						Weight[] vweights = new Weight[count];
						for(int n=0;n<count;n++)
						{
							vweights[n] = weights_f[start+n];
						}
						weights.add(vweights);
					}
					subMesh.weights = weights;
				}
			}
			buildDefaultSkeleton(animator.skeleton);
			System.out.println("MD5 Mesh parse end");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return con;
	}
	
	public Animation parseAnim(String animpath)
	{	
		Animation animation = new Animation();
		try
		{
			InputStream animIns = ctx_src.ctx_src.getAssets().open(animpath);
			InputStreamReader animReader = new InputStreamReader(animIns);
			BufferedReader animBReader = new BufferedReader(animReader);
			//String animName = new File(animpath).getName().split("\\.")[0];
			String tmps;
			int jointsNum = 0;
			//parse anim
			System.out.println("MD5 Anim parse start");
			while((tmps=animBReader.readLine())!=null)
			{
				String[] tmpSArr = tmps.split("[ ]+");
				//check version 
				if(tmpSArr[0].equals("MD5Version"))
				{
					if(!tmpSArr[1].equals("10"))
					{
						animation = null;
						Log.e(Constant.ANENGINE_ERROR_TAG,"MD5 version must be 10,cur:"+tmpSArr[1]);
						return null;
					}
				}
				else if(tmpSArr[0].equals("frameRate"))
				{
					animation.frameRate = Integer.parseInt(tmpSArr[1]);
				}
				else if(tmpSArr[0].equals("numFrames"))
				{
					animation.numFrame = Integer.parseInt(tmpSArr[1]);
				}
				else if(tmpSArr[0].equals("numAnimatedComponents"))
				{
					numAnimatedComponents = Integer.parseInt(tmpSArr[1]);
				}
				else if(tmpSArr[0].equals("numJoints"))
				{
					jointsNum = Integer.parseInt(tmpSArr[1]);
				}
				else if(tmpSArr[0].equals("hierarchy"))
				{
					for(int i=0;i<jointsNum;i++)
					{
						tmps=animBReader.readLine();
						tmpSArr = tmps.trim().split("[ ]+|[	]+");
						
						JointInfo jInfo = new JointInfo();
						jInfo.name = tmpSArr[0];
						jInfo.parent_id = Integer.parseInt(tmpSArr[1]);
						jInfo.flags = Integer.parseInt(tmpSArr[2]);
						jInfo.startIndex = Integer.parseInt(tmpSArr[3]);
						jointsInfo.add(jInfo);
					}
					tmps=animBReader.readLine();//for }
				}
				else if(tmpSArr[0].equals("bounds"))
				{
					int frameNum = animation.numFrame;
					animation.bounds = new AABB3[frameNum];
					for(int i=0;i<frameNum;i++)
					{
						tmps=animBReader.readLine();
						tmpSArr = tmps.split("[ ]+");
						AABB3 ab = new AABB3(Float.parseFloat(tmpSArr[1]),
								Float.parseFloat(tmpSArr[2]),
								Float.parseFloat(tmpSArr[3]),
								Float.parseFloat(tmpSArr[6]),
								Float.parseFloat(tmpSArr[7]),
								Float.parseFloat(tmpSArr[8]));
						animation.bounds[i] = ab;
					}
					tmps=animBReader.readLine();//for }
				}
				else if(tmpSArr[0].equals("baseframe"))
				{
					for(int i=0;i<jointsNum;i++)
					{
						tmps=animBReader.readLine();
						tmpSArr = tmps.split("[ ]+");
						BaseFrame baseFrame = new BaseFrame();
						baseFrame.pos = new float[]{
								Float.parseFloat(tmpSArr[1]),
								Float.parseFloat(tmpSArr[2]),
								Float.parseFloat(tmpSArr[3])};
						baseFrame.orient = new Quaternion(
								Float.parseFloat(tmpSArr[6]),
								Float.parseFloat(tmpSArr[7]),
								Float.parseFloat(tmpSArr[8]));
						baseframe.add(baseFrame);
					}
					tmps=animBReader.readLine();//for }
				}
				else if(tmpSArr[0].equals("frame"))
				{
					FrameData frameData = new FrameData();
					frameData.frame_id = Integer.parseInt(tmpSArr[1]);
					frameData.data = new float[this.numAnimatedComponents];
					int n = 0;
					
					while(!(tmps=animBReader.readLine()).equals("}"))
					{
						tmpSArr = tmps.split("[ ]+");
						
						for(int i=0;i<tmpSArr.length;i++)
						{
							frameData.data[n] = Float.parseFloat(tmpSArr[i]);
							n++;
						}
					}
					frames.add(frameData);
					//build skeleton
					Skeleton ske = buildFrameSkeleton(frameData);
					animation.frameSkeleton.add(ske);
				}
			}
			System.out.println("MD5 Anim parse end");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation.animDuration = animation.numFrame/animation.frameRate;
		if(animation.frameSkeleton.get(0)!=null)
			animation.animatedSkeleton = animation.frameSkeleton.get(0).clone();
		return animation;
	}
	
	private void buildDefaultSkeleton(Skeleton ske)
	{
		for(int i=0;i<ske.joints.size();i++)
		{
			Joint joint = ske.joints.get(i);
			if(joint.parent_index>0)
		    {
		    	Joint parentJoint = ske.joints.get(joint.parent_index);
		    	float[] vec3 = new float[3];
		    	parentJoint.orient.Multiply(vec3, joint.pos);
		    	joint.pos[0] = parentJoint.pos[0]+vec3[0];
		    	joint.pos[1] = parentJoint.pos[1]+vec3[1];
		    	joint.pos[2] = parentJoint.pos[2]+vec3[2];
		    	Quaternion result = new Quaternion();
		    	parentJoint.orient.Multiply(result, joint.orient);
		    	joint.orient = result.normalize();
		    }
		}
	}
	
	private Skeleton buildFrameSkeleton(FrameData frameData)
	{
		Skeleton ske = new Skeleton();
		for(int i=0;i<jointsInfo.size();i++)
		{
			int j = 0;
			BaseFrame bframe = baseframe.get(i);
			JointInfo jinfo = jointsInfo.get(i);
			Joint skeJoint = new Joint(jinfo.name,jinfo.parent_id,
							new float[]{bframe.pos[0],bframe.pos[1],bframe.pos[2]},
							bframe.orient.clone());
			ske.joints.add(skeJoint);
			 if ( (jinfo.flags & 1) !=0) // Pos.x
		     {
				 skeJoint.pos[0] = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     if ( (jinfo.flags & 2) !=0) // Pos.y
		     {
		    	 skeJoint.pos[1] = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     if ( (jinfo.flags & 4) !=0) // Pos.x
		     {
		    	 skeJoint.pos[2]  = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     if ( (jinfo.flags & 8) !=0) // Orient.x
		     {
		    	 skeJoint.orient.x = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     if ( (jinfo.flags & 16) !=0) // Orient.y
		     {
		    	 skeJoint.orient.y = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     if ( (jinfo.flags & 32) !=0) // Orient.z
		     {
		    	 skeJoint.orient.z = frameData.data[ jinfo.startIndex + j++ ];
		     }
		     
		     float w = 1 - skeJoint.orient.x*skeJoint.orient.x-skeJoint.orient.y*skeJoint.orient.y-skeJoint.orient.z*skeJoint.orient.z;
		     skeJoint.orient.w = w<0?0:-(float)Math.sqrt(w);
		     if(skeJoint.parent_index>0)
		     {
		    	 Joint parentJoint = ske.joints.get(skeJoint.parent_index);
		    	 float[] vec3 = new float[3];
		    	 parentJoint.orient.Multiply(vec3, skeJoint.pos);
		    	 skeJoint.pos[0] = parentJoint.pos[0]+vec3[0];
		    	 skeJoint.pos[1] = parentJoint.pos[1]+vec3[1];
		    	 skeJoint.pos[2] = parentJoint.pos[2]+vec3[2];
		    	 Quaternion result = new Quaternion();
		    	 parentJoint.orient.Multiply(result, skeJoint.orient);
		    	 skeJoint.orient = result.normalize();
		     }
		}
		ske.numJoints = ske.joints.size();
		return ske;
	}
	
	private TextureMaterial getTextureMaterial(String path)
	{
		return new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,path+textureExtName)));
	}
}
