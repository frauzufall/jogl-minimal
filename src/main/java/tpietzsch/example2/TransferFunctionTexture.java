/*-
 * #%L
 * Volume rendering of bdv datasets
 * %%
 * Copyright (C) 2018 - 2021 Tobias Pietzsch
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package tpietzsch.example2;

import tpietzsch.backend.GpuContext;
import tpietzsch.backend.Texture3D;

import java.nio.ByteBuffer;

import static tpietzsch.backend.Texture.InternalFormat.RGBA8UI;

public class TransferFunctionTexture implements Texture3D
{

	private ByteBuffer data;
	private int[] size;
	private boolean pleaseUpdate = false;

	public void init( int[] size )
	{
		this.data = ByteBuffer.allocateDirect(size[0] * size[1] * 4);
		this.size = size;
	}

	public void upload( final GpuContext context )
	{
		context.delete( this );
		context.texSubImage3D( this, 0, 0, 0, texWidth(), texHeight(), texDepth(), data );
	}

	@Override
	public InternalFormat texInternalFormat()
	{
		return RGBA8UI;
	}

	@Override
	public int texWidth()
	{
		return size[ 0 ];
	}

	@Override
	public int texHeight()
	{
		return size[ 1 ];
	}

	@Override
	public int texDepth()
	{
		return 1;
	}

	public ByteBuffer getData() {
		return data;
	}

	@Override
	public MinFilter texMinFilter()
	{
		return MinFilter.NEAREST;
	}

	@Override
	public MagFilter texMagFilter()
	{
		return MagFilter.NEAREST;
	}

	@Override
	public Wrap texWrap()
	{
		return Wrap.CLAMP_TO_EDGE;
	}

	public boolean needsUpdate() {
		return pleaseUpdate;
	}

	public void setPleaseUpdate(boolean pleaseUpdate) {
		this.pleaseUpdate = pleaseUpdate;
	}
}
