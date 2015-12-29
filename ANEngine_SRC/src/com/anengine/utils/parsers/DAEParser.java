package com.anengine.utils.parsers;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anengine.animation.Animation;
import com.anengine.animation.Animator;
import com.anengine.animation.Joint;
import com.anengine.animation.Skeleton;
import com.anengine.animation.data.Weight;
import com.anengine.core.ObjectContainer3D;
import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_math.MatrixOperation;
import com.anengine.independence.base_math.MatrixState;
import com.anengine.independence.base_math.Quaternion;
import com.anengine.materials.MaterialBase;
import com.anengine.materials.TextureMaterial;
import com.anengine.textures.BitmapTexture;

public class DAEParser extends ParserBase {

	private Animator animator = null;
	public boolean reverseT = false;
	
	public DAEParser(ContextResources ctx_src)
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
	}
	
	public ObjectContainer3D parse(String path)
	{
		ObjectContainer3D con = new ObjectContainer3D();
		String parentPath;
		
		try
		{
			parentPath = new File(path).getParent();
			if(parentPath!=null)
				parentPath += "\\";
			else
				parentPath = "";
			parentPath = parentPath.replaceAll("\\\\","/");
			
			InputStream ins = ctx_src.ctx_src.getAssets().open(path);
			Document dae = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ins);
			
			HashMap<String,MaterialBase> materials = parseMaterial(parentPath,dae);
			HashMap<String,SubMesh> submeshMap = new HashMap<String,SubMesh>();
			HashMap<SubMesh,int[]> meshIndex = new HashMap<SubMesh,int[]>();
			HashMap<SubMesh,float[]> meshVertice = new HashMap<SubMesh,float[]>();
			NodeList meshes = dae.getElementsByTagName("mesh");
			NodeList geometries = dae.getElementsByTagName("geometry");
			//parse meshs
			for(int i=0;i<meshes.getLength();i++)
			{
				Node meshNode = meshes.item(i);
				Element meshE = (Element)meshNode;
				Element verticesNode = null;//顶点数据节点
				Element stNode = null;//纹理坐标数据节点
				Element normalNode = null;//法线数据节点
				Node indexNode = null;//索引节点
				Element triangleNode = null;
				int vertexOffset = 0;
				int stOffset = 0;
				int normalOffset = 0;
				
				Mesh mesh = new Mesh();
				mesh.animator = this.animator;
				SubMesh subMesh = new SubMesh();
				subMesh.parent = mesh;
				subMesh.drawMode = 2;
				mesh.subMeshes.add(subMesh);
				con.addObject3D(mesh);
				
				Element geometry = (Element)geometries.item(i);
				String name = geometry.getAttribute("id").split("[-]")[0]+"Controller";
				submeshMap.put(name, subMesh);
				
				float[] vertices = null;
				float[] normals = null;
				float[] texCoords = null;
				
				//确定顶点，法线，纹理坐标数据的sourceName
				String srcVertex = ((Element)(meshE.getElementsByTagName("vertices").item(0).getChildNodes().item(1))).getAttribute("source");
				srcVertex = srcVertex.substring(1);
				String srcNormal="",srcTexCoord="";
				triangleNode = (Element)meshE.getElementsByTagName("triangles").item(0);
				String materialName = triangleNode.getAttribute("material");
				//获取索引节点
				indexNode = triangleNode.getElementsByTagName("p").item(0);
				vertices = new float[Integer.parseInt(triangleNode.getAttribute("count"))*9];
				normals = new float[Integer.parseInt(triangleNode.getAttribute("count"))*9];
				texCoords = new float[Integer.parseInt(triangleNode.getAttribute("count"))*6];
				NodeList triInputs = triangleNode.getElementsByTagName("input");
				for(int triInputIndex=0;triInputIndex<triInputs.getLength();triInputIndex++)
				{
					Element input = (Element)triInputs.item(triInputIndex);
					if(input.getAttribute("semantic").equals("NORMAL"))
					{
						srcNormal = input.getAttribute("source").trim().substring(1);
						normalOffset = Integer.parseInt(input.getAttribute("offset"));
					}
					else if(input.getAttribute("semantic").equals("TEXCOORD"))
					{
						srcTexCoord = input.getAttribute("source").trim().substring(1);
						stOffset = Integer.parseInt(input.getAttribute("offset"));
					}
					else if(input.getAttribute("semantic").equals("VERTEX"))
					{
						vertexOffset = Integer.parseInt(input.getAttribute("offset"));
					}
				}
				//获取顶点，法线，纹理坐标数据源
				NodeList srcNodes = meshE.getElementsByTagName("source");
				for(int srcIndex=0;srcIndex<srcNodes.getLength();srcIndex++)
				{
					Element src = (Element)srcNodes.item(srcIndex);
					if(src.getAttribute("id").equals(srcVertex))
					{
						verticesNode = (Element)src.getElementsByTagName("float_array").item(0);
					}
					else if(src.getAttribute("id").equals(srcNormal))
					{
						normalNode = (Element)src.getElementsByTagName("float_array").item(0);
					}
					else if(src.getAttribute("id").equals(srcTexCoord))
					{
						stNode = (Element)src.getElementsByTagName("float_array").item(0);
					}
				}
				//获取顶点，法线，纹理坐标,索引数据
				String verticesContent = verticesNode.getTextContent();
				String normalContent = normalNode.getTextContent();
				String texContent = stNode.getTextContent();
				String indexContent = indexNode.getTextContent();
				float[] tmp_vertices = getFloatArray(verticesContent);
				float[] tmp_normals = getFloatArray(normalContent);
				float[] tmp_texCoords = getFloatArray(texContent);
				int[] index = getIntArray(indexContent);
				int[] vindex = new int[index.length/3];
				meshIndex.put(subMesh, vindex);
				meshVertice.put(subMesh, tmp_vertices);
				int c = 0;
				for(int tri_i=0;tri_i<index.length;tri_i+=3)
				{
					vindex[c] = index[tri_i+vertexOffset];
					
					vertices[c*3] = tmp_vertices[index[tri_i+vertexOffset]*3];
					vertices[c*3+1] = tmp_vertices[index[tri_i+vertexOffset]*3+1];
					vertices[c*3+2] = tmp_vertices[index[tri_i+vertexOffset]*3+2];
					texCoords[c*2] = tmp_texCoords[index[tri_i+stOffset]*2];
					texCoords[c*2+1] = tmp_texCoords[index[tri_i+stOffset]*2+1];
					normals[c*3] = tmp_normals[index[tri_i+normalOffset]*3];
					normals[c*3+1] = tmp_normals[index[tri_i+normalOffset]*3+1];
					normals[c*3+2] = tmp_normals[index[tri_i+normalOffset]*3+2];
					c++;
				}
				subMesh.setVertices(vertices);
				subMesh.setTexCoord(texCoords);
				subMesh.setNormal(normals);
				subMesh.material = materials.get(materialName);
			}
			
			//parse animationData
			NodeList tmpN = dae.getElementsByTagName("library_animations");
			if(tmpN.getLength()>0)
			{
				//build skeleton
				ArrayList<Element> esa = new ArrayList<Element>();
				Element visual_scene = (Element)dae.getElementsByTagName("visual_scene").item(0);
				NodeList nodes = visual_scene.getChildNodes();
				for(int i=0;i<nodes.getLength();i++)
				{
					Element tmp = null;
					try
					{
						tmp = (Element)nodes.item(i);
					}
					catch(ClassCastException e)
					{
						continue;
					}
					
					if(tmp.getAttribute("type").equals("JOINT"))
					{
						esa.add(tmp);
					}
				}
				Element[] es = new Element[esa.size()];
				esa.toArray(es);
				parseJoints(es,this.animator.skeleton,-1);
				this.buildDefaultSkeleton(this.animator.skeleton);
				
				//parse weights
				NodeList cs = dae.getElementsByTagName("controller");
				Element[] controllers = new Element[cs.getLength()];
				for(int i=0;i<cs.getLength();i++)
				{
					controllers[i] = (Element)cs.item(i);
				}
				
				for(Element controller:controllers)
				{
					String id = controller.getAttribute("id");
					SubMesh subMesh = submeshMap.get(id);
					if(subMesh==null)continue;
					float[] submeshVertices = meshVertice.get(subMesh);
					String m = controller.getElementsByTagName("bind_shape_matrix").item(0).getTextContent();
					float[] bsm = getFloatArray(m);
					float[] pos = MatrixOperation.getTranslation(bsm).asFloat();
					Element joints = (Element)controller.getElementsByTagName("joints").item(0);
					NodeList inputs = joints.getElementsByTagName("input");
					String jointsJE = findByAttr(inputs,"semantic","JOINT").getAttribute("source").trim().substring(1);
					//反向绑定矩阵
					String jointINVM = findByAttr(inputs,"semantic","INV_BIND_MATRIX").getAttribute("source").trim().substring(1);
					Element jointE = findByAttr(controller.getElementsByTagName("source"),"id",jointsJE);
					Element invME = findByAttr(controller.getElementsByTagName("source"),"id",jointINVM);
					float[] invBindMatrix = null;
					if(invME!=null)
					{
						invBindMatrix = getFloatArray(invME.getElementsByTagName("float_array").item(0).getTextContent());
					}
					if(jointE==null)continue;
					String[] jointNames = jointE.getElementsByTagName("Name_array").item(0).getTextContent().trim().split("[ ]+");
					int[] jointsIndex = new int[jointNames.length];
					int ji = 0;
					//System.out.println("-----");
					int invMC = 0;
					for(String name:jointNames)
					{
						//System.out.println(" "+name);
						jointsIndex[ji] = this.animator.skeleton.getIndexByJointName(name);
						Joint joint = this.animator.skeleton.joints.get(jointsIndex[ji]);
						if(joint!=null&&invBindMatrix!=null)
						{
							joint.invBindMatrix = splice(invBindMatrix,invMC,16);
							invMC += 16;
						}
						ji++;
					}
					//System.out.println("--END");
					//权重数据
					Element vertex_weights = (Element)controller.getElementsByTagName("vertex_weights").item(0);
					String weightdata_id = findByAttr(vertex_weights.getElementsByTagName("input"),"semantic","WEIGHT").getAttribute("source").substring(1);
					float[] weightsData = getFloatArray(findByAttr(controller.getElementsByTagName("source"),"id",weightdata_id).getElementsByTagName("float_array").item(0).getTextContent());
					ArrayList<Weight[]> weights = new ArrayList<Weight[]>();
					String vcountS = controller.getElementsByTagName("vcount").item(0).getTextContent();
					//关节和权重索引
					String vS = controller.getElementsByTagName("v").item(0).getTextContent();
					int[] vcount = getIntArray(vcountS);
					int[] v = getIntArray(vS);
					int c = 0;
					//System.out.println("————————");
					for(int i=0;i<vcount.length;i++)
					{
						int vn = vcount[i];
						int count = vn*2;
						Weight[] ws = new Weight[vn];
						float[] real_pos = new float[]{
								submeshVertices[i*3]+pos[0],
								submeshVertices[i*3+1]+pos[1],
								submeshVertices[i*3+2]+pos[2]
						};
						for(int n=0;n<count;n+=2)
						{
							int jointIndex = jointsIndex[v[c+n]];
							float weight = weightsData[v[c+n+1]];
							//System.out.println(jointIndex+" , "+weight);
							Weight w = new Weight(jointIndex,weight,real_pos);
							ws[n/2] = w;
							c+=2;
						}
						weights.add(ws);
					}
					ArrayList<Weight[]> real_weights = new ArrayList<Weight[]>();
					int[] index = meshIndex.get(subMesh);
					for(int rwi=0;rwi<index.length;rwi++)
					{
						real_weights.add(weights.get(index[rwi]));
					}
					subMesh.weights = real_weights;
				}
				
				//parse anim
				Animation animation = null;
				ArrayList<float[]> f_time = new ArrayList<float[]>();
				ArrayList<float[]> f_matrix = new ArrayList<float[]>();
				
				NodeList animationsN = dae.getElementsByTagName("library_animations").item(0).getChildNodes();
				int frameNum = 0;
				float maxTime = 0;
				for(int i=0;i<animationsN.getLength();i++)
				{
					Element animationE = null;
					try
					{
						animationE = (Element)animationsN.item(i);
					}
					catch(ClassCastException exception)
					{
						continue;
					}
					String bonename = animationE.getAttribute("name");
					NodeList subanimation = animationE.getElementsByTagName("animation");
					if(subanimation.getLength()>0)
					{
						animationE = (Element)subanimation.item(0);
					}
					NodeList float_array = animationE.getElementsByTagName("float_array");
					int count = Integer.parseInt(((Element)float_array.item(0)).getAttribute("count"));
					float[] times = getFloatArray(float_array.item(0).getTextContent());
					float[] matrices = getFloatArray(float_array.item(1).getTextContent());
					maxTime = times[times.length-1]>maxTime?times[times.length-1]:maxTime;
					frameNum = count>frameNum?count:frameNum;
					f_time.add(times);
					f_matrix.add(matrices);
				}
				
				//设置animation
				animation = new Animation();
				animation.useJointPos = false;
				animation.numFrame = frameNum;
				animation.animDuration = maxTime;
				animation.frameRate = animation.numFrame/animation.animDuration;
				for(int n=0;n<frameNum;n++)
					animation.frameSkeleton.add(new Skeleton());
				this.animator.animations.put(Animator.DEFAULT_ANIMATION, animation);
				
				//生成动画骨骼
				int size = animation.frameSkeleton.size();
				animation.animatedSkeleton = animator.skeleton.clone();
				Joint[] lastFJ = new Joint[size];
				//生成size帧骨骼
				for(int sn=0;sn<size;sn++)
				{
					Skeleton curSke = animation.frameSkeleton.get(sn);
					//生成一帧骨骼
					for(int m=0;m<animator.skeleton.joints.size();m++)
					{
						Joint joint = this.animator.skeleton.joints.get(m).clone();
						if(m>=f_time.size())
						{
							curSke.joints.add(joint);
						}
						else
						{
							float[] matrix = splice(f_matrix.get(m),sn*16,16);
							if(matrix==null)
							{
								curSke.joints.add(lastFJ[m].clone());
							}
							else
							{
								Quaternion orient = Quaternion.createFromMatrix(matrix);
								float[] pos = MatrixOperation.getTranslation(matrix).asFloat();
								joint.orient = orient;
								joint.pos = pos;
								curSke.joints.add(joint);
								lastFJ[m] = joint;
							}
						}
					}
					this.buildDefaultSkeleton(curSke);
					animation.frameSkeleton.add(curSke);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return con;
	}
	
	private Element findByAttr(NodeList list,String attr,String value)
	{
		Element e = null;
		for(int i=0;i<list.getLength();i++)
		{
			Element tmp = (Element)list.item(i);
			if(tmp.getAttribute(attr).equals(value))
			{
				e = tmp;
				break;
			}
		}
		return e;
	}
	
	private float[] splice(float[] arr,int start,int len)
	{
		if(start+len>arr.length)return null;
		float[] result = new float[len];
		for(int i=0;i<len;i++)
		{
			result[i] = arr[start+i];
		}
		return result;
	}
	
	private int[] getIntArray(String str)
	{
		String[] strArr = str.trim().split("[ ]+|[\\n]+");
		int[] arr = new int[strArr.length];
		for(int i=0;i<strArr.length;i++)
		{
			arr[i] = Integer.parseInt(strArr[i]);
		}
		return arr;
	}
	
	private float[] getFloatArray(String str)
	{
		String[] strArr = str.trim().split("[ ]+|[\\n]+");
		float[] arr = new float[strArr.length];
		for(int i=0;i<strArr.length;i++)
		{
			arr[i] = Float.parseFloat(strArr[i]);
		}
		return arr;
	}
	
	private void parseJoints(Element[] es,Skeleton ske,int pindex)
	{
		for(Element e:es)
		{
			String name = e.getAttribute("name");
			float[] matrix = getFloatArray(e.getElementsByTagName("matrix").item(0).getTextContent());
			//System.out.println(name+"("+pindex+")");
			//MatrixState.printMatrix(matrix, 4);
			Quaternion orient = Quaternion.createFromMatrix(matrix);
			float[] pos = MatrixOperation.getTranslation(matrix).asFloat();
			//System.out.println(orient);
			Joint rootJoint = new Joint(name,pindex,pos,orient);
			ske.joints.add(rootJoint);
			int curindex = ske.getIndexByJointName(name);
			NodeList children = e.getChildNodes();
			if(children.getLength()>0)
			{
				ArrayList<Element> ea = new ArrayList<Element>();
				Element[] ce = null;
				for(int i=0;i<children.getLength();i++)
				{
					Element tmp = null;
					try
					{
						tmp = (Element)children.item(i);
					}
					catch(ClassCastException exception)
					{
						continue;
					}
					if(tmp.getAttribute("type").equals("JOINT"))
						ea.add(tmp);
				}
				ce = new Element[ea.size()];
				ea.toArray(ce);
				parseJoints(ce,ske,curindex);
			}
		}
	}
	
	//由骨骼父子关系逐级构建
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
	
	private HashMap<String,MaterialBase> parseMaterial(String parentPath,Document dom)
	{
		HashMap<String,MaterialBase> material = new HashMap<String,MaterialBase>();
		NodeList materials = dom.getElementsByTagName("material");
		HashMap<String,Element> effects = getElementFromNodeList(dom.getElementsByTagName("effect"));
		HashMap<String,Element> images = getElementFromNodeList(dom.getElementsByTagName("image"));
		for(int i=0;i<materials.getLength();i++)
		{
			Element material_e = (Element)materials.item(i);
			String name = material_e.getAttribute("name");
			String effectUrl = ((Element)material_e.getElementsByTagName("instance_effect").item(0)).getAttribute("url").trim();
			effectUrl = effectUrl.substring(1);
			Element effect = effects.get(effectUrl);
			NodeList initNodeList = effect.getElementsByTagName("init_from");
			String imageId = "";
			if(initNodeList.getLength()>0)
				imageId = initNodeList.item(0).getTextContent();
			else
			{
				imageId = ((Element)effect.getElementsByTagName("texture").item(0)).getAttribute("texture");
			}
			Element image = images.get(imageId);
			String imagePath = parentPath+image.getElementsByTagName("init_from").item(0).getTextContent();
			BitmapTexture bitT = new BitmapTexture(new ANE_Bitmap(ctx_src,imagePath));
			TextureMaterial tM = new TextureMaterial(bitT);
			if(DAEParser.this.reverseT)
				tM.reverseTexT = true;
			material.put(name, tM);
		}
		return material;
	}
	
	//id,element
	private HashMap<String,Element> getElementFromNodeList(NodeList list)
	{
		HashMap<String,Element> hsm = new HashMap<String,Element>();
		for(int i=0;i<list.getLength();i++)
		{
			Element e = (Element)list.item(i);
			String id = e.getAttribute("id");
			hsm.put(id, e);
		}
		return hsm;
	}
}
