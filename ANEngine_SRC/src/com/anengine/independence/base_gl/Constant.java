package com.anengine.independence.base_gl;

import android.opengl.GLES20;

/*
 *基本常量 
 */
 
public class Constant {
	public static float UNIT_SIZE = 0.5f;
	public static float UNIT_SIZE_2D = UNIT_SIZE;
	
	public static String ES20_ERROR_TAG = "ES20_ERROR";
	public static String ANENGINE_ERROR_TAG = "ANENGINE_ERROR";
	
	//OpenGL ES2.0
	public static int GL_NO_ERROR = GLES20.GL_NO_ERROR;
	public static int GL_TRUE = GLES20.GL_TRUE;
	
	//data type
	public static int GL_UNSIGNED_BYTE = GLES20.GL_UNSIGNED_BYTE;
	public static int GL_FLOAT = GLES20.GL_FLOAT;
	public static int GL_UNSIGNED_SHORT = GLES20.GL_UNSIGNED_SHORT;
	public static int GL_UNSIGNED_SHORT_5_6_5 = GLES20.GL_UNSIGNED_SHORT_5_6_5;
	public static int GL_UNSIGNED_SHORT_5_5_5_1 = GLES20.GL_UNSIGNED_SHORT_5_5_5_1;
	public static int GL_UNSIGNED_SHORT_4_4_4_4 = GLES20.GL_UNSIGNED_SHORT_4_4_4_4;
	
	//glEnable
	public static int GL_DEPTH_TEST = GLES20.GL_DEPTH_TEST;
	public static int GL_CULL_FACE = GLES20.GL_CULL_FACE;
	public static int GL_BLEND = GLES20.GL_BLEND;
	
	//blend func
	public static int GL_SRC_ALPHA = GLES20.GL_SRC_ALPHA;
	public static int GL_ONE_MINUS_SRC_ALPHA = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	
	//front face
	public static int GL_CW = GLES20.GL_CW;//顺时针
	public static int GL_CCW = GLES20.GL_CCW;//逆时针，默认
	
	//clear type
	public static int GL_DEPTH_BUFFER_BIT = GLES20.GL_DEPTH_BUFFER_BIT;
	public static int GL_COLOR_BUFFER_BIT = GLES20.GL_COLOR_BUFFER_BIT;
	public static int GL_STENCIL_BUFFER_BIT = GLES20.GL_STENCIL_BUFFER_BIT;
	
	//stencil test
	public static int GL_STENCIL_TEST = GLES20.GL_STENCIL_TEST;
	public static int GL_ALWAYS = GLES20.GL_ALWAYS;
	public static int GL_REPLACE = GLES20.GL_REPLACE;
	public static int GL_EQUAL = GLES20.GL_EQUAL;
	public static int GL_KEEP = GLES20.GL_KEEP;
	
	//draw mode
	public static int GL_TRIANGLES  = GLES20.GL_TRIANGLES;
	public static int GL_TRIANGLE_FAN = GLES20.GL_TRIANGLE_FAN;
	public static int GL_TRIANGLE_STRIP = GLES20.GL_TRIANGLE_STRIP;
	public static int GL_LINES = GLES20.GL_LINES;
	public static int GL_LINE_LOOP = GLES20.GL_LINE_LOOP;
	public static int GL_LINE_STRIP = GLES20.GL_LINE_STRIP;
	public static int GL_POINTS = GLES20.GL_POINTS; 
	
	//textures
	public static int GL_TEXTURE_2D = GLES20.GL_TEXTURE_2D;
	public static int GL_TEXTURE_MIN_FILTER = GLES20.GL_TEXTURE_MIN_FILTER;
	public static int GL_TEXTURE_MAG_FILTER = GLES20.GL_TEXTURE_MAG_FILTER;
	public static int GL_TEXTURE_WRAP_S = GLES20.GL_TEXTURE_WRAP_S;
	public static int GL_TEXTURE_WRAP_T = GLES20.GL_TEXTURE_WRAP_T;
	public static int GL_NEAREST = GLES20.GL_NEAREST;
	public static int GL_NEAREST_MIPMAP_NEAREST = GLES20.GL_NEAREST_MIPMAP_NEAREST;
	public static int GL_LINEAR_MIPMAP_NEAREST = GLES20.GL_LINEAR_MIPMAP_NEAREST;
	public static int GL_LINEAR = GLES20.GL_LINEAR;
	public static int GL_NEAREST_MIPMAP_LINEAR = GLES20.GL_NEAREST_MIPMAP_LINEAR;
	public static int GL_LINEAR_MIPMAP_LINEAR = GLES20.GL_LINEAR_MIPMAP_LINEAR;
	public static int GL_CLAMP_TO_EDGE = GLES20.GL_CLAMP_TO_EDGE;
	public static int GL_REPEAT = GLES20.GL_REPEAT;
	public static int GL_TEXTURE0 = GLES20.GL_TEXTURE0;
	public static int GL_TEXTURE1 = GLES20.GL_TEXTURE1;
	public static int GL_TEXTURE2 = GLES20.GL_TEXTURE2;
	public static int GL_TEXTURE3 = GLES20.GL_TEXTURE3;
	public static int GL_TEXTURE4 = GLES20.GL_TEXTURE4;
	public static int GL_TEXTURE5 = GLES20.GL_TEXTURE5;
	public static int GL_TEXTURE6 = GLES20.GL_TEXTURE6;
	public static int GL_TEXTURE7 = GLES20.GL_TEXTURE7;
	public static int GL_TEXTURE8 = GLES20.GL_TEXTURE8;
	public static int GL_TEXTURE9 = GLES20.GL_TEXTURE9;
	public static int GL_TEXTURE10 = GLES20.GL_TEXTURE10;
	public static int GL_TEXTURE11 = GLES20.GL_TEXTURE11;
	public static int GL_TEXTURE12 = GLES20.GL_TEXTURE12;
	
	//render buffer
	public static int GL_ALPHA = GLES20.GL_ALPHA;
	public static int GL_RGBA4 = GLES20.GL_RGBA4;
	public static int GL_RGBA = GLES20.GL_RGBA;
	public static int GL_RGB565 = GLES20.GL_RGB565;
	public static int GL_RGB5_A1 = GLES20.GL_RGB5_A1;
	public static int GL_DEPTH_COMPONENT16 = GLES20.GL_DEPTH_COMPONENT16;
	public static int GL_DEPTH_COMPONENT = GLES20.GL_DEPTH_COMPONENT;
	public static int GL_STENCIL_INDEX8 = GLES20.GL_STENCIL_INDEX8;
}
