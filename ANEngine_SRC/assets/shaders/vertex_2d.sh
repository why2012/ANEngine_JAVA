uniform mat4 finalMatrix;
attribute vec3 vPosition;
attribute vec2 vtexCoord;

varying vec2 vatexCoord;

void main()
{
	gl_Position = finalMatrix * vec4(vPosition,1);
	vatexCoord = vtexCoord;
}