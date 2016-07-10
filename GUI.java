package net.sf.jnetparse.gui;
//package net.sf.jsecnet.framework.gui;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.awt.Desktop;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.sf.jnetparse.gui.MetaData;
import net.sf.jnetparse.util.Version;
//import net.sf.jnetparse.util.InitHelper;

/**
 * 
 * 
 * @author Mohammed El-Amine MAHBOUBI
 */
public abstract class GUI extends JFrame {
	// System.getProperty("line.separator"); 
	// or more elegant solution provided by Java 1.5 API
	public static final String NEW_LINE = String.format("%n");

	public static final String GUI_SPLASH = "gui.splash";

	public static final String IMAGES_BASE = "images/";
	public static final String ICONS_BASE = IMAGES_BASE + "icons/";

	public static final String GUI_NAME = "gui.name";
	//public static final String GUI_VERSION = "/config/GUI.xml";
	public static final String GUI_VERSION = "gui.version";
	public static final String GUI_SETTINGS = "gui.settings";

	public static final String GUI_HOME_URL     = "gui.home.url";
	public static final String GUI_HELP_LICENSE = "gui.help.license";

	public static final String UPDATE_ADDRESS = "gui.update.url";

	public static final String TAB_HELP_ABOUT   = "gui.tab.help.about";
	public static final String TAB_HELP_DETAILS = "gui.tab.help.about.details"; 
	public static final String TAB_HELP_LICENSE = "gui.tab.help.license";


	protected Version version;
	protected Version lastVersion;
	protected ResourceBundle resources = null;

	protected Desktop desktop;

	public ImageIcon createImageIcon(String p, boolean isIcon) {
		String path;
		URL imageURL;
		ImageIcon icon;

		if (isIcon) {
			path = "/" + ICONS_BASE + p;
		} else {
			path = p;
		}
		imageURL = getClass().getResource(path);
		icon = (imageURL == null? null: new ImageIcon(imageURL));

		System.out.println("GUI.createImageIcon -> path="+p);
		System.out.println("GUI.createImageIcon -> url="+path);
		System.out.println("GUI.createImageIcon -> imageURL="+imageURL);
		System.out.println("GUI.createImageIcon -> icon="+icon);
		return icon;
	}

	public String getStringResource(String name) {
		return resources.getString(name);
	}

	public InputStream getResourceStream(String name) {
		System.out.println("name="+name);
		System.out.println("resource name="+resources.getString(name));
		System.out.println("stream="+getClass().getResourceAsStream("/"+resources.getString(name)));
		return getClass().getResourceAsStream("/"+resources.getString(name));
	}

	public GUI(String title) {
		this(title, true);
	}

	public GUI(String title, boolean newThread) {
		super(title);
		try {
			resources = ResourceBundle.getBundle("config.GUI");
			//System.out.println(new java.io.File(".").getAbsolutePath());
			//System.out.println(new java.io.File("/").getAbsolutePath());
			//java.util.Iterator<String> it = resources.keySet().iterator();
			//String key;
			//while (it.hasNext()) {
			//	key = it.next();
			//	System.out.println("["+key+"]="+resources.getString(key));
			//}
		} catch (MissingResourceException mre) {
			System.err.println("'config/GUI.properties' not found:");
			mre.printStackTrace(System.err);
			System.exit(1);
		}
		System.out.println("GUI.getStringResource(GUI_VERSION)::"+getStringResource(GUI_VERSION));
		version = MetaData.loadVersion(getClass().
			getResourceAsStream(getStringResource(GUI_VERSION)));
		version.seal();
		checkVersion(newThread);
		initGUI();
	}

	public void checkVersion(boolean newThread) {
		if (newThread) {
			new Thread(new Runnable() {
				@Override
				public void run() {
				do_check_version();
				}}).start();			
		} else {
			do_check_version();
		}
	}

	private void do_check_version() {
		try {
			String adr = resources.getString(UPDATE_ADDRESS);
			URL nmu = new URL(adr);
			InputStream input = nmu.openStream();

			lastVersion = MetaData.loadVersion(input);
			System.out.println("last version from '"+adr+"' is:"+
					lastVersion.verbose());
		} catch (MalformedURLException e) {
			e.printStackTrace(System.err);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private void initGUI() {
		if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            // now enable buttons for actions that are supported.
            //enableSupportedActions();
        } else {
        	desktop = null;
        }
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public Version getVersion() {
		return version;
	}
}