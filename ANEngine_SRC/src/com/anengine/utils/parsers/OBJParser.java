package com.anengine.utils.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.anengine.core.ObjectContainer3D;
import com.anengine.entity.Mesh;
import com.anengine.entity.SubMesh;
import com.anengine.independence.base_data.ANE_ArrayList;
import com.anengine.independence.base_data.ANE_Bitmap;
import com.anengine.independence.base_file.ContextResources;
import com.anengine.independence.base_math.Vector3D;
import com.anengine.materials.ColorMaterial;
import com.anengine.materials.MaterialBase;
import com.anengine.materials.TextureMaterial;
import com.anengine.textures.BitmapTexture;

public class OBJParser extends ParserBase {
	
	public boolean reverseT = false;

	public OBJParser(ContextResources ctx_src)
	{
		super(ctx_src);
	}
	
	//区分分组
	public ObjectContainer3D parse(String path)
	{
		ObjectContainer3D objcon = new ObjectContainer3D();
		
		try
		{
			String parentPath = new File(path).getParent();
			if(parentPath!=null)
				parentPath += "\\";
			else
				parentPath = "";
			parentPath = parentPath.replaceAll("\\\\","/");
			
			InputStream ins = ctx_src.ctx_src.getAssets().open(path);
			InputStreamReader insr = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(insr);
			String tmps = null;
			MtlLibParser mtlLib = null;
			ANE_ArrayList<Float> vertices = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> texCoords = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> normal = new ANE_ArrayList<Float>();
			
			ANE_ArrayList<Float> verticesA = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> texCoordsA = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> normalA = new ANE_ArrayList<Float>();
			
			ANE_ArrayList<Integer> indicesA = new ANE_ArrayList<Integer>();
			HashMap<Integer,HashSet<Vector3D>> hmn = new HashMap<Integer,HashSet<Vector3D>>();
			
			boolean tex = false;
			boolean nor = false;
			boolean sec  =false;
			boolean usemtl = false;
			
			SubMesh submesh = null;

			while(sec||(tmps=br.readLine())!=null)
			{
				sec = false; 
				String[] tmpssp = tmps.split("[ ]+");
				if(tmpssp[0].trim().equals("mtllib"))
				{
					String mtllibPath = parentPath+tmpssp[1].trim();
					mtllibPath = mtllibPath.replaceAll("\\\\","/");
					
					System.out.println("mtllib:"+mtllibPath);
					BufferedReader mbr = new BufferedReader(new InputStreamReader(ctx_src.ctx_src.getAssets().open(mtllibPath)));
					mtlLib = new MtlLibParser(mbr);
					mtlLib.dir = parentPath;
					mtlLib.parse();
					System.out.println(mtlLib);
				}
				//兼容顶点数据不在g内的obj格式
				else if(tmpssp[0].trim().equals("v"))
				{
					verticesA.add(Float.parseFloat(tmpssp[1]));
					verticesA.add(Float.parseFloat(tmpssp[2]));
					verticesA.add(Float.parseFloat(tmpssp[3]));
				}
				else if(tmpssp[0].trim().equals("vt"))
				{
					texCoordsA.add(Float.parseFloat(tmpssp[1]));
					texCoordsA.add(Float.parseFloat(tmpssp[2]));
					tex = true;
				}
				else if(tmpssp[0].trim().equals("vn"))
				{
					normalA.add(Float.parseFloat(tmpssp[1]));
					normalA.add(Float.parseFloat(tmpssp[2]));
					normalA.add(Float.parseFloat(tmpssp[3]));
					nor = true;
				}
				else if(tmpssp[0].trim().equals("g")&&tmpssp.length>1)
				{
					System.out.println("start : "+tmps);
					Mesh mesh = new Mesh();
					objcon.addObject3D(mesh);
	
					while((tmps=br.readLine())!=null)
					{
						tmpssp = tmps.split("[ ]+");
						if(tmpssp[0].trim().equals("v"))
						{
							verticesA.add(Float.parseFloat(tmpssp[1]));
							verticesA.add(Float.parseFloat(tmpssp[2]));
							verticesA.add(Float.parseFloat(tmpssp[3]));
						}
						else if(tmpssp[0].trim().equals("vt"))
						{
							texCoordsA.add(Float.parseFloat(tmpssp[1]));
							texCoordsA.add(Float.parseFloat(tmpssp[2]));
							tex = true;
						}
						else if(tmpssp[0].trim().equals("vn"))
						{
							normalA.add(Float.parseFloat(tmpssp[1]));
							normalA.add(Float.parseFloat(tmpssp[2]));
							normalA.add(Float.parseFloat(tmpssp[3]));
							nor = true;
						}
						else if(tmpssp[0].trim().equals("usemtl"))
						{
							if(submesh!=null)//处理上一个submesh
							{
								//vertices
								int v_len = vertices.length();
								float[] vertices_tmp = new float[v_len];
								for(int i=0;i<v_len;i++)
									vertices_tmp[i] = vertices.get(i);
								submesh.setVertices(vertices_tmp);
								
								//texCoords
								if(tex)
								{
									int t_len = texCoords.length();
									float[] texCoords_tmp = new float[t_len];
									for(int i=0;i<t_len;i++)
										texCoords_tmp[i] = texCoords.get(i);
									submesh.setTexCoord(texCoords_tmp);
								}
								
								//normal
								if(nor)
								{
									int n_len = normal.length();
									float[] normal_tmp = new float[n_len];
									for(int i=0;i<n_len;i++)
										normal_tmp[i] = normal.get(i);
									submesh.setNormal(normal_tmp);
								}
								else
								{
									int n_len = indicesA.length();
									float[] normal_tmp = new float[n_len*3];
									int c = 0;
									for(Integer i:indicesA.list)
									{
										HashSet<Vector3D> hsn = hmn.get(i);
										float[] tn = Vector3D.getAverage(hsn);
										normal_tmp[c++] = tn[0];
										normal_tmp[c++] = tn[1];
										normal_tmp[c++] = tn[2];
									}
									submesh.setNormal(normal_tmp);
								}
								vertices.clear();
								texCoords.clear();
								normal.clear();
								indicesA.clear();
							}
							//System.out.println("SubMesh mtl:"+mtlLib.mtl.get(tmpssp[1].trim()));
							submesh = new SubMesh();
							submesh.drawMode = 2;
							submesh.material = mtlLib.mtl.get(tmpssp[1].trim()).map_Kd_textureMaterial;
							mesh.subMeshes.add(submesh);
							submesh.parent = mesh;
							usemtl = true;
						}
						else if(tmpssp[0].trim().equals("f"))
						{
							String[] _1 = tmpssp[1].split("/");
							String[] _2 = tmpssp[2].split("/");
							String[] _3 = tmpssp[3].split("/");
							//vertices
							int[] index = new int[3];
							index[0] = Integer.parseInt(_1[0])-1;
							index[1] = Integer.parseInt(_2[0])-1;
							index[2] = Integer.parseInt(_3[0])-1;
							indicesA.add(index[0]);
							indicesA.add(index[1]);
							indicesA.add(index[2]);
							float x0,y0,z0,x1,y1,z1,x2,y2,z2;
							x0 = verticesA.get(index[0]*3);y0 = verticesA.get(index[0]*3+1); z0 = verticesA.get(index[0]*3+2);
							x1 = verticesA.get(index[1]*3);y1 = verticesA.get(index[1]*3+1); z1 = verticesA.get(index[1]*3+2);
							x2 = verticesA.get(index[2]*3);y2 = verticesA.get(index[2]*3+1); z2 = verticesA.get(index[2]*3+2);
							vertices.add(x0);
							vertices.add(y0);
							vertices.add(z0);
							
							vertices.add(x1);
							vertices.add(y1);
							vertices.add(z1);
							
							vertices.add(x2);
							vertices.add(y2);
							vertices.add(z2);
							
							//如果模型中没有法向量数据则自行计算
							if(!nor)
							{
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
							//normal
							if(nor)
							{
								index[0] = Integer.parseInt(_1[2])-1;
								normal.add(normalA.get(index[0]*3));
								normal.add(normalA.get(index[0]*3+1));
								normal.add(normalA.get(index[0]*3+2));
								index[1] = Integer.parseInt(_2[2])-1;
								normal.add(normalA.get(index[1]*3));
								normal.add(normalA.get(index[1]*3+1));
								normal.add(normalA.get(index[1]*3+2));
								index[2] = Integer.parseInt(_3[2])-1;
								normal.add(normalA.get(index[2]*3));
								normal.add(normalA.get(index[2]*3+1));
								normal.add(normalA.get(index[2]*3+2));
							}
							//texCoord
							if(tex)
							{
								index[0] = Integer.parseInt(_1[1])-1;
								texCoords.add(texCoordsA.get(index[0]*2));
								texCoords.add(texCoordsA.get(index[0]*2+1));
								index[1] = Integer.parseInt(_2[1])-1;
								texCoords.add(texCoordsA.get(index[1]*2));
								texCoords.add(texCoordsA.get(index[1]*2+1));
								index[2] = Integer.parseInt(_3[1])-1;
								texCoords.add(texCoordsA.get(index[2]*2));
								texCoords.add(texCoordsA.get(index[2]*2+1));
							}
						}
						else if(tmpssp[0].trim().equals("g")&&tmpssp.length==1)
						{
							break;
						}
						else if(tmpssp[0].trim().equals("g"))
						{
							sec = true;
							break;
						}
					}
					
					System.out.println("end : "+tmps);
					//如果模型中没有使用mtl库
					if(!usemtl)
					{
						submesh = new SubMesh();
						mesh.subMeshes.add(submesh);
						
						//vertices
						int v_len = vertices.length();
						float[] vertices_tmp = new float[v_len];
						for(int i=0;i<v_len;i++)
							vertices_tmp[i] = vertices.get(i);
						submesh.setVertices(vertices_tmp);
						
						//texCoords
						if(tex)
						{
							int t_len = texCoords.length();
							float[] texCoords_tmp = new float[t_len];
							for(int i=0;i<t_len;i++)
								texCoords_tmp[i] = texCoords.get(i);
							submesh.setTexCoord(texCoords_tmp);
						}
						
						//normal
						if(nor)
						{
							int n_len = normal.length();
							float[] normal_tmp = new float[n_len];
							for(int i=0;i<n_len;i++)
								normal_tmp[i] = normal.get(i);
							submesh.setNormal(normal_tmp);
						}
						else
						{
							int n_len = indicesA.length();
							float[] normal_tmp = new float[n_len*3];
							int c = 0;
							for(Integer i:indicesA.list)
							{
								HashSet<Vector3D> hsn = hmn.get(i);
								float[] tn = Vector3D.getAverage(hsn);
								normal_tmp[c++] = tn[0];
								normal_tmp[c++] = tn[1];
								normal_tmp[c++] = tn[2];
								submesh.setNormal(normal_tmp);
							}
						}
						vertices.clear();
						texCoords.clear();
						normal.clear();
						indicesA.clear();
					}
				}
			}
			//处理最后一个mtl
			if(usemtl)
			{
				//vertices
				int v_len = vertices.length();
				float[] vertices_tmp = new float[v_len];
				for(int i=0;i<v_len;i++)
					vertices_tmp[i] = vertices.get(i);
				submesh.setVertices(vertices_tmp);
				
				//texCoords
				if(tex)
				{
					int t_len = texCoords.length();
					float[] texCoords_tmp = new float[t_len];
					for(int i=0;i<t_len;i++)
						texCoords_tmp[i] = texCoords.get(i);
					submesh.setTexCoord(texCoords_tmp);
				}
				
				//normal
				if(nor)
				{
					int n_len = normal.length();
					float[] normal_tmp = new float[n_len];
					for(int i=0;i<n_len;i++)
						normal_tmp[i] = normal.get(i);
					submesh.setNormal(normal_tmp);
				}
				else
				{
					int n_len = indicesA.length();
					float[] normal_tmp = new float[n_len*3];
					int c = 0;
					for(Integer i:indicesA.list)
					{
						HashSet<Vector3D> hsn = hmn.get(i);
						float[] tn = Vector3D.getAverage(hsn);
						normal_tmp[c++] = tn[0];
						normal_tmp[c++] = tn[1];
						normal_tmp[c++] = tn[2];
					}
					submesh.setNormal(normal_tmp);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.gc();
		
		return objcon;
	}
	
	//不区分分组 无mtl解析功能
	public ObjectContainer3D singleParse(String path)
	{
		ObjectContainer3D objcon = new ObjectContainer3D();
		
		try
		{
			InputStream ins = ctx_src.ctx_src.getAssets().open(path);
			InputStreamReader insr = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(insr);
			String tmps = null;
			ANE_ArrayList<Float> verticesA = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> texCoordsA = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> normalA = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> vertices = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> texCoords = new ANE_ArrayList<Float>();
			ANE_ArrayList<Float> normal = new ANE_ArrayList<Float>();
			ANE_ArrayList<Integer> indicesA = new ANE_ArrayList<Integer>();
			HashMap<Integer,HashSet<Vector3D>> hmn = new HashMap<Integer,HashSet<Vector3D>>();
			
			boolean tex = false;
			boolean nor = false;
			System.out.println("single start");
			while((tmps=br.readLine())!=null)
			{
				String[] tmpssp = tmps.split("[ ]+");
				if(tmpssp[0].trim().equals("v"))
				{
					verticesA.add(Float.parseFloat(tmpssp[1]));
					verticesA.add(Float.parseFloat(tmpssp[2]));
					verticesA.add(Float.parseFloat(tmpssp[3]));
				}
				else if(tmpssp[0].trim().equals("vt"))
				{
					texCoordsA.add(Float.parseFloat(tmpssp[1])/2.0f);
					texCoordsA.add(Float.parseFloat(tmpssp[2])/2.0f);
					tex = true;
				}
				else if(tmpssp[0].trim().equals("vn"))
				{
					normalA.add(Float.parseFloat(tmpssp[1]));
					normalA.add(Float.parseFloat(tmpssp[2]));
					normalA.add(Float.parseFloat(tmpssp[3]));
					nor = true;
				}
				else if(tmpssp[0].trim().equals("f"))
				{
					String[] _1 = tmpssp[1].split("/");
					String[] _2 = tmpssp[2].split("/");
					String[] _3 = tmpssp[3].split("/");
					//vertices
					int[] index = new int[3];
					index[0] = Integer.parseInt(_1[0])-1;
					index[1] = Integer.parseInt(_2[0])-1;
					index[2] = Integer.parseInt(_3[0])-1;
					indicesA.add(index[0]);
					indicesA.add(index[1]);
					indicesA.add(index[2]);
					float x0,y0,z0,x1,y1,z1,x2,y2,z2;
					x0 = verticesA.get(index[0]*3);y0 = verticesA.get(index[0]*3+1); z0 = verticesA.get(index[0]*3+2);
					x1 = verticesA.get(index[1]*3);y1 = verticesA.get(index[1]*3+1); z1 = verticesA.get(index[1]*3+2);
					x2 = verticesA.get(index[2]*3);y2 = verticesA.get(index[2]*3+1); z2 = verticesA.get(index[2]*3+2);
					vertices.add(x0);
					vertices.add(y0);
					vertices.add(z0);
					
					vertices.add(x1);
					vertices.add(y1);
					vertices.add(z1);
					
					vertices.add(x2);
					vertices.add(y2);
					vertices.add(z2);
					
					//如果模型中没有法向量数据则自行计算
					if(!nor)
					{
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
					//normal
					if(nor)
					{
						index[0] = Integer.parseInt(_1[2])-1;
						normal.add(normalA.get(index[0]*3));
						normal.add(normalA.get(index[0]*3+1));
						normal.add(normalA.get(index[0]*3+2));
						index[1] = Integer.parseInt(_2[2])-1;
						normal.add(normalA.get(index[1]*3));
						normal.add(normalA.get(index[1]*3+1));
						normal.add(normalA.get(index[1]*3+2));
						index[2] = Integer.parseInt(_3[2])-1;
						normal.add(normalA.get(index[2]*3));
						normal.add(normalA.get(index[2]*3+1));
						normal.add(normalA.get(index[2]*3+2));
					}
					//texCoord
					if(tex)
					{
						index[0] = Integer.parseInt(_1[1])-1;
						texCoords.add(texCoordsA.get(index[0]*2));
						texCoords.add(texCoordsA.get(index[0]*2+1));
						index[1] = Integer.parseInt(_2[1])-1;
						texCoords.add(texCoordsA.get(index[1]*2));
						texCoords.add(texCoordsA.get(index[1]*2+1));
						index[2] = Integer.parseInt(_3[1])-1;
						texCoords.add(texCoordsA.get(index[2]*2));
						texCoords.add(texCoordsA.get(index[2]*2+1));
					}
				}
			}
			
			System.out.println("single end");
			
			Mesh mesh = new Mesh();
			SubMesh obj3d = new SubMesh();
			obj3d.drawMode = 2;
			mesh.subMeshes.add(obj3d);
			obj3d.parent = mesh;
			
			//vertices
			int v_len = vertices.length();
			float[] _vertices = new float[v_len];
			for(int i=0;i<v_len;i++)
				_vertices[i] = vertices.get(i);
			obj3d.setVertices(_vertices);
			
			//texCoords
			if(tex)
			{
				int t_len = texCoords.length();
				float[] texCoord = new float[t_len];
				for(int i=0;i<t_len;i++)
					texCoord[i] = texCoords.get(i);
				obj3d.setTexCoord(texCoord);
			}
			
			//normal
			if(nor)
			{
				int n_len = normal.length();
				float[] _normal = new float[n_len];
				for(int i=0;i<n_len;i++)
					_normal[i] = normal.get(i);
				obj3d.setNormal(_normal);
			}
			else
			{
				int n_len = indicesA.length();
				float[] _normal = new float[n_len*3];
				int c = 0;
				for(Integer i:indicesA.list)
				{
					HashSet<Vector3D> hsn = hmn.get(i);
					float[] tn = Vector3D.getAverage(hsn);
					_normal[c++] = tn[0];
					_normal[c++] = tn[1];
					_normal[c++] = tn[2];
				}
				obj3d.setNormal(_normal);
			}
			
			objcon.addObject3D(mesh);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.gc();
		
		return objcon;
	}
	
	public class MtlLibParser
	{
		public HashMap<String,MtlItem> mtl = new HashMap<String,MtlItem>();
		public String dir = "";
		public BufferedReader br;
		
		public MtlLibParser(BufferedReader br) 
		{
			this.br = br;
		}
		
		public void parse() throws Exception
		{
			String tmps;
			MtlItem mitem = null;
			while((tmps=br.readLine())!=null)
			{
				String[] tmpsp = tmps.split("[ ]+");
				if(tmpsp[0].trim().equals("newmtl"))
				{
					mitem = new MtlItem();
					mitem.name = tmpsp[1].trim();
					mtl.put(mitem.name, mitem);
				}
				else if(tmpsp[0].trim().equals("map_Kd"))
				{
					if(mitem==null)
						throw new Exception("Mtl parse error:attribute before mtl define");
					if(tmpsp.length==2)
					{
						mitem.map_Kd = dir+tmpsp[1];
						mitem.map_Kd_textureMaterial = new TextureMaterial(new BitmapTexture(new ANE_Bitmap(ctx_src,mitem.map_Kd)));
						if(OBJParser.this.reverseT)
							((TextureMaterial)mitem.map_Kd_textureMaterial).reverseTexT = true;
					}
				}
				else if(tmpsp[0].trim().equals("Kd"))
				{
					mitem.color = new float[4];
					mitem.color[0] = Float.parseFloat(tmpsp[1]);
					mitem.color[1] = Float.parseFloat(tmpsp[2]);
					mitem.color[2] = Float.parseFloat(tmpsp[3]);
					mitem.color[3] = 1;
					mitem.map_Kd_textureMaterial = new ColorMaterial(mitem.color[0],mitem.color[1],mitem.color[2],mitem.color[3]);
				}
			}
		}
		
		public String toString()
		{
			String r = "";
			Set<String> keySet = mtl.keySet();
			for(String s:keySet)
			{
				r += mtl.get(s).toString();
			}
			return r;
		}
		
		public class MtlItem
		{
			public String name;
			public String map_Kd;
			public float[] color;
			public MaterialBase map_Kd_textureMaterial;
			
			public String toString()
			{
				String s = "Mtl--{name:"+name+" , mapKd:"+map_Kd+"}\n";
				return s;
			}
		}
	}
}
