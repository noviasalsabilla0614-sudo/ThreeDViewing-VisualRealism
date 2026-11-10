#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;
in vec4 FragPosLightSpace;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightColor;
uniform vec3 objectColor;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;

uniform bool useTexture;
uniform bool useBlinnPhong;   // Indikator 1: Key 'B'
uniform bool useFlatShading;  // Indikator 2: Key 'F'

float CalculationShadow(vec4 fragPosLightSpace, vec3 normal, vec3 lightDir) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    
    if(projCoords.z > 1.0) return 0.0;
    
    float closestDepth = texture(shadowMap, projCoords.xy).r; 
    float currentDepth = projCoords.z;
    
    // Dynamic Shadow Bias penanganan Shadow Acne (Indikator 5)
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);
    
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r; 
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;        
        }    
    }
    shadow /= 9.0;
    
    return shadow;
}

void main() {
    // Indikator 2: Flat vs Smooth Shading
    vec3 norm = useFlatShading ? normalize(cross(dFdx(FragPos), dFdy(FragPos))) : normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    
    // 1. Ambient
    float ambientStrength = 0.2;
    vec3 ambient = ambientStrength * lightColor;
    
    // 2. Diffuse (Lambertian)
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    
    // 3. Specular (Phong vs Blinn-Phong - Indikator 1)
    float specularStrength = 0.8;
    vec3 viewDir = normalize(viewPos - FragPos);
    float spec = 0.0;
    
    if (useBlinnPhong) {
        vec3 halfwayDir = normalize(lightDir + viewDir);
        spec = pow(max(dot(norm, halfwayDir), 0.0), 32.0); // Blinn-Phong
    } else {
        vec3 reflectDir = reflect(-lightDir, norm);
        spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.0); // Phong Standard
    }
    vec3 specular = specularStrength * spec * lightColor;
    
    // 4. Shadow Calculation (Indikator 5)
    float shadow = CalculationShadow(FragPosLightSpace, norm, lightDir);
    vec3 baseColor = useTexture ? texture(textureSampler, TexCoords).rgb : objectColor;
    
    vec3 lighting = (ambient + (1.0 - shadow) * (diffuse + specular)) * baseColor;
    FragColor = vec4(lighting, 1.0);
}
