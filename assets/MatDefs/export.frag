#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/PBR.glsllib"
#import "Common/ShaderLib/Parallax.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"
uniform vec4 g_LightData[NB_LIGHTS];
uniform vec3 g_CameraPosition;
varying vec3 wPosition;
varying vec3 wNormal;
varying vec4 wTangent;
vec4 gv_u(vec4 d, float m, float s, float r, vec4 n) {
    vec3 viewDir = normalize(g_CameraPosition - wPosition);    
    vec4 output = vec4(0.0);
    vec4 albedo = vec4(d);
    float alpha = albedo.a;
    float Metallic = float(m);
    float Roughness = float(r);
    vec3 norm = normalize(wNormal);
    vec3 normal = vec3(0.0);
    if (n.x != 0.0 || n.y != 0.0 || n.z != 0.0 || n.w != 0.0) {
        vec3 tan = normalize(wTangent.xyz);
        mat3 tbnMat = mat3(tan, wTangent.w * cross( (norm), (tan)), norm);
        float normalType = -1.0; // 1.0=OpenGl, -1.0=DirectX
        vec4 normalHeight = vec4(n);
        normal = normalize((normalHeight.xyz * vec3(2.0, normalType * 2.0, 2.0) - vec3(1.0, normalType * 1.0, 1.0)));
        normal = normalize(tbnMat * normal);
    }
    else {
        normal = norm;
    }
    vec4 specularColor = vec4(1.0, 1.0, 1.0, 1.0);
    specularColor *= s;
    vec4 diffuseColor = albedo;// * (1.0 - max(max(specularColor.r, specularColor.g), specularColor.b));
    vec3 fZero = specularColor.xyz;
    float ndotv = max( dot( normal, viewDir ),0.0);
    for (int i = 0; i < NB_LIGHTS; i += 3) {
        vec4 lightColor = g_LightData[i];
        vec4 lightData1 = g_LightData[i+1];
        vec4 lightDir;
        vec3 lightVec;
        lightComputeDir(wPosition, lightColor.w, lightData1, lightDir, lightVec);
        float fallOff = 1.0;
        #if __VERSION__ >= 110
        if(lightColor.w > 1.0){
        #endif
            fallOff =  computeSpotFalloff(g_LightData[i+2], lightVec);
        #if __VERSION__ >= 110
        }
        #endif
        fallOff *= lightDir.w;
        lightDir.xyz = normalize(lightDir.xyz);            
        vec3 directDiffuse;
        vec3 directSpecular;
        float hdotv = PBR_ComputeDirectLight(normal, lightDir.xyz, viewDir,
                            lightColor.rgb, fZero, Roughness, ndotv,
                            directDiffuse,  directSpecular);
        vec3 directLighting = diffuseColor.rgb *directDiffuse + directSpecular;
        output.rgb += directLighting * fallOff;
    }
    output.a = alpha;
    return output;
}
void main() {
    float gv_r = float(1.0);
    float gv_f = float(gv_r);
    float gv_i = float(gv_r);
    float gv_s = float(gv_f+gv_i);
    vec4 gv_t = vec4(gv_s, 0.0, 0.0, 1.0);
    vec4 gv_v = gv_u(gv_t, 0.5, 0.5, 0.5, vec4(0.0,0.0,0.0,0.0));
    gl_FragColor = gv_v;
}
