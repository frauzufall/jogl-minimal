uniform vec4 offset;
uniform vec4 scale;
uniform usampler3D transfer_fcn;

vec4 convert( float v )
{
    vec4 converted = offset + scale * v;
    uvec4 val = texture(transfer_fcn, vec3(converted.r,0.0, 0.0));
	return vec4(val.r/255., val.g/255., val.b/255., converted.r);
}
