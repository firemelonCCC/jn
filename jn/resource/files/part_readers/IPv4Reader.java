package part_readers;

import com.jds.jn.Jn;
import com.jds.jn.parser.datatree.ValuePart;
import com.jds.jn.parser.valuereader.ValueReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;

/**
 * @author Ulysses R. Ribeiro
 */
public class IPv4Reader implements ValueReader
{

	public <T extends Enum<T>> T getEnum(ValuePart part)
	{
		return null;
	}

	public boolean loadReaderFromXML(Node n)
	{
		return true;
	}

	public String read(ValuePart part)
	{
		if (part.getBytesSize() == 4)
		{
			byte[] ip = part.getBytes();
			return (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF) + "." + (ip[3] & 0xFF);
		}
		Jn.getForm().warn("IPv4 ValueReader requires a part with exactly 4 bytes. Faulty part : " + part.getModelPart().getName());
		return "";
	}

	public JComponent readToComponent(ValuePart part)
	{
		return new JLabel(this.read(part));
	}

	public boolean saveReaderToXML(Element element, Document doc)
	{
		return true;
	}

	public boolean supportsEnum()
	{
		return false;
	}

}
