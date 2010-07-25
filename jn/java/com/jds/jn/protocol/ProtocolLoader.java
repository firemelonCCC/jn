package com.jds.jn.protocol;

import org.xml.sax.SAXParseException;

import org.w3c.dom.*;

import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteOrder;

import com.jds.jn.Jn;
import com.jds.jn.classes.CLoader;
import com.jds.jn.network.packets.PacketType;
import com.jds.jn.parser.PartTypeManager;
import com.jds.jn.parser.formattree.*;
import com.jds.jn.parser.packetreader.PacketReader;
import com.jds.jn.parser.valuereader.ValueReader;
import com.jds.jn.protocol.protocoltree.*;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date: 25.09.2009
 * Time: 14:30:14
 */
@SuppressWarnings("unchecked")
public class ProtocolLoader
{
	private static final Logger _log = Logger.getLogger(ProtocolLoader.class);

	public static Protocol restore(File file)
	{
		Protocol protocol = null;
		Document doc = null;

		try
		{
			FileInputStream fis = new FileInputStream(file);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(false);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			try
			{
				doc = docBuilder.parse(fis);
			}
			catch (SAXParseException e)
			{
				report("ERROR", e);
			}
			Node root = doc.getDocumentElement();

			if (!root.getNodeName().equals("protocol"))
			{
				_log.info("Error malformed protocol : root node should be called 'protocol'.");
			}
			protocol = new Protocol(file.getAbsolutePath());

			NamedNodeMap attr = root.getAttributes();
			Node node = attr.getNamedItem("crypt");
			if (node != null)
			{
				protocol.setEncryption(node.getNodeValue());
			}
			else
			{
				protocol.setEncryption("Null");
			}


			node = attr.getNamedItem("name");
			if (node != null)
			{
				protocol.setName(node.getNodeValue());
			}
			else
			{
				return null;
			}

			node = attr.getNamedItem("order");
			if (node != null)
			{
				protocol.setOrder((ByteOrder)ByteOrder.class.getField(node.getNodeValue()).get(null));
			}

			for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("packetfamilly".equalsIgnoreCase(n.getNodeName()))
				{
					PacketFamilly familly = parseFamilly(n);

					if (familly != null)
					{
						protocol.setFamily(familly.getType(), familly);
					}
					else
					{
						_log.info("Error packetfamilly returned is null there was an error");
					}
				}
				else if("macro".equalsIgnoreCase(n.getNodeName()))
				{
					NamedNodeMap map = n.getAttributes();
					String name = map.getNamedItem("id").getNodeValue();
					MacroInfo part = new MacroInfo(name);
					if (parseParts(n, part.getModelBlock()))
					{
						protocol.addMacro(part);
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.info("Exception: " + file.getName(), e);
		}

		return protocol;
	}

	private static PacketFamilly parseFamilly(Node n)
	{
		NamedNodeMap map = n.getAttributes();
		PacketType type = null;

		Node atr = map.getNamedItem("way");
		if (atr == null)
		{
			_log.info("Error, Root packetfamilly don't have 'way'. skipping it");
			return null;
		}
		else
		{
			String way = atr.getNodeValue();

			type = PacketType.valueOf(way);
		}

		if(type == null)
		{
			return null;
		}

		PacketFamilly familly = new PacketFamilly(type);

		for (Node o = n.getFirstChild(); o != null; o = o.getNextSibling())
		{
			if ("packet".equalsIgnoreCase(o.getNodeName()))
			{
				map = o.getAttributes();

				String id = map.getNamedItem("id").getNodeValue();
				String name = map.getNamedItem("name") == null ? "Null Name" : map.getNamedItem("name").getNodeValue();
				boolean isKey = map.getNamedItem("key") != null && Boolean.parseBoolean(map.getNamedItem("key").getNodeValue());
				boolean server_list = map.getNamedItem("server_list") != null && Boolean.parseBoolean(map.getNamedItem("server_list").getNodeValue());
				String reader_c = map.getNamedItem("reader") == null ? null : map.getNamedItem("reader").getNodeValue();
				Class<PacketReader> reader = null;

				if(reader_c != null)
				{
					try
					{
						reader = (Class<PacketReader>) CLoader.getInstance().forName("packet_readers." + reader_c + "Reader");
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
				}

				if(id.startsWith("0x"))
				{
					//TODO remake
					continue;
				}

				PacketInfo format = new PacketInfo(id, name, isKey, server_list, reader);

				boolean b = parseParts(o, format.getDataFormat().getMainBlock());
				if(!b)
				{
					System.out.println("Error after parsing");
				}

				familly.addPacket(format);
			}
		}

		return familly;
	}

	private static boolean parseParts(Node n, PartContainer pc)
	{
		NamedNodeMap attrs;
		Node atr;
		for (Node o = n.getFirstChild(); o != null; o = o.getNextSibling())
		{
			if ("part".equalsIgnoreCase(o.getNodeName()))
			{
				Part pp = parsePart(o, pc);
				if (pp == null)
				{
					return false;
				}
				pc.addPart(pp);
			}
			else if ("for".equalsIgnoreCase(o.getNodeName()))
			{
				attrs = o.getAttributes();
				int forId;
				String name = "";

				atr = attrs.getNamedItem("id");
				if (atr == null)
				{
					_log.info("Error, for doesnt have 'id'. skipping packet");
					return false;
				}
				forId = Integer.parseInt(atr.getNodeValue());

				if (attrs.getNamedItem("name") != null)
				{
					name = attrs.getNamedItem("name").getNodeValue();
				}

				ForPart newForPart = new ForPart(forId);
				newForPart.setName(name);
				newForPart.setParentContainer(pc);
				newForPart.setContainingFormat(pc.getContainingFormat());
				if (parseParts(o, newForPart.getModelBlock()))
				{
					pc.addPart(newForPart);
				}
				else
				{
					return false;
				}
			}
			else if ("macro".equalsIgnoreCase(o.getNodeName()))
			{
				attrs = o.getAttributes();
				atr = attrs.getNamedItem("id");
				if (atr == null)
				{
					_log.info("Error, for doesnt have 'id'. skipping packet");
					return false;
				}
				String id, name;
				id = atr.getNodeValue();

				atr = attrs.getNamedItem("name");
				if (atr == null)
				{
					name = id;
				}
				else
				{
					name = atr.getNodeValue();
				}

				MacroPart part = new MacroPart(id, name);
				part.setParentContainer(pc);
				part.setContainingFormat(pc.getContainingFormat());
				pc.addPart(part);
			}
			else if ("switch".equalsIgnoreCase(o.getNodeName()))
			{
				attrs = o.getAttributes();
				int switchId;
				atr = attrs.getNamedItem("id");

				if (atr == null)
				{
					_log.info("Error, switch doesnt have 'id'. skipping packet");
					return false;
				}
				switchId = Integer.parseInt(atr.getNodeValue());

				SwitchPart newSwitchBlock = new SwitchPart(switchId);
				newSwitchBlock.setParentContainer(pc);
				newSwitchBlock.setContainingFormat(pc.getContainingFormat());
				for (Node caseNode = o.getFirstChild(); caseNode != null; caseNode = caseNode.getNextSibling())
				{
					if ("case".equalsIgnoreCase(caseNode.getNodeName()))
					{
						attrs = caseNode.getAttributes();

						atr = attrs.getNamedItem("id");
						if (atr == null)
						{
							_log.info("Error, case doesnt have 'id'. skipping packet");
							return false;
						}

						String caseName = "";

						if (attrs.getNamedItem("name") != null)
						{
							caseName = attrs.getNamedItem("name").getNodeValue();
						}

						SwitchCaseBlock newSwitchCase;

						if (atr.getNodeValue().equalsIgnoreCase("default"))
						{
							newSwitchCase = new SwitchCaseBlock(newSwitchBlock);
						}
						else
						{
							int caseId;
							try
							{
								caseId = Integer.decode(atr.getNodeValue());
								newSwitchCase = new SwitchCaseBlock(newSwitchBlock, caseId);
							}
							catch (NumberFormatException e)
							{
								Jn.getForm().warn("Warning, case doesnt have a valid 'id'. making it default");
								newSwitchCase = new SwitchCaseBlock(newSwitchBlock);
							}
						}
						newSwitchCase.setName(caseName);
						newSwitchCase.setParentContainer(pc);
						newSwitchCase.setContainingFormat(pc.getContainingFormat());

						if (parseParts(caseNode, newSwitchCase))
						{
							newSwitchBlock.addCase(newSwitchCase);
						}
						else
						{
							return false;
						}
					}
				}
				pc.addPart(newSwitchBlock);
			}
		}
		return true;
	}

	private static Part parsePart(Node n, PartContainer pc)
	{
		String partName;
		NamedNodeMap attrs = n.getAttributes();
		Node atr = attrs.getNamedItem("name");
		if (atr == null)
		{
			Jn.getForm().warn("Warning, part doesnt have 'name'");
			partName = "";
		}
		else
		{
			partName = atr.getNodeValue();
		}

		int partId;
		atr = attrs.getNamedItem("id");
		if (atr == null)
		{
			partId = -1;
		}
		else
		{
			try
			{
				partId = Integer.parseInt(atr.getNodeValue());
			}
			catch (NumberFormatException nfe)
			{
				Jn.getForm().warn("Warning: parts id must be an integer");
				partId = -1;
			}
		}

		atr = attrs.getNamedItem("type");
		if (atr == null)
		{
			Jn.getForm().warn("Error, part doesnt have 'type'. skipping packet");
			return null;
		}
		String type = atr.getNodeValue();

		int size = 0;
		int sizeid = -1;
		boolean dynBSize = false;
		atr = attrs.getNamedItem("size");
		if (atr != null)
		{
			size = Integer.decode(atr.getNodeValue());
		}
		else
		{
			atr = attrs.getNamedItem("sizeid");
			if (atr != null)
			{
				sizeid = Integer.parseInt(atr.getNodeValue());
				dynBSize = true;
			}
		}
		ValueReader r = null;
		for (Node subNode = n.getFirstChild(); subNode != null; subNode = subNode.getNextSibling())
		{

			if ("reader".equals(subNode.getNodeName()))
			{
				if (r != null)
				{
					Jn.getForm().warn("Warning, part '" + (partName) + "' has mutiple readers");
				}
				NamedNodeMap attrs2 = subNode.getAttributes();
				atr = attrs2.getNamedItem("type");
				if (atr == null)
				{
					Jn.getForm().warn("Warning, part '" + (partName) + "' has a reader with no type");
					continue;
				}

				//try default package
				Class<?> clazz = null;
				try
				{
					clazz = CLoader.getInstance().forName("part_readers." + atr.getNodeValue() + "Reader");
				}
				catch (DOMException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}

				if (clazz == null)
				{
					Jn.getForm().warn("Warning, part '" + (partName) + "' reader's could not be found in either parser or custom packages");
					continue;
				}
				try
				{
					r = (ValueReader) clazz.newInstance();
					if (!r.loadReaderFromXML(subNode)) //drop reader if loading went wrong
					{
						r = null;
					}

				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}


		boolean invert;
		atr = attrs.getNamedItem("invert");
		invert = atr != null && Boolean.parseBoolean(atr.getNodeValue());

		Part pp = new Part(PartTypeManager.getInstance().getType(type), partId, partName, invert);

		if (dynBSize)
		{
			pp.setBSizeId(sizeid);
			pp.setDynamicBSize(true);
		}
		else
		{
			pp.setBSize(size);
			pp.setDynamicBSize(false);
		}

		pp.setReader(r);
		return pp;
	}


	public static void report(String severity, SAXParseException e)
	{
		Jn.getForm().warn(severity + ": " + e.getMessage() + " (Line " + e.getLineNumber() + ", Column: " + e.getColumnNumber() + ")", e);
	}
}