package com.anengine.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.anengine.animation.Joint;
import com.anengine.animation.data.Weight;
import com.anengine.core.renderer.RenderData;
import com.anengine.independence.base_data.ANE_Buffer;
import com.anengine.independence.base_data.PointsDataManager;
import com.anengine.independence.base_math.Vector3D;
import com.anengine.materials.IMaterial;
import com.anengine.materials.SegmentMaterial;

public class SubMesh{
	private float[] vertices = null;
	private float[] texCoord = null;//纹理坐标
	private short[] indices = null;
	private float[] normal = null;//法向量
	private float[] tangent = null;//切向量
	public ArrayList<Weight[]> weights = null;//骨骼动画中每个顶点对应的关节权重
	protected int vCount = 0;//顶点数目
	protected int iCount = 0;//索引数目
	
	public ANE_Buffer verticesBuffer = null;
	public ANE_Buffer texCoordBuffer = null;
	public ANE_Buffer indicesBuffer = null;
	public ANE_Buffer normalBuffer = null;
	
	private boolean wireframe = false;
	public ANE_Buffer wireframe_indicesBuffer = null;
	public ANE_Buffer wireframe_verticesBuffer = null;
	private boolean hasTangent = false;
	public ANE_Buffer tangentBuffer = null;
	public float[] offsetscaleST = new float[]{0,0,1,1};//finalTexCoord = (texCoor+offset)*scale
	public IMaterial material = null;

	public int drawMode = 1;//1:根据索引绘制 2:vertices
	public Mesh parent = null;
	
	public SubMesh()
	{
		super();
	}
	
	public int vCount()
	{
		return this.vCount;
	}
	
	public int iCount()
	{
		return this.iCount;
	}
	
	public int faces()
	{
		int faces = 0;
		if(this.drawMode==1)
		{
			faces = iCount/3;
		}
		else if(this.drawMode==2)
		{
			faces = vCount/3;
		}
		return faces;
	}

	public void setVertices(float[] vertices)
	{
		this.vertices = vertices;
		vCount = this.vertices.length/3;
		this.verticesBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(this.vertices));
	}
	
	public void setTexCoord(float[] texCoord)
	{
		if(texCoord!=null)
		{
			this.texCoord = texCoord;
			this.texCoordBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(this.texCoord));
		}
	}
	
	public void setIndices(short[] indices)
	{
		this.indices = indices;
		iCount = this.indices.length;
		this.indicesBuffer = new ANE_Buffer(PointsDataManager.getBufferfromShort(indices));
	}
	
	public void setNormal(float[] normal)
	{
		this.normal = normal;
		this.normalBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(normal));
	}
	
	//准备线框数据
	public void enableWireframe(boolean force)
	{
		if(!this.wireframe||force)
		{
			if(this.drawMode==1)
			{
				short[] wireIndices = new short[iCount*2];
				for(int i=0,n=0;n<iCount*2;i+=3,n+=6)
				{
					short start = indices[i];
					wireIndices[n] = start;
					wireIndices[n+1] = indices[i+1];
					wireIndices[n+2] = indices[i+1];
					wireIndices[n+3] = indices[i+2];
					wireIndices[n+4] = indices[i+2];
					wireIndices[n+5] = start;
				}
				this.wireframe = true;
				this.wireframe_indicesBuffer = new ANE_Buffer(PointsDataManager.getBufferfromShort(wireIndices));
			}
			else if(this.drawMode==2)
			{
				float[] wireVertices = new float[vCount*2*3];
				for(int i=0,n=0;n<vCount*6;i+=9,n+=18)
				{
					wireVertices[n] = vertices[i];
					wireVertices[n+1] = vertices[i+1];
					wireVertices[n+2] = vertices[i+2];
					
					wireVertices[n+3] = vertices[i+3];
					wireVertices[n+4] = vertices[i+4];
					wireVertices[n+5] = vertices[i+5];
					
					wireVertices[n+6] = vertices[i+3];
					wireVertices[n+7] = vertices[i+4];
					wireVertices[n+8] = vertices[i+5];
					
					wireVertices[n+9] = vertices[i+6];
					wireVertices[n+10] = vertices[i+7];
					wireVertices[n+11] = vertices[i+8];
					
					wireVertices[n+12] = vertices[i+6];
					wireVertices[n+13] = vertices[i+7];
					wireVertices[n+14] = vertices[i+8];
					
					wireVertices[n+15] = vertices[i];
					wireVertices[n+16] = vertices[i+1];
					wireVertices[n+17] = vertices[i+2];

				}
				this.wireframe = true;
				this.wireframe_verticesBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(wireVertices));
			}
		}
	}
	
	//计算法向量
	public void cacuNormal()
	{
		if(this.drawMode==1)
		{
			HashMap<Integer,HashSet<Vector3D>> hmn = new HashMap<Integer,HashSet<Vector3D>>();
			for(int i=0;i<indices.length;i+=3)
			{
				short[] index = new short[]{indices[i],indices[i+1],indices[i+2]};
				float x0 = vertices[index[0]*3],y0 = vertices[index[0]*3+1],z0 = vertices[index[0]*3+2];
				float x1 = vertices[index[1]*3],y1 = vertices[index[1]*3+1],z1 = vertices[index[1]*3+2];
				float x2 = vertices[index[2]*3],y2 = vertices[index[2]*3+1],z2 = vertices[index[2]*3+2];
				float vxa = x1 - x0,vya = y1 - y0,vza = z1 - z0;
				float vxb = x2 - x0,vyb = y2 - y0,vzb = z2 - z0;
				Vector3D vNormal = Vector3D.getCrossProduct(vxa, vya, vza, vxb, vyb, vzb);
				for(int tmpIndex:index)
				{
					HashSet<Vector3D> hsn = hmn.get(tmpIndex);
					if(hsn==null)
						hsn = new HashSet<Vector3D>();
					hsn.add(vNormal);
					hmn.put(tmpIndex, hsn);
				}
			}
			this.normal = new float[vertices.length];
			for(int i=0;i<hmn.size();i++)
			{
				HashSet<Vector3D> hsn = hmn.get(i);
				float[] avg = Vector3D.getAverage(hsn);
				this.normal[i*3] = avg[0];
				this.normal[i*3+1] = avg[1];
				this.normal[i*3+2] = avg[2];
			}
			this.normalBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(normal));
		}
		else if(this.drawMode==2)
		{
			this.normal = new float[this.vertices.length];
			for(int i=0;i<vertices.length;i+=9)
			{
				float x0 = vertices[i],y0 = vertices[i+1],z0 = vertices[i+2];
				float x1 = vertices[i+3],y1 = vertices[i+4],z1 = vertices[i+5];
				float x2 = vertices[i+6],y2 = vertices[i+7],z2 = vertices[i+8];
				float vxa = x1 - x0,vya = y1 - y0,vza = z1 - z0;
				float vxb = x2 - x0,vyb = y2 - y0,vzb = z2 - z0;
				Vector3D vNormal = Vector3D.getCrossProduct(vxa, vya, vza, vxb, vyb, vzb);
				this.normal[i]=this.normal[i+3]=this.normal[i+6]=vNormal.nx;
				this.normal[i+1]=this.normal[i+4]=this.normal[i+7]=vNormal.ny;
				this.normal[i+2]=this.normal[i+5]=this.normal[i+8]=vNormal.nz;
			}
			this.normalBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(normal));
		}
	}
	
	/*
	 * 计算各顶点的切向量
	 */
	public void cacuTangent(boolean force)
	{
		if(!hasTangent||force)
		{
			float A,B,C;
			float x0,y0,z0;
			float x1,y1,z1;
			tangent = new float[vCount*3];
			for(int i=0;i<vCount;i++)
			{
				A = normal[i*3];B = normal[i*3+1];C = normal[i*3+2];
				x0 = vertices[i*3];y0 = vertices[i*3+1];z0 = vertices[i*3+2];
				
				//三组切向量
				x1 = x0+1;z1 = z0+1;y1 = (C*(z0-z1)+A*(x0-x1))/B+y0;
				if(x1<50)
				{
					tangent[i*3] = x1;tangent[i*3+1] = y1;tangent[i*3+2] = z1;
					continue;
				}
				
				x1 = x0+1;y1 = y0+1;z1 = (A*(x0-x1)+B*(y0-y1))/C+z0;
				if(y1<50)
				{
					tangent[i*3] = x1;tangent[i*3+1] = y1;tangent[i*3+2] = z1;
					continue;
				}
				
				y1 = y0+1;z1 = z0+1;x1=(B*(y0-y1)+C*(z0-z1))/A+x0;
				tangent[i*3] = x1;tangent[i*3+1] = y1;tangent[i*3+2] = z1;
			}
			this.tangentBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(tangent));
			hasTangent = true;
		}
	}
	
	public void prepare(RenderData renderData)
	{
		if(!(this.material instanceof SegmentMaterial))
		{
			this.wireframe = false;
			renderData.iCount = iCount;
			renderData.vCount = vCount;
			renderData.indicesBuffer = this.indicesBuffer;
		}
		else
		{
			renderData.indicesBuffer = this.wireframe_indicesBuffer;
			if(this.drawMode==1)
				renderData.iCount = iCount*2;
			else if(this.drawMode==2)
				renderData.vCount = vCount*2;
		}
	}
	
	//计算骨骼影响
	public void prepareSkeletonMesh()
	{
		if(parent!=null&&parent.animator!=null)
		{
			float[] resultVec3 = new float[3];
			for(int i=0;i<vCount;i++)
			{
				Weight[] w = weights.get(i);
				vertices[i*3]=vertices[i*3+1]=vertices[i*3+2]=0;
				for(int n=0;n<w.length;n++)
				{
					Weight weight = w[n];
					Joint joint = parent.animator.animatedSkeleton.joints.get(weight.joint_id);
					joint.orient.Multiply(resultVec3, weight.pos);
					if(parent.animator.curAnimation().useJointPos)
					{
						vertices[i*3] += (resultVec3[0]+joint.pos[0])*joint.scaleXYZ[0]*weight.bias;
						vertices[i*3+1] += (resultVec3[1]+joint.pos[0])*joint.scaleXYZ[1]*weight.bias;
						vertices[i*3+2] += (resultVec3[2]+joint.pos[0])*joint.scaleXYZ[2]*weight.bias;
					}
					else
					{
						vertices[i*3] += (resultVec3[0])*joint.scaleXYZ[0]*weight.bias;
						vertices[i*3+1] += (resultVec3[1])*joint.scaleXYZ[1]*weight.bias;
						vertices[i*3+2] += (resultVec3[2])*joint.scaleXYZ[2]*weight.bias;
					}
				}
			}
			this.verticesBuffer = new ANE_Buffer(PointsDataManager.getBufferfromFloat(this.vertices));
		}
	}
	
	public SubMesh clone()
	{
		SubMesh tmp = new SubMesh();
		tmp.setVertices(vertices);
		tmp.setTexCoord(texCoord);
		tmp.setIndices(indices);
		tmp.setNormal(this.normal);
		tmp.material = this.material;
		return tmp;
	}
}
