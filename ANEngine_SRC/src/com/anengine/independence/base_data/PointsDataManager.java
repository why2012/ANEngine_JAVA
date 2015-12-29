package com.anengine.independence.base_data;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class PointsDataManager {
	
	public static Buffer getBufferfromFloat(float[] points)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(points.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(points);
		fb.position(0);
		return fb;
	}
	
	public static Buffer getBufferfromInt(int[] points)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(points.length*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer fb = bb.asIntBuffer();
		fb.put(points);
		fb.position(0);
		return fb;
	}
	
	public static Buffer getBufferfromShort(short[] points)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(points.length*4);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer fb = bb.asShortBuffer();
		fb.put(points);
		fb.position(0);
		return fb;
	}
	
	public static Buffer getBufferfromByte(byte[] points)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(points.length*4);
		bb.order(ByteOrder.nativeOrder());
		bb.put(points);
		bb.position(0);
		return bb;
	}
}
