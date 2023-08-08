varying vec2 texCoord;
uniform float m_Dissolve;
bool gv_R(int n) {
    return mod(n, 2) < 1;
}
vec4 gv_Q(vec2 uv, vec4 c1, vec4 c2, float s) {
    uv *= s;
    bool x = gv_R(int(floor(uv.x)));
    bool y = gv_R(int(floor(uv.y)));
    if (x == y) {
        return c1;
    }
    else {
        return c2;
    }
}
float gv_O(int f, float a, float b) {
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
float gv_I(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(14.3423, 76.8593))) * 41852.5463133);
}
float gv_J(vec2 uv, float s) {
    uv *= s;
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    float a = gv_I(i);
    float b = gv_I(i + vec2(1.0, 0.0));
    float c = gv_I(i + vec2(0.0, 1.0));
    float d = gv_I(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) + (c - a)* u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
void main() {
    vec2 gv_G = vec2(texCoord);
    float gv_H = float(m_Dissolve);
    vec2 gv_f = vec2(gv_G);
    float gv_i = float(5.0);
    float gv_K = gv_J(gv_f, gv_i);
    float gv_z = float(0.0);
    float gv_A = float(0.7);
    float gv_B = float(0.3);
    float gv_C = float(1.0);
    vec4 gv_L = vec4(gv_z, gv_A, gv_B, gv_C);
    float gv_u = float(0.0);
    float gv_v = float(1.0);
    float gv_w = float(0.0);
    float gv_x = float(1.0);
    vec4 gv_M = vec4(gv_u, gv_v, gv_w, gv_x);
    float gv_n = float(1.0);
    float gv_o = float(0.0);
    float gv_p = float(0.0);
    float gv_q = float(1.0);
    vec4 gv_N = vec4(gv_n, gv_o, gv_p, gv_q);
    int gv_d = int(0);
    float gv_e = float(gv_K);
    float gv_j = float(gv_H);
    float gv_P = gv_O(gv_d, gv_e, gv_j);
    vec2 gv_s = vec2(gv_G);
    vec4 gv_t = vec4(gv_M);
    vec4 gv_y = vec4(gv_L);
    float gv_D = float(7.0);
    vec4 gv_S = gv_Q(gv_s, gv_t, gv_y, gv_D);
    float gv_c = float(gv_P);
    vec4 gv_m = vec4(gv_N);
    vec4 gv_r = vec4(gv_S);
    vec4 gv_T = mix(gv_m, gv_r, min(max(gv_c, 0.0), 1.0));
    float gv_E = float(gv_K);
    float gv_F = float(gv_H);
    if (gv_E < gv_F) {
        discard;
    }
    vec4 gv_a = vec4(gv_T);
    gl_FragColor = gv_a;
}
