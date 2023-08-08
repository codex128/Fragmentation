uniform float m_Dissolve;
varying vec2 texCoord;
float gv_T(vec2 uv, float s, vec2 d, float off, float mw, float trans) {
    uv *= s;
    if (mod(int(floor(uv.y)), 2) < 1) {
    }
    vec2 f = fract(uv);
    float w1 = smoothstep(mw, mw + trans, f.x);
    float w2 = 1 - smoothstep(1.0 - mw - trans, 1.0 - mw, f.x);
    float w3 = smoothstep(mw, mw + trans, f.y);
    float w4 = 1 - smoothstep(1.0 - mw - trans, 1.0 - mw, f.y);
    return min(min(w1, w2), min(w3, w4));
}
float gv_V(int f, float a, float b) {
    if (f == 0) return a+b;
    if (f == 1) return a-b;
    if (f == 2) return a*b;
    if (f == 3) return b == 0 ? 0 : a/b;
    if (f == 4) return abs(a);
    if (f == 5) return fract(a);
    if (f == 6) return ceil(a);
    if (f == 7) return mod(a, b);
    if (f == 8) return pow(a, b);
    if (f == 9) return sqrt(a);
    if (f == 10) return max(a, b);
    if (f == 11) return min(a, b);
}
float gv_X(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(14.3423, 76.8593))) * 41852.5463133);
}
float gv_Y(vec2 uv, float s) {
    uv *= s;
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    float a = gv_X(i);
    float b = gv_X(i + vec2(1.0, 0.0));
    float c = gv_X(i + vec2(0.0, 1.0));
    float d = gv_X(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) + (c - a)* u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
void main() {
    float gv_R = float(m_Dissolve);
    vec2 gv_S = vec2(texCoord);
    vec2 gv_y = vec2(gv_S);
    float gv_U = gv_T(gv_y, 5.0, vec2(1.0,0.5), 0.5, 0.05, 0.05);
    float gv_W = gv_V(0, gv_R, 0.1);
    vec2 gv_g = vec2(gv_S);
    float gv_Z = gv_Y(gv_g, 5.0);
    vec4 gv_a1 = vec4(0.5, 0.5, 0.5, 1.0);
    vec4 gv_b1 = vec4(0.1, 1.0, 0.3, 1.0);
    vec4 gv_c1 = mix(gv_a1, gv_b1, min(max(gv_U, 0.0), 1.0));
    float gv_e1 = gv_V(1, gv_Z, gv_W);
    vec4 gv_f1 = vec4(1.0, 0.0, 0.0, 1.0);
    vec4 gv_g1 = mix(gv_f1, gv_c1, min(max(gv_e1, 0.0), 1.0));
    vec4 gv_h1 = vec4(gv_g1.rgb, gv_e1);
    if (gv_Z < gv_R) {
        discard;
    }
    gl_FragColor = gv_h1;
}
