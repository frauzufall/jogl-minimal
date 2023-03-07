if (vis)
{
    if (v.a < 0.95) {
        vec4 x = convert(sampleVolume(wpos));
        v.rgb += (1.0 - v.a) * x.a * x.rgb;
        v.a += (1.0 - v.a) * x.a;
    }
}
