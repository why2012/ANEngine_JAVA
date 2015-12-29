package com.anengine.debug;

import java.util.Stack;

import com.anengine.core.Scene3D;
import com.anengine.core.Shader;
import com.anengine.debug.MetaElement.Line;
import com.anengine.independence.base_gl.Constant;
import com.anengine.independence.base_gl.GLWrapper;
import com.anengine.independence.base_math.Vector3D;

//所有绘制的元素都是一次性的，调用clear全部清除
public class FreeDrawer {
	
	private Stack<FreeDrawerData> drawerData = new Stack<FreeDrawerData>();
	private static FreeDrawer freeDrawer = null;
	private Shader shader = null;
	private Scene3D scene = null;
	
	private Vector3D DrawLine_CurPoint = null;
	
	private FreeDrawer()
	{
		shader = Shader.getOne(Shader.PATH, "shaders/vertex_freedrawer.sh", "shaders/frag_freedrawer.sh");
	}
	
	public static FreeDrawer InitDrawer(Scene3D scene)
	{
		if(freeDrawer==null)
			freeDrawer = new FreeDrawer();
		freeDrawer.scene = scene;
		return freeDrawer;
	}
	
	public static FreeDrawer Drawer()
	{
		return freeDrawer;
	}
	
	public void push()
	{
		drawerData.push(new FreeDrawerData());
	}
	
	public void pop()
	{
		drawerData.pop();
	}
	
	public void drawLine(Vector3D pos1,Vector3D pos2)
	{
		if(drawerData.size()==0)return;
		drawerData.peek().elements.add(new Line(pos1,pos2,shader));
	}
	
	public void drawLine(Vector3D pos1,Vector3D pos2,float width)
	{
		if(drawerData.size()==0)return;
		Line line = new Line(pos1,pos2,shader);
		line.width = width;
		drawerData.peek().elements.add(line);
	}
	
	public void drawLine(Vector3D pos1,Vector3D pos2,float width,float[] color)
	{
		if(drawerData.size()==0)return;
		Line line = new Line(pos1,pos2,color,shader);
		line.width = width;
		drawerData.peek().elements.add(line);
	}
	
	public void drawLine(Vector3D pos1,Vector3D pos2,float[] color)
	{
		if(drawerData.size()==0)return;
		drawerData.peek().elements.add(new Line(pos1,pos2,color,shader));
	}
	
	public void moveTo(Vector3D pos)
	{
		DrawLine_CurPoint = pos;
	}
	
	public void lineTo(Vector3D pos)
	{
		if(DrawLine_CurPoint==null)return;
		this.drawLine(DrawLine_CurPoint, pos);
		DrawLine_CurPoint = pos;
	}
	
	public void lineTo(Vector3D pos,float[] color)
	{
		if(DrawLine_CurPoint==null)return;
		this.drawLine(DrawLine_CurPoint, pos,color);
		DrawLine_CurPoint = pos;
	}
	
	public void lineTo(Vector3D pos,float width)
	{
		if(DrawLine_CurPoint==null)return;
		this.drawLine(DrawLine_CurPoint, pos,width);
		DrawLine_CurPoint = pos; 
	}
	
	public void lineTo(Vector3D pos,float width,float[] color)
	{
		if(DrawLine_CurPoint==null)return;
		this.drawLine(DrawLine_CurPoint, pos,width,color);
		DrawLine_CurPoint = pos;
	}
	
	public void depthTest(boolean b)
	{
		if(drawerData.size()==0)return;
		drawerData.peek().enableDepthTest = b;
	}
	
	public void render()
	{
		if(this.scene==null)return;
		float[] finalMatrix = this.scene.camera3d.getFinalMatrix();
		GLWrapper.glUseProgram(shader.program);
		for(FreeDrawerData drawer:drawerData)
		{
			if(!drawer.enableDepthTest)
				GLWrapper.glDisable(Constant.GL_DEPTH_TEST);
			drawer.render(finalMatrix);
			if(!drawer.enableDepthTest)
				GLWrapper.glEnable(Constant.GL_DEPTH_TEST);
		}
	}
	
	public void clear()
	{
		drawerData.clear();
	}
}
