package com.anengine.independence.base_file;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//独立的文件资源管理
public class FileSystem{
	
	public static String loadFileAsString(String fname,ContextResources ctx)
	{
		String shader_code = null;
		try
		{
			if(ctx.ctx_src==null)
				throw new Exception("ContextResource must not be null,in FlleSystem");
			InputStream ins = ctx.ctx_src.getAssets().open(fname);
			ByteArrayOutputStream bostream = new ByteArrayOutputStream();
			byte[] buffer = new byte[100];
			int len = 0;
			while((len=ins.read(buffer))!=-1)
			{
				bostream.write(buffer, 0, len);
			}
			shader_code = new String(bostream.toByteArray(),"utf-8").replaceAll("\\r\\n", "\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return shader_code;
	}
}
