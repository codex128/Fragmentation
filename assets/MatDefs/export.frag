#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/PBR.glsllib"
#import "Common/ShaderLib/Parallax.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"
uniform vec4 g_LightData[NB_LIGHTS];
uniform vec3 g_CameraPosition;
varying vec3 wPosition;
varying vec3 wNormal;
varying vec4 wTangent;
varying vec2 texCoord;
bool gv_B(int n) {
    return mod(n, 2) < 1;
}
vec4 gv_C(vec2 uv, vec4 c1, vec4 c2, float s) {
    uv *= s;
    bool x = gv_B(int(floor(uv.x)));
    bool y = gv_B(int(floor(uv.y)));
    if (x == y) {
        return c1;
    }
    else {
        return c2;
    }
}
vec4 gv_L(vec4 d, float m, float s, float r, vec4 n) {
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
float gv_I(int f, float a, float b) {
    if (f == 0) return a+b;
    if (f == 1) return a-b;
    if (f == 2) return a*b;
    if (f == 3) return b == 0 ? 0 : a/b;
    if (f == 4) return abs(a);
    if (f == 5) return fract(a);
    if (f == 6) return floor(a);
    if (f == 7) return ceil(a);
    if (f == 8) return mod(a, b);
    if (f == 9) return pow(a, b);
    if (f == 10) return sqrt(a);
    if (f == 11) return max(a, b);
    if (f == 12) return min(a, b);
}
void main() {
    vec4 gv_y = vec4(0.0, 1.0, 0.0, 0.0);
    vec4 gv_z = vec4(gv_y.rgb, 1.0);
    vec2 gv_A = vec2(texCoord);
    vec2 gv_d = vec2(gv_A);
    vec4 gv_D = gv_C(gv_d, gv_z, gv_y, 7.0).rgba;
    float gv_E = gv_D.x;
    float gv_F = gv_D.y;
    float gv_G = gv_D.z;
    float gv_H = gv_D.w;
    float gv_J = gv_I(1, 1.0, gv_H);
    vec4 gv_K = vec4(gv_D.rgb, 1.0);
    vec4 gv_M = gv_L(gv_K, gv_H, gv_H, gv_J, vec4(0.0,0.0,0.0,0.0));
    gl_FragColor = gv_M;
}
