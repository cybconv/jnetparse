package net.sf.jnetparse;

public class MAC {//implements Address {
	public static String toString(byte[] bytes) {
		StringBuffer buf = new StringBuffer();

		if (isValid(bytes) == false) {
			return null;
		}
		for (int i=0; i < bytes.length; i++) {
			if (bytes[i] < 0x10) {
				buf.append('0');
			}
			buf.append(Integer.toHexString(bytes[i]));
		}
		return buf.toString();		
	}

	public static String toString(int[] bytes) {
		StringBuffer buf = new StringBuffer();

		if (isValid(bytes) == false) {
			return null;
		}
		for (int i=0; i < bytes.length; i++) {
			if (bytes[i] < 0x10) {
				buf.append('0');
			}
			buf.append(Integer.toHexString(bytes[i]));
		}
		return buf.toString();
	}

	public static boolean isValid(byte[] bytes) {
		return (bytes != null) &&
			(bytes.length == 6) &&
			(bytes[0] > -1) && (bytes[0] < 256) &&
			(bytes[1] > -1) && (bytes[1] < 256) &&
			(bytes[2] > -1) && (bytes[2] < 256) &&
			(bytes[3] > -1) && (bytes[3] < 256) &&
			(bytes[4] > -1) && (bytes[4] < 256) &&
			(bytes[5] > -1) && (bytes[5] < 256);
	}

	public static boolean isValid(int[] bytes) {
		return (bytes != null) &&
			(bytes.length == 6) &&
			(bytes[0] > -1) && (bytes[0] < 256) &&
			(bytes[1] > -1) && (bytes[1] < 256) &&
			(bytes[2] > -1) && (bytes[2] < 256) &&
			(bytes[3] > -1) && (bytes[3] < 256) &&
			(bytes[4] > -1) && (bytes[4] < 256) &&
			(bytes[5] > -1) && (bytes[5] < 256);
	}

	// MAC @ format looks like: XXXX.YYYY.ZZZZ
	// where XXXX, YYYY & ZZZZ are hexadecimal strings that 
	// does allows leading 0s according to whether 'strict' is true of false
	// NOTE THAT some vendors like CISCO use this FORMAT is matched when strict is set to 'true'
	public static boolean isVendorForm(String str, boolean strict) {
		String s = null;
		boolean vendor = (str != null);

		if (vendor) {
			s = str.trim().toLowerCase();
			vendor = s.matches("^([0-9a-f]{1,4}\\.){2}[0-9a-f]{1,4}$");
			if (vendor && strict) {
				for (String t : s.split("\\.")) {
					if (strict && (t.length() < 4)) {
						vendor = false;
						break;
					}
				}
			}
		}
		return vendor;
	}

	public static boolean isVendorForm(String str) {
		return isVendorForm(str, true);
	}

	public static String toVendorForm(String str) {
		StringBuffer buf = null;
		String s = normalize(str);
		boolean ok = (s != null) && (s.length() == 12);

		if (ok) {
			buf = new StringBuffer();
			for (int i=0; i<3; i++) {
				buf.append(s.substring(4*i, 4*(i+1))).append('.');
			}
			buf.deleteCharAt(buf.length()-1);
		}
		return (buf == null? null: buf.toString());
	}

	public static String vendorToNorm(String str) {
		String s = (str == null? null: str.trim().toLowerCase());
		boolean vendor = s.matches("([0-9a-f]{4}\\.){2}[0-9a-f]{4}");
		StringBuffer buf = null;
		int len;
		int w;

		if (vendor) {
			buf = new StringBuffer();
			for (String t : s.split("\\.")) {
				buf.append(t);
			}
		}
		return (buf == null? null: buf.toString());
	}

	public static String normalize(String str) {
		boolean ok = isValid(str);
		String s = (ok == false? null:
			str.trim().toLowerCase().replaceAll("\\p{Punct}", ""));

		if ((s != null) && (s.length() != 12)) {
			s = null;
		}
		return s;
	}

	public static boolean isValid(String str) {
		String n = (str == null? null: 
			str.trim().toLowerCase().replaceAll("\\p{Punct}", ""));

		return (n != null) && n.matches("[0-9a-f]{12}");
	}

	public static int[] getOctets(String str) {
		int[] octets = null;
		String s = normalize(str);

		if (s != null) {
			octets = new int[6];
			for (int i=0; i < 6; i++) {
				octets[i] = Integer.parseInt(s.substring(2*i, 2*(i+1)), 16);
			}
		}
		return octets;
	}

	public static boolean isHSRP(String str) {
		String s = normalize(str);
		boolean ok = (s != null) && s.matches("^00:00:0c:07:ac:[0-9a-f]{2}$");

		return ok;
	}

	public static boolean isVRRP(String str) {
		String s = normalize(str);
		boolean ok = (s != null) && s.matches("^00:00:5e:00:01:[0-9a-f]{2}$");

		return ok;
	}

	public static boolean isGLBP(String str) {
		String s = normalize(str);
		boolean ok = (s != null) && 
			s.matches("^00:07:b4:00:[0-9a-f]{2}:[0-9a-f]{2}$");

		return ok;
	}

	public static boolean isMC(String str) {
		String s = normalize(str);
		int[] octets = getOctets(s);
		boolean ok = (octets != null);

		if (ok) {
			ok = (octets[0] % 2 == 1);
		}
		return ok;
	}
}