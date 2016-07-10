package net.sf.jnetparse.util;

import java.util.GregorianCalendar;
import java.util.Calendar;

public class Version implements Comparable {
	//public static final Version initHelper;

	//static {
	//	initHelper = new Version();
	//	initHelper.seal();
	//};

	public static final String ALPHA    = "alpha";
	public static final String BETA     = "beta";
	public static final String RELEASE  = "release";
	public static final String TERMINAL = "terminal";

	// entity name; project, software, etc...
	private String name;

	// format: X.Y.Z
	private String id;

	private boolean mutable;

	private boolean alpha;
	private boolean beta;
	private boolean release;
	private boolean terminal;

	private GregorianCalendar cal;

	public Version() {
		this.name     = null;
		this.id       = null;
		this.mutable  = true;
		this.alpha    = false;
		this.beta     = false;
		this.release  = false;
		this.terminal = false;
		this.cal      = null;
	}

	public Version(String name, String id) {
		this(name, id, false, false, true, false, false);
	}

	public Version(String name, String id, boolean alpha, boolean beta, boolean release, boolean terminal, boolean mutable) {
		super();
		init_ver(name, id, alpha, beta, release, terminal, mutable);
		this.cal = new GregorianCalendar();
		this.cal.setLenient(false);
	}

	public Version(String name, String id, boolean alpha, boolean beta, boolean release, boolean terminal, 
		int year, int month, int day, boolean mutable) {
		this(name, id, alpha, beta, release, terminal, year, month, day, 0, 0, 0, mutable);
	}

	public Version(String name, String id, boolean alpha, boolean beta, boolean release, boolean terminal, 
		int year, int month, int day, int hour, int minute, int second, boolean mutable) {
		super();
		init_ver(name, id, alpha, beta, release, terminal, year, month, day, hour, minute, second, mutable);
	}

	private void init_ver(String name, String id, boolean alpha, boolean beta, boolean release, boolean terminal, 
		int year, int month, int day, int hour, int minute, int second, boolean mutable) {

		init_ver(name, id, alpha, beta, release, terminal, mutable);
		if (isValidDate(year, month, day) == false) {
			throw new IllegalArgumentException("invalid date - "+
				"year:"+year+
				" month:"+month+
				" day:"+day);
		}
		if (isValidTime(hour, minute, second) == false) {
			throw new IllegalArgumentException("invalid time - "+
				"hour:"+hour+
				" minute:"+minute+
				" second:"+second);
		}
		this.cal = new GregorianCalendar(year, month, day, hour, minute, second);
		this.cal.setLenient(false);
	}


	private void init_ver(String name, String id, boolean alpha, boolean beta, boolean release, boolean terminal, boolean mutable) {
		if (name == null) {
			throw new IllegalArgumentException("name:null");
		}
		if (id == null) {
			throw new IllegalArgumentException("version.id:null");
		}
		this.name = name.trim();
		this.id = id.trim();
		if (this.id.matches("([0-9])+.([0-9])+.([0-9])+") == false) {
			throw new IllegalArgumentException(
				"Wrong format for version: expected X.Y.Z - found:" + 
				this.id);
		}
		if ((alpha && beta) || (alpha && release) || (alpha && terminal) || 
			(beta && release) || (beta && terminal) ||
			(release && terminal)) {
			throw new IllegalArgumentException("Version inconsistent status:" +
				" the status can only be at a time one among the following " +
				"['alpha', 'beta', 'release', 'terminal']...");
		}
		this.alpha    = alpha;
		this.beta     = beta;
		this.release  = release;
		this.terminal = terminal;
		this.mutable  = mutable;
	}

	private boolean isValidDate(int year, int month, int second) {
		GregorianCalendar cal = null;

		try {
			cal = new GregorianCalendar(year, month, second);
			cal.setLenient(false);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.add(Calendar.DAY_OF_MONTH, +1);
			return true;
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace(System.err);
			return false;
		}
	}

	private boolean isValidTime(int hour, int minute, int second) {
		return (hour > -1) && (hour < 24) && 
			(minute > -1) && (minute < 60) &&
			(second > -1) && (second < 60);
	}

	public boolean isComplete() {
		return (name != null) && (id != null) && 
			(alpha || beta || release || terminal) &&
			(cal != null);
	}

	public void setId(String id) {
		if (mutable == false) {
			return;
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public boolean seal() {
		if (mutable == false) {
			return false;
		}
		mutable = false;
		return true;
	}

	@Override
	public String toString() {
		return (name + " -" +
			id + " - " +
			(alpha? ALPHA: "") +
			(beta? BETA: "") +
			(release? RELEASE: "") +
			(terminal? TERMINAL: "") +
			(" - " + cal.get(Calendar.DAY_OF_MONTH) +
				"." + cal.get(Calendar.MONTH) +
				"." + cal.get(Calendar.YEAR)));
	}

	public String verbose() {
		return ("[name="+name+
			" - id="+id+
			" - alpha="+alpha+
			" - beta="+beta+
			" - release="+release+
			" - terminal="+terminal+
			" - mutable="+mutable+
			" - date=" + cal.get(Calendar.DAY_OF_MONTH) +
				"." + cal.get(Calendar.MONTH) +
				"." + cal.get(Calendar.YEAR) +"]");
	}

	public boolean isMutable() {
		return mutable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (mutable == false) {
			return;
		}
		this.name = name;
	}

	public GregorianCalendar getCal() {
		return (GregorianCalendar)(cal.clone());
	}

	public void setCal(int year, int month, int day) {
		setCal(year, month, day, 0, 0, 0);
	}

	public void setCal(int year, int month, int day, int hour, int minute, int second) {
		if (mutable == false) {
			return;
		}
		this.cal = new GregorianCalendar(year, month, day, hour, minute, second);
	}

	public boolean isAlpha() {
		return alpha;
	}

	public void setAlpha(boolean alpha) {
		if (mutable == false) {
			return;
		}
		if (alpha) {
			this.alpha = alpha;
			this.beta = false;
			this.release = false;
			this.terminal = false;
		}
	}

	public boolean isBeta() {
		return beta;
	}

	public void setBeta(boolean beta) {
		if (mutable == false) {
			return;
		}
		if (beta) {
			this.alpha = false;
			this.beta = beta;
			this.release = false;
			this.terminal = false;
		}
	}

	public boolean isRelease() {
		return release;
	}

	public void setRelease(boolean release) {
		if (mutable == false) {
			return;
		}
		if (release) {
			this.alpha = false;
			this.beta = false;
			this.release = release;
			this.terminal = false;
		}
	}

	public boolean isTerminal() {
		return terminal;
	}

	public void setTerminal(boolean terminal) {
		if (mutable == false) {
			return;
		}
		if (terminal) {
			this.alpha = false;
			this.beta = false;
			this.release = false;
			this.terminal = terminal;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Version) {
			Version v = (Version)o;
			return name.equals(v.getName()) && 
				id.equals(v.getId()) && 
				(alpha == v.isAlpha()) && 
				(beta == v.isBeta()) && 
				(release == v.isRelease()) && 
				(terminal == v.isTerminal()) &&
				(mutable == v.isMutable());
		}
		return false;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Version) {
			return do_compare((Version)o, false);
		}
		throw new ClassCastException("Type mismatch:"+o);
	}

	public int compareIgnoreNames(Object o) {
		if (o instanceof Version) {
			return do_compare((Version)o, true);
		}
		throw new ClassCastException("Type mismatch:"+o);	
	}

	private int do_compare(Version v, boolean ignore_names) {
		String[] str = id.split("\\.");
		String[] n_str;
		int[] fields = new int[] {
			Integer.parseInt(str[0]),
			Integer.parseInt(str[1]),
			Integer.parseInt(str[2])
			};
		int[] n_fields;
		int[] diff = new int[3];

		if ((ignore_names == false) && (name.equals(v.getName()) == false)) {
			throw new ClassCastException("trying to compare two differents names: '"+
				name + "' and '" + v.getName()+"'");
		}
		n_str = v.getId().split("\\.");
		n_fields = new int[] {
			Integer.parseInt(n_str[0]),
			Integer.parseInt(n_str[1]),
			Integer.parseInt(n_str[2])
			};
		diff[0] = fields[0] - n_fields[0];
		diff[1] = fields[1] - n_fields[1];
		diff[2] = fields[2] - n_fields[2];
		if (diff[0] != 0) {
			return diff[0];
		}
		if (diff[1] != 0) {
			return diff[1];
		}
		return diff[2];
	}
}