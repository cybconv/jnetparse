package net.sf.jnetparse;

public class IPv4 {//implements Address {
	public static String toString(int a, int b, int c, int d) {
		if ((a < 0) || (a > 255) ||
			(b < 0) || (b > 255) ||
			(b < 0) || (b > 255) ||
			(b < 0) || (b > 255)) {
				return null;
		}
		return a + "." + b + "." + c + "." + d;
	}

	public static String toString(int octets[]) {
		if ((octets == null) || (octets.length != 4)) {
			return null;
		}
		return toString(octets[0], octets[1], octets[2], octets[3]);
	}

	public static String pad(String s, char padding, int length) {
		String r;
		int len = (s == null? 0: s.length());

		if ((length < 1) || (len >= length)) {
			return s;
		}
		r = s;
		for (int i=len; i < length; i++) {
			r = padding + r;
		}
		return r;
	}

	public static boolean isValidMask(String netmask) {
		String nm = (netmask == null? null: netmask.trim());
		int b[] = getOctets(nm);
		int i = 0;
		StringBuffer bin = new StringBuffer();

		if (b == null) {
			return false;
		}
		for (int v: b) {
			bin.append(pad(Integer.toBinaryString(v), '0', 8));
		}
		while (i < 32) {
			if (bin.charAt(i) == '1') {
				i++;
			} else {
				break;
			}
		}
		if (i == 32) {
			return true;
		}
		while (i < 32) {
			if (bin.charAt(i) == '0') {
				i++;
			} else {
				break;
			}
		}
		return (i == 32);
	}

	public static int getMaskLength(String netmask) {
		String nm = (netmask == null? null: netmask.trim());
		int b[] = getOctets(nm);
		int i = 0;
		StringBuffer bin = new StringBuffer();

		if (b == null) {
			return -1;
		}
		for (int v: b) {
			bin.append(pad(Integer.toBinaryString(v), '0', 8));
		}
		while (i < 32) {
			if (bin.charAt(i) == '0') {
				break;
			} else {
				i++;
			}
		}
		if (i == 0) {
			if (bin.indexOf("1") == -1) {
				return 0;
			} else {
				return -1;
			}
		}
		if (bin.indexOf("1", i) == -1) {
			return i;
		} else {
			return -1;
		}
	}

	public static String getNetMask(int maskLength) {
		StringBuffer buf = new StringBuffer();

		if ((maskLength < 0) || (maskLength > 32)) {
			return null;
		}
		for (int i=0; i < maskLength; i++) {
			buf.append('1');
		}
		for (int i=maskLength; i < 32; i++) {
			buf.append('0');
		}
		return toString(Integer.parseInt(buf.substring(0, 8), 2),
			Integer.parseInt(buf.substring(8, 16), 2),
			Integer.parseInt(buf.substring(16, 24), 2),
			Integer.parseInt(buf.substring(24, 32), 2));
	}

	public static String getNetworkAddress(String ip, String mask) {
		int b[] = getOctets(ip);
		int m[] = getOctets(mask);

		if ((b == null) || (isValidMask(mask) == false)) {
			return null;
		}
		return toString(b[0] & m[0],
			b[1] & m[1],
			b[2] & m[2],
			b[3] & m[3]);
	}

	public static String getBroadcastAddress(String ip, String mask) {
		int b[] = getOctets(ip);
		int m[] = getOctets(mask);

		if ((b == null) || (isValidMask(mask) == false)) {
			return null;
		}
		for (int i=0; i < m.length; i++) {
			m[i] = ~m[i] & 0xFF;
		}
		return toString(b[0] | m[0],
			b[1] | m[1],
			b[2] | m[2],
			b[3] | m[3]);
	}

	public static String[] getAddressesRange(String ip, String mask) {
		String na = getNetworkAddress(ip, mask);
		String ba = getBroadcastAddress(ip, mask);
		long dec_na;
		long dec_ba;
		String first;
		String last;

		if ((na == null) || (ba == null) || (ip == null) || (mask == null)) {
			return null;
		}
		try {
			dec_na = Long.parseLong(dotted2decimal(na));
			dec_ba = Long.parseLong(dotted2decimal(ba));
		} catch (NumberFormatException nfe) {
			return null;
		}
		if ((dec_na == 0L) && (dec_ba == 0xFFFFFFFFL)) {
			first = "0.0.0.0";
			last = "255.255.255.255";
		} else if ("255.255.255.254".equals(mask)) {
			first = decimal2dotted(dec_na+"");
			last = decimal2dotted(dec_ba+"");
		} else if ("255.255.255.255".equals(mask)) {
			first = ip;
			last  = ip;
		} else {
			first = decimal2dotted((dec_na+1)+"");
			last = decimal2dotted((dec_ba-1)+"");
		}
		return new String[] {first, last};
	}

	public static String dotted2decimal(String ip) {
		String s = normalize(ip);
		int b[] = getOctets(s);
		long dec = 0;

		if ((s == null) || (b == null)) {
			return null;
		}
		dec = ((long)b[0]) * 0x01000000L + 
			((long)b[1]) * 0x00010000L +
			((long)b[2]) * 0x00000100L +
			(long)b[3];
		return dec+"";
	}

	public static String decimal2dotted(String dec) {
		String s = (dec == null? null: dec.trim());
		long d;
		int b[] = null;
		String res = null;

		if ((s == null) || (s.length() > 10) || (s.matches(".*[^0-9].*"))) {
			return null;
		}

		try {
			d = Long.parseLong(s);
			// 4294967295L represents: 255.255.255.255
			if (d <= 4294967295L) {
				b = new int[4];
				b[0] = (int)(d / 0x01000000L);
				d -= ((long)b[0]) * 0x01000000L;
				b[1] = (int)(d / 0x00010000L);
				d -= ((long)b[1]) * 0x00010000L;
				b[2] = (int)(d / 0x00000100L);
				d -= (int)(b[2] * 0x00000100L);
				b[3] = (int)d;
				res = b[0] + "." + b[1] + "." + b[2] + "." + b[3];
			}
		} catch (NumberFormatException nfe) {
			res = null;
		}
		return res;
	}

	public static String normalize(String str) {
		//String s = (str == null? "": str.trim().replaceAll("[ -:_]", "."));
		String s = (str == null? "": str.trim());

		if (s.split("\\.").length != 4) {
			s = null;
		}
		return s;
	}

	public static boolean isValid(String str) {
		int b = -1;
		int i = 0;
		String i_str = null;
		String[] bytes = null;
		boolean ok;

		try {
			i_str = normalize(str);
			bytes = i_str.split("\\.");
			ok = (bytes.length == 4) &&
				i_str.matches("(\\d{1,3}\\.){3}(\\d){1,3}");
			while (ok && (i < 4)) {
				b = Integer.parseInt(bytes[i]);
				ok = ok && (b > -1) && (b < 256);
				i++;
			}
		} catch (NullPointerException npe) {
			ok = false;
		} catch (NumberFormatException nfe) {
			ok = false;
		}
		return ok;
	}

	public static boolean isPrivateAddress(String str) {
		String i_str = null;
		String[] octets = null;
		int octet1 = -1, octet2 = -1;
		boolean ok = false;

		try {
			i_str = normalize(str);
			ok = isValid(str);
			if (ok) {
				octets = i_str.split("\\.");
				octet1 = Integer.parseInt(octets[0]);
				octet2 = Integer.parseInt(octets[1]);
				ok = (octet1 == 10) || 
					((octet1 == 172) && (octet2 > 15) && (octet2 < 32)) || 
					((octet1 == 192) && (octet2 == 168));
			}
		} catch (NullPointerException npe) {
			ok = false;
		} catch (NumberFormatException nfe) {
			ok = false;
		}
		return ok;
	}

	public static int[] getOctets(String str) {
		String i_str = null;
		String[] os = null;
		int[] octets = null;

		try {
			i_str = normalize(str);
			if (isValid(i_str)) {
				os = i_str.split("\\.");
				octets = new int[4];
				octets[0] = Integer.parseInt(os[0]);
				octets[1] = Integer.parseInt(os[1]);
				octets[2] = Integer.parseInt(os[2]);
				octets[3] = Integer.parseInt(os[3]);
			}
		} catch (NullPointerException npe) {
			octets = null;
		} catch (NumberFormatException nfe) {
			octets = null;
		}
		return octets;
	}

	public static boolean isMC(String str) {
		int[] octets = getOctets(str);
		return (octets != null) && (octets[0] > 223) && 
			(octets[0] < 240);
	}

	public static String getMacMC(String str) {
		String t = null;
		String tmp;
		String s = normalize(str);
		int[] octets = getOctets(s);
		boolean ok = isMC(s) && (octets != null);
		// 01:00:5e:xx:xx:xx

		if (ok) {
			tmp = Integer.toString(octets[1] % 128, 16);
			if (tmp.length() == 1) {
				tmp = "0" + tmp;
			}
			t = "01:00:5e:" + tmp + ":" +
				(octets[2] < 0x10? "0": "") + Integer.toHexString(octets[2]) +
				(octets[3] < 0x10? ":0": ":") + Integer.toHexString(octets[3]);
		}
		return t;
	}

	public static String[] listMCIPv4(String str) {
		String[] list = null;
		String s = MAC.normalize(str);
		int[] octets = MAC.getOctets(s);
		boolean ok = (octets != null) && MAC.isMC(s) && 
			(octets[0] == 0x01) &&
			(octets[1] == 0x00) &&
			(octets[2] == 0x5E);
		int b0;
		int b1;

		if (ok) {
			final int seed1 = (octets[1] % 128);
			list = new String[32];
			for (int i = 0; i < 32; i++) {
				b0 = 0xE0 + (i / 2);
				b1 = (128 * (i % 2) + octets[3]) % 256;
				list[i] = b0 + "." + b1 + "." + octets[4] + "." + octets[5];
			}
		}
		return list;
	}
}