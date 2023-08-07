varying vec2 texCoord;
bool gv_t(int n) {
    return mod(n, 2) < 1;
}
vec4 gv_s(vec2 uv, vec4 c1, vec4 c2, float s) {
    uv *= s;
    bool x = gv_t(int(floor(uv.x)));
    bool y = gv_t(int(floor(uv.y)));
    if (x == y) {
        return c1;
    }
    else {
        return c2;
    }
}
float gv_q(int f, float a, float b) {
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
float gv_n(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(14.3423, 76.8593))) * 41852.5463133);
}
float gv_o(vec2 uv, float s) {
    uv *= s;
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    float a = gv_n(i);
    float b = gv_n(i + vec2(1.0, 0.0));
    float c = gv_n(i + vec2(0.0, 1.0));
    float d = gv_n(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) + (c - a)* u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
void main() {
    vec2 gv_m = vec2(texCoord);
    vec2 gv_j = vec2(gv_m);
    float gv_k = float(5.0);
    float gv_p = gv_o(gv_j, gv_k);
    int gv_h = int(0);
    float gv_i = float(gv_p);
    float gv_l = float(4.0);
    float gv_r = gv_q(gv_h, gv_i, gv_l);
    vec2 gv_b = vec2(gv_m);
    vec4 gv_e = vec4(0.03,0.03,0.03,1.0);
    vec4 gv_f = vec4(1.0,1.0,1.0,1.0);
    float gv_g = float(gv_r);
    vec4 gv_u = gv_s(gv_b, gv_e, gv_f, gv_g);
    vec4 gv_a = vec4(gv_u);
    gl_FragColor = gv_a;
}
