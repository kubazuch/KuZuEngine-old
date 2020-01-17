#version 150

in vec2 texCoordOut;
in vec3 normalOut;

out vec4 fragColor;

uniform vec3 baseColor;
uniform vec3 ambientLight;
uniform sampler2D sampler;

struct BaseLight {
    vec3 color;
    float intensity;
};

struct DirectionalLight {
    BaseLight base;
    vec3 direction;
};

uniform DirectionalLight directionalLight1;
uniform DirectionalLight directionalLight2;

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal) {
    float diffuseFactor = dot(normal, -direction);
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    if (diffuseFactor > 0) {
        diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;
    }

    return diffuseColor;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal) {
    return calcLight(directionalLight.base, -directionalLight.direction, normal);
}

void main() {
    vec4 totalLight = vec4(ambientLight, 1);
    vec4 color = vec4(baseColor, 1);
    vec4 textureColor = texture(sampler, texCoordOut);

    if (textureColor != vec4(0, 0, 0, 0))
    color *= textureColor;

    vec3 normal = normalize(normalOut);

    totalLight += calcDirectionalLight(directionalLight1, normal);
    totalLight += calcDirectionalLight(directionalLight2, normal);

    fragColor = color * totalLight;
}