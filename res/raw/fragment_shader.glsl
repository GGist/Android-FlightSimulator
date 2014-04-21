precision mediump float;

uniform vec4 u_Color;

varying float height;

void main()
{
	gl_FragColor = vec4(0.0, 255.0 * (height / 25.0), 0.0, 0.0);
}