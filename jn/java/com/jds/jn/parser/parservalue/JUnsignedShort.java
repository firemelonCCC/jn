package com.jds.jn.parser.parservalue;

import javax.swing.*;

import java.awt.*;

import com.jds.jn.statics.ImageStatic;
import com.jds.nio.buffer.NioBuffer;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date:  15:45:57/07.04.2010
 */
public class JUnsignedShort implements ParserValue<Integer>
{
	private static final Color _color = new Color(255, 200, 10);

	@Override
	public Integer getValue(NioBuffer b, Object... arg)
	{
		return b.getUnsignedShort();
	}

	@Override
	public ImageIcon getIcon()
	{
		return ImageStatic.PART_USHORT;
	}

	@Override
	public Color getColor()
	{
		return _color;
	}
}