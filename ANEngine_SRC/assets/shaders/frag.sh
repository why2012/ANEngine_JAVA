precision mediump float;

#define MAX_LIGHT 20
uniform int lightCount;//最大为20
uniform int lightOff;//是否关闭灯光效果
uniform mat4 transformMatrix;
uniform vec3 lightPosition[MAX_LIGHT];
uniform vec4 lightColor[MAX_LIGHT];
uniform float lightAmbient[MAX_LIGHT];
uniform float lightStrength[MAX_LIGHT];
uniform vec3 lightAttenuation[MAX_LIGHT];//衰减系数 ，for simplelight ,linear and exp , besides optional spotLightExp
uniform vec2 lightAttenuationRange[MAX_LIGHT];//衰减范围，start end
uniform vec4 spotlightAngleCosDirection[MAX_LIGHT];//spotlight 张角和方向
varying float vamdiffuse;
varying float vamspecular;
varying float vamemission;
varying float varough;
varying vec3 vacameraLoc;
uniform int materialType;


//for shadow
uniform highp mat4 lightFinalMatrix;
uniform highp vec3 shadowLight;
uniform int receiveShadow;
varying vec4 tPosition;//transformed vposition

varying vec4 vaColor;
varying vec3 vaPosition;
varying vec3 vaNormal;
varying vec2 vatexCoord;
varying vec3 vaTangent;

uniform sampler2D shadow_texture;
uniform sampler2D texture_specular;//高光图
uniform sampler2D texture_normal;//法线图
uniform sampler2D texture01;

uniform vec4 offsetscaleST;
uniform int reverseTexT;
uniform int enable_specularmap;
uniform int enable_normalmap;

mat3 normalmap_rotation;

void pointDirectionLight(in vec3 normal,
				inout vec4 diffuse,
				inout vec4 specular,
				in vec3 lightPos,
				in float shininess,//粗糙度
				in float lightAmbient,
				in vec4 lightDiffuse,
				in vec4 lightSpecular)
{	 			
  	 vec3 normalTarget=vaPosition+normal;	
  	 vec3 newNormal=(transformMatrix*vec4(normalTarget,1)).xyz-(transformMatrix*vec4(vaPosition,1)).xyz;
  	 newNormal=normalize(newNormal); 
 	
 	 vec3 eye= normalize(vacameraLoc-(transformMatrix*vec4(vaPosition,1)).xyz);  
 	 vec3 vp = vec3(0.0);
 	 if(lightAmbient==-1.0)//pointlight
 	 {
 		vp = normalize(lightPos-(transformMatrix*vec4(vaPosition,1)).xyz); 
 		vp = normalize(vp);
 		 //应用法线贴图
 		if(enable_normalmap==1)
 	 	{
 	 		eye = normalize(normalmap_rotation*eye);
 	 		vp = normalize(normalmap_rotation*vp); 	
 	 	}
 	 }
 	 else if(lightAmbient==-3.0)//directionallight
 	 {
 	 	vp = normalize(lightPos);  
 	 }
 	
 	 vec3 halfVector=normalize(vp+eye);	 			
 	 float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	
 	 diffuse=lightDiffuse*nDotViewPosition;				
	 float nDotViewHalfVector=dot(newNormal,halfVector);	
	 float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	
	 specular=lightSpecular*powerFactor;    			
}

void spotLight(in vec3 normal,
				inout vec2 strength,
				inout vec4 diffuse,
				inout vec4 specular,
				in float spotExp,
				in vec3 lightPos,
				in float angleCos,
				in vec3 direction,
				in float shininess,//粗糙度
				in vec4 lightDiffuse,
				in vec4 lightSpecular)
{	 			
	vec3 normalTarget=vaPosition+normal;	
  	vec3 newNormal=(transformMatrix*vec4(normalTarget,1)).xyz-(transformMatrix*vec4(vaPosition,1)).xyz;
  	newNormal=normalize(newNormal);
  	vec3 vp = lightPos-(transformMatrix*vec4(vaPosition,1)).xyz; 
 	vec3 eye= vacameraLoc-(transformMatrix*vec4(vaPosition,1)).xyz;  
 	//应用法线贴图
 	if(enable_normalmap==1)
 	{
 	 	eye = normalize(normalmap_rotation*eye);
 	 	vp = normalize(normalmap_rotation*vp);
 	}
 	else
 	{
 		eye = normalize(eye);
 		vp = normalize(vp);
  	}
  	diffuse = vec4(0.0);
	specular = vec4(0.0);
	strength.x = 1.0;
	strength.y = 1.0;
	lowp float ndotl = max(dot(newNormal,vp),0.0);
	if(ndotl>0.0)
	{
		float spotDot = max(dot(-vp,direction),0.0);
		if(spotDot>=angleCos)
		{ 
 	 		vec3 halfVector=normalize(vp+eye);	 			
 	 		float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	
 	 		diffuse=lightDiffuse*nDotViewPosition;				
	 		float nDotViewHalfVector=dot(newNormal,halfVector);	
	 		float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	
	 		specular=lightSpecular*powerFactor;
	 		strength.x = spotDot;
	 		strength.y = spotExp;
	 	}    
	}
}

void applyShadow(inout bool inshadow)
{
   vec4 gytyPosition=lightFinalMatrix * tPosition;
   gytyPosition=gytyPosition/gytyPosition.w;  		
   float s=(gytyPosition.s+1.0)/2.0; 				
   float t=(gytyPosition.t+1.0)/2.0; 
   vec4 depth4=texture2D(shadow_texture, vec2(s,t)); 	
   
   float minDis=depth4.r*256.0*256.0+depth4.g*256.0+depth4.b+depth4.a/32.0;   
   float currDis=distance(tPosition.xyz,shadowLight);			
   if(s>=0.0&&s<=1.0&&t>=0.0&&t<=1.0) { 		
   		if(minDis<=currDis-3.0) {//控制自阴影问题				
   			inshadow = true;
   		} else{
   			inshadow = false;
   		}
   } else{ 
         inshadow = false;
   }
}

void cacuNormalmap(inout vec3 texNormal)
{
	//恢复扰动向量
	vec2 finalTexCoord;
	finalTexCoord.s = (vatexCoord.s+offsetscaleST[0])*offsetscaleST[2];
	finalTexCoord.t = (vatexCoord.t+offsetscaleST[1])*offsetscaleST[3];
	vec4 normalColor = texture2D(texture_normal,finalTexCoord);
	float tmpX,tmpY,tmpZ;
	vec3 tmpVec3;
	tmpX = 2.0*(normalColor.r-0.5);
	tmpY = 2.0*(normalColor.g-0.5);
	tmpZ = 2.0*(normalColor.b-0.5);
	vec3 cNormal = vec3(tmpX,tmpY,tmpZ);
	cNormal = normalize(cNormal);
	texNormal = cNormal;
	//计算变换后的法向量
	vec3 normalTarget=vaPosition+vaNormal;	
  	vec3 newNormal=(transformMatrix*vec4(normalTarget,1)).xyz-(transformMatrix*vec4(vaPosition,1)).xyz;
  	newNormal=normalize(newNormal); 
  	//计算变换后的切向量
  	vec3 tangentTarget=vaPosition+vaTangent;	
  	vec3 newTangent=(transformMatrix*vec4(tangentTarget,1)).xyz-(transformMatrix*vec4(vaPosition,1)).xyz;
  	newTangent=normalize(newTangent); 
  	//计算副法向量
  	tmpVec3 = cross(newTangent,newNormal);
  	vec3 binormal = normalize(tmpVec3);
  	//得到变换矩阵
  	normalmap_rotation = mat3(newTangent,binormal,newNormal);
}

void main()
{
	vec4 ambient,diffuse,specular,emission,tmp1,tmp2,tmp3;
	vec4 ambient_t,diffuse_t,specular_t;
	vec4 tmpColor;
	vec3 in_normal;
	vec2 finalTexCoord;

	if(materialType==1||materialType==2)
		tmpColor = vaColor;
	else if(materialType==3)
	{
		finalTexCoord.s = (vatexCoord.s+offsetscaleST[0])*offsetscaleST[2];
		if(reverseTexT==1)
		{
			finalTexCoord.t = 1.0-(vatexCoord.t+offsetscaleST[1])*offsetscaleST[3];
		}
		else
		{
			finalTexCoord.t = (vatexCoord.t+offsetscaleST[1])*offsetscaleST[3];
		}
		tmpColor = texture2D(texture01,finalTexCoord);
	}
		
	if(lightCount!=0&&lightOff==0)
	{
		for(int i=0,curSpotLightIndex=0;i<lightCount&&i<20;i++,curSpotLightIndex++)
		{			
			float pl_distance = distance(vaPosition,lightPosition[i]);
			//若距离超出衰减范围则不计算
			if((lightAmbient[i]==-1.0||lightAmbient[i]==-4.0)&&pl_distance>lightAttenuationRange[i].y)
				continue;
			//pointLight,spotlight or directionalLight
			if(lightAmbient[i]<0.0)
			{
				//是否在阴影中
				bool in_shadow;
				bool specular_ok = true;;
				if(receiveShadow!=0)
				{
					applyShadow(in_shadow);
					if(in_shadow)
						continue;
				}
				//应用法线贴图则计算变换矩阵
				if(enable_normalmap==1)
 	 			{
 	 				cacuNormalmap(in_normal);
 	 			}
 	 			else
 	 			{
 	 				in_normal = normalize(vaNormal);
 	 			}
				//判断此片元是否应用高光
				if(enable_specularmap==1)
	 			{
					vec4 smap = texture2D(texture_specular,finalTexCoord);
					if(smap.r==0.0&&smap.g==0.0&&smap.b==0.0)
						specular_ok = false;
				}
				tmp2 = lightColor[i] * vamdiffuse;
				tmp3 = lightColor[i] * vamspecular;
				tmp2.a = lightColor[i].a;
				tmp3.a = lightColor[i].a;
				//spotlight
				if(lightAmbient[i] == -4.0)
				{
					
					vec3 dir = vec3(spotlightAngleCosDirection[curSpotLightIndex].y,
					spotlightAngleCosDirection[curSpotLightIndex].z,
					spotlightAngleCosDirection[curSpotLightIndex].w);
					dir = normalize(dir);
					vec2 tmp;
					spotLight(in_normal,tmp,
						diffuse_t,specular_t,
						lightAttenuation[i].z,
						lightPosition[i],
						spotlightAngleCosDirection[curSpotLightIndex].x,
						dir,
						varough,
						tmp2,
						tmp3
					);
					//光强
					float strength = lightStrength[i];
					strength *= pow(tmp.x,tmp.y);
					//聚光灯强度衰减计算
					if(pl_distance>lightAttenuationRange[i].x&&pl_distance<=lightAttenuationRange[i].y)
					{
						strength = strength/(1.0+lightAttenuation[i].x*pl_distance+lightAttenuation[i].y*pl_distance*pl_distance);
					}
					diffuse += diffuse_t*strength;
					if(specular_ok)
						specular += specular_t*strength;
				}
				else
				{
					pointDirectionLight(in_normal,
						diffuse_t,specular_t,
						lightPosition[i],varough,
						lightAmbient[i],
						tmp2,
						tmp3
					);
					//光强
					float strength = lightStrength[i];
					//点光源强度衰减计算
					if(lightAmbient[i]==-1.0&&pl_distance>lightAttenuationRange[i].x&&pl_distance<=lightAttenuationRange[i].y)
					{
						strength = strength/(1.0+lightAttenuation[i].x*pl_distance+lightAttenuation[i].y*pl_distance*pl_distance);
					}
					diffuse += diffuse_t*strength;
					if(specular_ok)
						specular += specular_t*strength;
				}
			}//ambientLight
			else
			{
				ambient_t = lightColor[i] * lightAmbient[i];
				ambient_t.a = lightColor[i].a;
				ambient += ambient_t*lightStrength[i];
			}			
		}
		tmpColor = tmpColor*(ambient+diffuse+specular);
	}
	//应用辐射光
	tmpColor += tmpColor*vamemission;

	gl_FragColor = tmpColor;
}