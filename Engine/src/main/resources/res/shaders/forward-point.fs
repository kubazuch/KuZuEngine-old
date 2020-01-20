#version 150

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;

invariant out vec4 fragColor;

struct BaseLight {
    vec3 color;
    float intensity;
};

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    BaseLight base;
    Attenuation atten;
    vec3 pos;
    float range;
};

uniform vec3 eyePos;
uniform sampler2D diffuse;

uniform float specularIntensity;
uniform float specularPower;

uniform PointLight pointLight;

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal, vec3 worldPos) {
    float diffuseFactor = dot(normal, -direction);

    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);
    if (diffuseFactor > 0) {
        diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;

        vec3 directionToEye = normalize(eyePos - worldPos);
        //        vec3 reflectDirection = normalize(reflect(direction, normal));
        vec3 halfDirection = normalize(directionToEye - direction);

        //        float specularFactor = dot(directionToEye, reflectDirection);
        float specularFactor = dot(halfDirection, normal);
        specularFactor = pow(specularFactor, specularPower);

        if (specularFactor > 0) {
            specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
        }
    }

    return diffuseColor + specularColor;
}

vec4 calcPointLight(PointLight pointLight, vec3 normal, vec3 worldPos) {
    vec3 lightDirection = worldPos - pointLight.pos;
    float distanceToPoint = length(lightDirection);

    if (distanceToPoint > pointLight.range)
    return vec4(0, 0, 0, 0);

    lightDirection = normalize(lightDirection);

    vec4 color = calcLight(pointLight.base, lightDirection, normal, worldPos);

    float attenuation = pointLight.atten.constant +
    pointLight.atten.linear * distanceToPoint +
    pointLight.atten.exponent * distanceToPoint * distanceToPoint +
    0.00001;

    return color / attenuation;
}

void main() {
    vec4 totalLight = vec4(0, 0, 0, 1);
    vec4 color = texture(diffuse, texCoord0);

    vec3 normal = normalize(normal0);

    totalLight += calcPointLight(pointLight, normal, worldPos0);

    fragColor = color * totalLight;
}