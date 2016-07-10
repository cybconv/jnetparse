package net.sf.jnetparse.gui;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.text.MessageFormat;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// to read XML with StaX API
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

// to write XML with StaX API
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import net.sf.jnetparse.util.Version;

public class MetaData {
	public static final String TAG_VERSION = "version";
	public static final String TAG_NAME    = "name";
	public static final String TAG_ID      = "version-id";
	public static final String TAG_STATUS  = "status";
	public static final String TAG_DATE    = "date";
	public static final String TAG_YEAR    = "year";
	public static final String TAG_MONTH   = "month";
	public static final String TAG_DAY     = "day";

	public static final String ATT_STATE   = "state";

	public static final String STATE_ALPHA    = "alpha";
	public static final String STATE_BETA     = "beta";
	public static final String STATE_RELEASE  = "release";
	public static final String STATE_TERMINAL = "terminal";

	public static final MetaData helper = new MetaData();

	private MetaData() {}

	public static Version loadVersion(InputStream input) {
		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		XMLEventReader eventReader;
		Version v = null;
		ArrayList<Version> list = new ArrayList<Version>();
		int year  = -1;
		int month = -1;
		int day   = -1;
		boolean tag_date = false;
		String state;
		String tag = null;

		try {
			System.out.println("MetaData.loadVersion():: input="+input);
			eventReader = inputFactory.createXMLEventReader(input);
			// Read the XML document
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					tag = startElement.getName().getLocalPart();
					// If we have a item element we create a new item
					if (TAG_VERSION.equals(tag)) {
						v = new Version();
						year = month = day = -1;
						continue;
					}
					if (TAG_NAME.equals(tag)) {
						event = eventReader.nextEvent();
						v.setName(event.asCharacters().getData().trim());
						continue;
					}
					if (TAG_ID.equals(tag)) {
						event = eventReader.nextEvent();
						v.setId(event.asCharacters().getData().trim());
						continue;
					}

					if (TAG_STATUS.equals(tag)) {
						// We read the attributes from this tag and add the date
						// attribute to our object
						Iterator attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = (Attribute)attributes.next();
							if (ATT_STATE.equals(attribute.getName().toString())) {
								state = attribute.getValue();
								if (Version.ALPHA.equals(state)) {
									v.setAlpha(true);
								} else if (Version.BETA.equals(state)) {
									v.setBeta(true);
								} else if (Version.RELEASE.equals(state)) {
									v.setRelease(true);
								} else if (Version.TERMINAL.equals(state)) {
									v.setTerminal(true);
								}
							}
						}
					}

					tag = event.asStartElement().getName().getLocalPart();
					if (event.isStartElement()) {
						if (TAG_DATE.equals(tag)) {
							event = eventReader.nextEvent();
							tag_date = true;
							continue;
						}
					}

					if (TAG_YEAR.equals(tag)) {
						if (tag_date) {
							event = eventReader.nextEvent();
							year = Integer.parseInt(event.asCharacters().getData());
						}
						continue;
					}

					if (TAG_MONTH.equals(tag)) {
						if (tag_date) {
							event = eventReader.nextEvent();
							month = Integer.parseInt(event.asCharacters().getData());
						}
						continue;
					}

					if (TAG_DAY.equals(tag)) {
						if (tag_date) {
							event = eventReader.nextEvent();
							day = Integer.parseInt(event.asCharacters().getData());
						}
						continue;
					}
				}
				// If we reach the end of a 'Version' item element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					tag = endElement.getName().getLocalPart();
					if (TAG_VERSION.equals(tag)) {
						v.seal();
						if (v.isComplete()) {
							list.add(v);
						}
					} else if (TAG_DATE.equals(tag)) {
						v.setCal(year, month, day);
						tag_date = false;
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace(System.err);
			return null;
		}
		//System.out.println(v.verbose());
		//System.out.println(list.get(0).verbose());
		return list.get(0);
	}

	public static boolean storeVersion(OutputStream output, Version v) {
		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// Create XMLEventWriter
		XMLEventWriter eventWriter = null;
		// Create a EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent nl = eventFactory.createDTD("\n");
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		StartElement configStartElement = null;
		HashMap<String, String> hm1 = new HashMap<String,String>();
		HashMap<String, String> hm2 = new HashMap<String,String>();
		GregorianCalendar cal;

		try {
			eventWriter = outputFactory.createXMLEventWriter(output);
			eventWriter.add(startDocument);
			// Create Version open tag
			configStartElement = eventFactory.createStartElement("", "", 
				TAG_VERSION);
			eventWriter.add(configStartElement);
			eventWriter.add(nl);
			// Write the different nodes
			createNode(eventWriter, TAG_NAME, null, v.getName());
			createNode(eventWriter, TAG_ID, null, v.getId());
			if (v.isAlpha()) {
				hm1.put(TAG_STATUS, STATE_ALPHA);
				createNode(eventWriter, TAG_STATUS, hm1, null);
			} else if (v.isBeta()) {
				hm1.put(TAG_STATUS, STATE_BETA);
				createNode(eventWriter, TAG_STATUS, hm1, null);
			} else if (v.isRelease()) {
				hm1.put(TAG_STATUS, STATE_RELEASE);
				createNode(eventWriter, TAG_STATUS, hm1, null);
			} else if (v.isTerminal()) {
				hm1.put(TAG_STATUS, STATE_TERMINAL);
				createNode(eventWriter, TAG_STATUS, hm1, null);
			}
			cal = v.getCal();
			hm2.put(TAG_YEAR, cal.get(Calendar.YEAR)+"");
			hm2.put(TAG_MONTH, cal.get(Calendar.MONTH)+"");
			hm2.put(TAG_DAY, cal.get(Calendar.DAY_OF_MONTH)+"");
			createNode(eventWriter, TAG_DATE, null, hm2);

			// Create Version closing tag
			eventWriter.add(eventFactory.createEndElement("", "", TAG_VERSION));
			eventWriter.add(nl);
			eventWriter.add(eventFactory.createEndDocument());
			eventWriter.close();
		} catch (XMLStreamException xse) {
			xse.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	private static void createNode(XMLEventWriter eventWriter, String name,
		HashMap attrs, 
		Object value) throws XMLStreamException {

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent nl    = eventFactory.createDTD("\n");
		XMLEvent tab   = eventFactory.createDTD("\t");
		XMLEvent space = eventFactory.createDTD(" ");
		// Create Start node
		StartElement sElement = eventFactory.createStartElement("", "", name);
		String attr_n;
		Attribute at = null;

		eventWriter.add(tab);
		eventWriter.add(sElement);
		// Add attributes, if applicable
		if (attrs != null) {
			for (Iterator it = attrs.values().iterator(); it.hasNext(); ) {
				attr_n = it.next().toString();
				at = eventFactory.createAttribute(attr_n, (String)attrs.get(attr_n));
				eventWriter.add(at);
			}
		}
		// Create Content, if applicable
		if (value != null) {
			if (value instanceof String) {
				Characters characters = eventFactory.createCharacters((String)value);
				eventWriter.add(characters);
			} else if ((value instanceof HashMap) && TAG_DATE.equals(name)) {
				HashMap hm = (HashMap)value;
				StartElement selt_y = eventFactory.createStartElement("", "", TAG_YEAR);
				StartElement selt_m = eventFactory.createStartElement("", "", TAG_MONTH);
				StartElement selt_d = eventFactory.createStartElement("", "", TAG_DAY);
				EndElement eelt_y = eventFactory.createEndElement("", "", TAG_YEAR);
				EndElement eelt_m = eventFactory.createEndElement("", "", TAG_MONTH);
				EndElement eelt_d = eventFactory.createEndElement("", "", TAG_DAY);
				Characters char_year  = eventFactory.
					createCharacters(hm.get(TAG_YEAR).toString());
				Characters char_month = eventFactory.
					createCharacters(hm.get(TAG_MONTH).toString());
				Characters char_day   = eventFactory.
					createCharacters(hm.get(TAG_DAY).toString());

				eventWriter.add(nl);
				eventWriter.add(selt_y);
				eventWriter.add(char_year);
				eventWriter.add(eelt_y);
				eventWriter.add(selt_m);
				eventWriter.add(char_month);
				eventWriter.add(eelt_m);
				eventWriter.add(selt_d);
				eventWriter.add(char_day);
				eventWriter.add(eelt_d);
			}
		}
		// Create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(nl);
	}

	public static Properties loadData(String file) {
		return loadData(file, true);
	}

	public static Properties loadData(String file, boolean wrapLines) {
		try {
			return loadData(new FileInputStream(file), wrapLines);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
			return null;
		}
	}

	public static Properties loadData(InputStream input) {
		return loadData(input, true);
	}

	public static Properties loadData(InputStream input, boolean wrapLines) {
		Properties props = null;
		MessageFormat form;
		final String[] nl = new String[] {String.format("%n")};
		Enumeration e;
		String key;

		try {
			props = new Properties();
			props.load(input);
			if (wrapLines) {
				e = props.keys();
				while (e.hasMoreElements()) {
					key = (String)e.nextElement();
					form = new MessageFormat(props.getProperty(key));
					props.setProperty(key, form.format(nl));
				}
				//props.list(System.out);
			}
		} catch (IOException ioe) {
			props = null;
		} catch (NullPointerException npe) {
			npe.printStackTrace(System.err);
			props = null;
		}
		return props;
	}

	public static JTable makeTable(Properties props) {
		final int rows, cols;
		int i = 1;
		final Vector<Vector> rowData; 
		final Vector<String> columnNames;
		String v;
		final AbstractTableModel model;

		if (props == null) {
			return null;
		}
		rowData = new Vector<Vector>();
		columnNames = new Vector<String>();
		// read the columns names
		while (true) {
			v = props.getProperty("title."+i);
			if (v == null) {
				break;
			}
			columnNames.addElement(v);
			i++;
		}
		// then, read the lines values
		cols = columnNames.size();
		i = 1;
		while (true) {
			v = props.getProperty("line."+i+".1");
			if (v == null) {
				break;
			}
			Vector<String> line = new Vector<String>();
			for (int j=0; j < cols; j++) {
				v = props.getProperty("line."+i+"."+(j+1));
				if (v == null) {
					return null;
				}
				line.addElement(v);
			}
			rowData.addElement(line);
			i++;
		}

		rows = rowData.size();
		model = new AbstractTableModel() {
			public String getColumnName(int col) {
				return (String)columnNames.elementAt(col);
			}
			public int getRowCount() { return rows; }
			public int getColumnCount() { return cols; }
			public Object getValueAt(int row, int col) {
				return ((rowData.elementAt(row))).elementAt(col);
			}
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		return new JTable(model);
	}

	public static String[] getNotes(Properties props) {
		int i = 1;
		String v;
		Vector<String> notes = null;
		String result[] = null;
		MessageFormat form;

		if (props == null) {
			return null;
		}
		notes = new Vector<String>();
		while (true) {
			v = props.getProperty("table.meta.note."+i);
			if (v == null) {
				break;
			}
			form = new MessageFormat(v);
			notes.addElement(form.format(new String[] {String.format("%n")}));
			i++;
		}
		if (i == 1) {
			return null;
		}
		result = new String[notes.size()];
		System.arraycopy(notes.toArray(), 0, result, 0, result.length);
		return result;
	}

	// TO-BE-DONE
	public static Properties makeProperties(JTable table) {
		return null;
	}

	public static JTree makeTree(Properties props) {
		//Create the nodes.
		DefaultMutableTreeNode root =
			new DefaultMutableTreeNode(props.getProperty("node.0", "ROOT"));
		final JTree tree = new JTree(root);
		final Vector<String> nodeNames = new Vector<String>();

		createNodes(props, root, "");
		//Create a tree that allows one selection at a time.
		tree.getSelectionModel().setSelectionMode
			(TreeSelectionModel.SINGLE_TREE_SELECTION);

		return tree;
	}

	private static void createNodes(Properties p, DefaultMutableTreeNode top, String id) {
		String key;
		String label;
		String new_id;
		//MessageFormat form;
		//final String nl = String.format("%n");
		int i = 1;

		while (true) {
			new_id = ("".equals(id)? ""+i: id+"."+i);
			key = "node." + new_id;
			label = p.getProperty(key);
			if (label == null) {
				break;
			}
			//form = new MessageFormat(label);
			//p.setProperty(key, (label = form.format(nl).toString()));
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TopicInfo(new_id, label));
			top.add(node);
			if (isLeaf(p, key)) {
				//leaves.addElement(key);
			} else {
				createNodes(p, node, new_id);
			}
			i++;
		}
	}

	private static boolean isLeaf(Properties props, String id) {
		return (props.getProperty(id+".1") == null);
	}
}