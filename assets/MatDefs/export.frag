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
    float gv_z = float(5.0);
    float gv_A = float(0.5);
    vec2 gv_B = vec2(1.0,0.5);
    float gv_C = float(0.05);
    float gv_D = float(0.05);
    float gv_U = gv_T(gv_y, gv_z, gv_B, gv_A, gv_C, gv_D);
    int gv_l = int(0);
    float gv_m = float(gv_R);
    float gv_p = float(0.1);
    float gv_W = gv_V(gv_l, gv_m, gv_p);
    vec2 gv_g = vec2(gv_S);
    float gv_j = float(5.0);
    float gv_Z = gv_Y(gv_g, gv_j);
    float gv_F = float(0.5);
    float gv_G = float(0.5);
    float gv_H = float(0.5);
    float gv_I = float(1.0);
    vec4 gv_a1 = vec4(gv_F, gv_G, gv_H, gv_I);
    float gv_K = float(0.1);
    float gv_L = float(1.0);
    float gv_M = float(0.3);
    float gv_N = float(1.0);
    vec4 gv_b1 = vec4(gv_K, gv_L, gv_M, gv_N);
    float gv_x = float(gv_U);
    vec4 gv_E = vec4(gv_a1);
    vec4 gv_J = vec4(gv_b1);
    vec4 gv_c1 = mix(gv_E, gv_J, min(max(gv_x, 0.0), 1.0));
    int gv_e = int(1);
    float gv_f = float(gv_Z);
    float gv_k = float(gv_W);
    float gv_e1 = gv_V(gv_e, gv_f, gv_k);
    float gv_r = float(1.0);
    float gv_s = float(0.0);
    float gv_t = float(0.0);
    float gv_u = float(1.0);
    vec4 gv_f1 = vec4(gv_r, gv_s, gv_t, gv_u);
    float gv_d = float(gv_e1);
    vec4 gv_q = vec4(gv_f1);
    vec4 gv_v = vec4(gv_c1);
    vec4 gv_g1 = mix(gv_q, gv_v, min(max(gv_d, 0.0), 1.0));
    vec4 gv_b = vec4(gv_g1);
    float gv_O = float(gv_e1);
    vec4 gv_h1 = vec4(gv_b.rgb, gv_O);
    float gv_P = float(gv_Z);
    float gv_Q = float(gv_R);
    if (gv_P < gv_Q) {
        discard;
    }
    vec4 gv_a = vec4(gv_h1);
    gl_FragColor = gv_a;
}
