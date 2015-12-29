/*
	matrialType
	1:ColorMaterial
	2:SegmentMaterial
	3:TextureMaterial
*/
uniform int materialType;
uniform vec4 uColor;//for ColorMaterial SegmentMaterial

uniform mat4 finalMatrix;
uniform mat4 transformMatrix;
attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vtexCoord;//for TextureMaterial
attribute vec3 tangent;//for normal map
uniform int receiveShadow;
uniform float mdiffuse;//材质漫反射系数
uniform float mspecular;//材质镜面反射系数
uniform float memission;//材质辐射光强度
uniform float rough;//粗糙度
uniform vec3 cameraLoc;

varying vec4 vaColor;
varying vec3 vaPosition;
varying vec3 vaNormal;
varying vec2 vatexCoord;
varying vec3 vaTangent;
varying vec4 tPosition;
varying float vamdiffuse;
varying float vamspecular;
varying float vamemission;
varying float varough;
varying vec3 vacameraLoc;

void main()
{
	gl_Position = finalMatrix * vec4(vPosition,1);
	if(materialType==1||materialType==2)
		vaColor = uColor;
	vaPosition = vPosition;
	vaNormal = vNormal;
	if(materialType==3)
		vatexCoord = vtexCoord;
	if(receiveShadow==1)
		tPosition = transformMatrix*vec4(vPosition,1.0);
	vaTangent = tangent;
	vamdiffuse = mdiffuse;
	vamspecular = mspecular;
	vamemission = memission;
	varough = rough;
	vacameraLoc = cameraLoc;
}