uniform mat4 finalMatrix;
uniform mat4 transformMatrix;
attribute vec3 vPosition;
varying vec4 taPosition;

void main()
{
	gl_Position = finalMatrix*transformMatrix*vec4(vPosition,1);
	taPosition = transformMatrix*vec4(vPosition,1.0);
}