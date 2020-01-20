#version 330

in vec2 texCoord0;
invariant out vec4 fragColor;

uniform vec3 ambientIntensity;
uniform sampler2D sampler;

void main() {
    fragColor = texture2D(sampler, texCoord0) * vec4(ambientIntensity, 1);
}