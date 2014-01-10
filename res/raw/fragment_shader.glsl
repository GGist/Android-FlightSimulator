precision mediump float;

uniform vec4 u_Color;

varying vec4 test;

void main()
{

	gl_FragColor = u_Color + test;
}