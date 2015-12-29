uniform mat4 finalMatrix;
attribute vec3 vPosition;

void main()
{
	gl_Position = finalMatrix * vec4(vPosition,1);
}