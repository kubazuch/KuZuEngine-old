#version 150

invariant gl_Position;

attribute vec3 pos;
attribute vec2 texCoord;
attribute vec3 normal;

out vec2 texCoord0;
out vec3 normal0;
out vec3 worldPos0;

uniform mat4 modelMat;
uniform mat4 viewMat;
uniform mat4 projectionMat;

void main() {
    vec4 worldPosition = modelMat * vec4(pos, 1.0);
    gl_Position = projectionMat * viewMat * worldPosition;
    texCoord0 = texCoord;
    normal0 = (modelMat * vec4(normal, 0.0)).xyz;
    worldPos0 = worldPosition.xyz;
}