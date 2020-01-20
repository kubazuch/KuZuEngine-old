#version 330

invariant gl_Position;

attribute vec3 pos;
attribute vec2 texCoord;

out vec2 texCoord0;

uniform mat4 modelMat;
uniform mat4 viewMat;
uniform mat4 projectionMat;

void main() {
    vec4 worldPosition = modelMat * vec4(pos, 1.0);
    gl_Position = projectionMat * viewMat * worldPosition;
    texCoord0 = texCoord;
}