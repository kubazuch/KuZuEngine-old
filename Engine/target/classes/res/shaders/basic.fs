#version 400 core

in vec2 texCoordOut;
//in vec4 col;
out vec4 color;

uniform sampler2D sampler;

void main() {
    color = texture2D(sampler, texCoordOut);
//    color = col;
}