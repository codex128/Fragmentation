uniform float m_Dissolve;
varying vec2 texCoord;
bool gv_V(int n) {
    return mod(n, 2) < 1;
}
vec4 gv_W(vec2 uv, vec4 c1, vec4 c2, float s) {
    uv *= s;
    bool x = gv_V(int(floor(uv.x)));
    bool y = gv_V(int(floor(uv.y)));
    if (x == y) {
        return c1;
    }
    else {
        return c2;
    }
}
float gv_L(int f, float a, float b) {
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
float gv_N(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(14.3423, 76.8593))) * 41852.5463133);
}
float gv_O(vec2 uv, float s) {
    uv *= s;
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    float a = gv_N(i);
    float b = gv_N(i + vec2(1.0, 0.0));
    float c = gv_N(i + vec2(0.0, 1.0));
    float d = gv_N(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) + (c - a)* u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
void main() {
    float gv_J = float(m_Dissolve);
    vec2 gv_K = vec2(texCoord);
    int gv_k = int(0);
    float gv_l = float(gv_J);
    float gv_o = float(0.5);
    float gv_M = gv_L(gv_k, gv_l, gv_o);
    vec2 gv_f = vec2(gv_K);
    float gv_i = float(5.0);
    float gv_P = gv_O(gv_f, gv_i);
    float gv_C = float(0.0);
    float gv_D = float(0.7);
    float gv_E = float(0.5);
    float gv_F = float(1.0);
    vec4 gv_Q = vec4(gv_C, gv_D, gv_E, gv_F);
    float gv_x = float(0.0);
    float gv_y = float(1.0);
    float gv_z = float(0.0);
    float gv_A = float(1.0);
    vec4 gv_R = vec4(gv_x, gv_y, gv_z, gv_A);
    int gv_d = int(1);
    float gv_e = float(gv_P);
    float gv_j = float(gv_M);
    float gv_T = gv_L(gv_d, gv_e, gv_j);
    float gv_q = float(1.0);
    float gv_r = float(0.0);
    float gv_s = float(0.0);
    float gv_t = float(1.0);
    vec4 gv_U = vec4(gv_q, gv_r, gv_s, gv_t);
    vec2 gv_v = vec2(gv_K);
    vec4 gv_w = vec4(gv_R);
    vec4 gv_B = vec4(gv_Q);
    float gv_G = float(5.0);
    vec4 gv_X = gv_W(gv_v, gv_w, gv_B, gv_G);
    float gv_c = float(gv_T);
    vec4 gv_p = vec4(gv_U);
    vec4 gv_u = vec4(gv_X);
    vec4 gv_Y = mix(gv_p, gv_u, min(max(gv_c, 0.0), 1.0));
    float gv_H = float(gv_P);
    float gv_I = float(gv_J);
    if (gv_H < gv_I) {
        discard;
    }
    vec4 gv_a = vec4(gv_Y);
    gl_FragColor = gv_a;
}
