 precision mediump float;
 
 uniform sampler2D texture01;
 uniform vec4 offsetscaleST;
 
 varying vec2 vatexCoord;
 
 void main()
 {
 	vec2 finalTexCoord;
 	finalTexCoord.s = (vatexCoord.s+offsetscaleST[0])*offsetscaleST[2];
	finalTexCoord.t = (vatexCoord.t+offsetscaleST[1])*offsetscaleST[3];
	gl_FragColor = texture2D(texture01,finalTexCoord);
 }