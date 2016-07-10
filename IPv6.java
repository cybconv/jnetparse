package net.sf.jnetparse;

public class IPv6 {//implements Address {
	public static boolean isSSMAndEmbeddedRPAddress(String adr) {
		int bytes[];

		if (isValid(adr) == false) {
			return false;
		}
		bytes = getOctets(adr);
		return (bytes[0] == 0xFF) &&
			((bytes[1] & 0xF0) == 0x30);
	}

	public static String getEmbeddedRPAddress(String ssmAdr) {
		if (isSSMAndEmbeddedRPAddress(ssmAdr) == false) {
			return null;
		}
		return null;
	}

	public static boolean isValidMask(String netmask) {
		String nm = (netmask == null? null: netmask.trim());
		int b[] = getOctets(nm);
		int first, last;
		StringBuffer bin = new StringBuffer();

		if (b == null) {
			return false;
		}
		for (int v: b) {
			bin.append(IPv4.pad(Integer.toBinaryString(v), '0', 8));
		}
		first = bin.indexOf("1");
		last  = bin.lastIndexOf("1", 1);
		if (first > 0) {
			return false;
		}
		if (first == -1) {
			return (last == -1);
		}
		if (last == -1) {
			return true;
		}
		return (bin.substring(first, last).indexOf('0') == -1) &&
			(bin.indexOf("1", last+1) == -1);
	}

	public static int getMaskLength(String netmask) {
		String nm = (netmask == null? null: netmask.trim());
		int b[] = getOctets(nm);
		int i = 0;
		final int len;
		StringBuffer bin = new StringBuffer();

		if (b == null) {
			return -1;
		}
		for (int v: b) {
			bin.append(IPv4.pad(Integer.toBinaryString(v), '0', 8));
		}
		len = b.length * 8;
		while (i < len) {
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
		StringBuffer m = new StringBuffer();
		int b;

		if ((maskLength < 0) || (maskLength > 128)) {
			return null;
		}
		for (int i=0; i < maskLength; i++) {
			buf.append('1');
		}
		for (int i=maskLength; i < 128; i++) {
			buf.append('0');
		}
		for (int i=0; i < 16; i++) {
			b = Integer.parseInt(buf.substring(8*i, 8*(i+1)), 2);
			m.append(Integer.toHexString(b));
			if (i % 2 == 1) {
				m.append(":");
			}
		}
		return m.substring(0, m.length());
	}

		public static String getNetworkAddress(String ip, String mask) {
		int b[] = getOctets(ip);
		int m[] = getOctets(mask);

		if ((b == null) || (isValidMask(mask) == false)) {
			return null;
		}
		return null;
	}

	public static String getBroadcastAddress(String ip, String mask) {
		int b[] = getOctets(ip);
		int m[] = getOctets(mask);

		if ((b == null) || (isValidMask(mask) == false)) {
			return null;
		}
		for (int i=0; i < m.length; i++) {
			m[i] = ~m[i];
		}
		return (b[0] | m[0]) + "." +
			(b[1] | m[1]) + "." +
			(b[2] | m[2]) + "." +
			(b[3] | m[3]);
	}

	public static String[] getAddressesRanges(String ip, String mask) {
		return null;
	}

	public static String toString(int octets[]) {		
		int b;
		String elt;
		StringBuffer ipv6 = new StringBuffer();

		if ((octets == null) || (octets.length != 16)) {
			return null;
		}
		for (int i=0; i < 8; i++) {
			elt = Integer.toHexString(octets[2*i]);
			b = octets[2*i+1];
			if (b < 0x10) {
				elt = elt + "0" + Integer.toHexString(b);
			} else {
				elt = elt + Integer.toHexString(b);
			}
			ipv6.append(Integer.toHexString(Integer.parseInt(elt,16)));
			if (i < 7) {
				ipv6.append(":");
			}
		}
		return ipv6.toString();
	}

	public static String normalize(String str) {
		String s = (isValid(str)? str.trim().toLowerCase(): null);
		StringBuffer norm = new StringBuffer();
		int octets[] = getOctets(s);

		if ((s == null) || (octets == null)) {
			return null;
		}
		for (int i=0; i<16; i++) {
			norm.append(Integer.toHexString(octets[i]));
			if (i < 15) {
				norm.append(":");
			}
		}
		return norm.toString();
	}

	public static boolean isValidEUI64(String eui64) {
		String e = (eui64 == null? null: eui64.trim().toLowerCase());
		String es[] = (e == null? null: e.split(":"));

		return (es != null) && 
			e.matches("([0-9a-f]{1,4}:){3}[0-9a-f]{1,4}");
	}

	public static int[] getEUI64Bytes(String eui64) {
		int octets[] = null;
		int i = 0;
		String e = null;
		String es[] = null;

		if (isValidEUI64(eui64) == false) {
			return null;
		}
		e = eui64.trim().toLowerCase();
		es = e.split(":");
		octets = new int[8];
		for (String t: es) {
			switch (t.length()) {
				case 1:
					t = "000" + t;
					break;
				case 2:
					t = "00" + t;
					break;
				case 3:
					t = "0" + t;
					break;
				default:
					break;
			}
			octets[i++] = Integer.parseInt(t.substring(0,2), 16);
			octets[i++] = Integer.parseInt(t.substring(2), 16);
		}
		return octets;
	}

	public static boolean isValid(String str) {
		String s = (str == null? null: str.trim().toLowerCase());
		String a, b;
		String bs[];
		boolean syntax_error = (s == null) || 
			s.matches(".*[^0-9a-f:\\.].*") ||
			s.matches(".*:::.*") ||
			s.matches(".*::.*::.*") ||
			s.matches(".*[^:]:$") ||
			s.matches("^:[^:].*");
		boolean ok = false;
		int idx = -1;
		int cpt_a = 0, cpt_b = 0;
		int ii = 0;

		if (syntax_error) {
			return false;
		}
		idx = s.indexOf("::");
		if (idx != -1) {
			a = s.substring(0, idx);
			cpt_a = a.split(":").length;
			try {
				b = s.substring(idx+2);
				ok = (a.matches("([0-9a-f]{0,4}:){0,6}[0-9a-f]{0,4}") &&
					(b.matches("([0-9a-f]{0,4}:){0,6}[0-9a-f]{0,4}") || "".equals(b))) ||
					(s.matches("::ffff:([0-9]{1,3}\\.){3}[0-9]{1,3}") ||
					s.matches("::([0-9]{1,3}\\.){3}[0-9]{1,3}"));
				bs = b.split(":");
				cpt_b = bs.length;
				for (String t: bs) {
					if (IPv4.isValid(t)) {
						if (ii != bs.length - 1) {
							ok = false;
							break;
						} else {
							ok = true;
						}
					} else {
						ii++;
					}
				}
			} catch (IndexOutOfBoundsException ioobe) {
				// s ends with "::"
				b = null;
				ok = a.matches("([0-9a-f]{0,4}:){0,6}[0-9a-f]{0,4}");
			}
			return ok && (cpt_a + cpt_b < 8);
		}
		return s.matches("([0-9a-f]{1,4}:){7}[0-9a-f]{1,4}");
	}

	public static String getEUI64(String str) {
		StringBuffer eui64 = new StringBuffer();
		int mac[] = MAC.getOctets(str);
		int octets[] = null;
		int b;
		int i = 0;
		String padding;
		String elt = null;

		if (mac == null) {
			return null;
		}
		octets = new int[8];
		// Flip the seventh bit of the first octet of the MAC @
		if ((mac[0] & 0x2) == 0x2) {
			mac[0] = (mac[0] & 0xFD);
		} else {
			mac[0] = (mac[0] | 0x02);
		}
		System.arraycopy(mac, 0, octets, 0, 3);
		octets[3] = 0xFF;
		octets[4] = 0xFE;
		System.arraycopy(mac, 3, octets, 5, 3);

		while (i < 4) {
			elt = Integer.toHexString(octets[2*i]);
			b = octets[2*i+1];
			if (b < 0x10) {
				elt = elt + "0" + Integer.toHexString(b);
			} else {
				elt = elt + Integer.toHexString(b);
			}
			eui64.append(Integer.toHexString(Integer.parseInt(elt,16)));
			if (i++ < 3) {
				eui64.append(":");
			}
		}
		return eui64.toString();
	}

	public static String getLinkLocal(String mac) {
		String linkLocal = null;
		String eui64 = getEUI64(mac);

		if (eui64 != null) {
			linkLocal = "fe80::" + eui64;
		}
		return linkLocal;
	}

	public static String buildIPv6PrefixFromMAC(String prefix, String mac) {
		return buildIPv6PrefixFromEUI64(prefix, getEUI64(mac));
	}

	public static String getPrefix(String str, int maskLength) {
		StringBuffer pfx = new StringBuffer();
		int octets[] = null;
		int lim = (maskLength < 128? maskLength: 128) / 8;
		int rem = (maskLength < 128? maskLength: 0) % 8;
		int mask = 0x1;
		String t;

		if (isValid(str) == false) {
			if (str == null) {
				return null;
			} else {
				str = str.trim().toLowerCase();
			}
			if (isValid(str + "::") == false) {
				return null;
			}
			str = str + "::";
		}
		if (maskLength == 0) {
			return "::";
		} else if (maskLength < 0) {
			return null;
		}
		octets = getOctets(str);
		if (octets == null) {
			return null;
		}
		for (int i=0; i < lim; i += 2) {
			if (i == lim-1) {
				pfx.append(Integer.toHexString(octets[i]));
			} else {
				t = Integer.toHexString(
					(((octets[i] & 0xFF) << 8) | (octets[i+1] & 0xFF)));
				pfx.append(t).append(":");
			}
		}
		if (rem != 0) {
			mask = 0xFF << (8-rem);
			t = Integer.toHexString(octets[lim] & mask);
			pfx.append(":").append(t);
		}
		return pfx.toString();
	}

	/**
	* eui64 format is: XXXX:XXff:feXX:XXXX
	*/
	public static String buildIPv6PrefixFromEUI64(String prefix, String eui64) {
		int octets[] = null;
		int b;
		int e64[] = null;
		StringBuffer ipv6 = new StringBuffer();
		String elt;
		int i = 0;

		if (isValid(prefix) == false) {
			if (prefix == null) {
				return null;
			} else {
				prefix = prefix.trim().toLowerCase();
			}
			if (isValid(prefix + "::") == false) {
				return null;
			} else if (prefix.isEmpty()) {
				return null;
			} else {
				prefix = prefix + "::";
			}
		}
		if (isValidEUI64(eui64) == false) {
			return prefix;
		}
		e64 = getEUI64Bytes(eui64);
		octets = getOctets(prefix);
		System.arraycopy(e64, 0, octets, 8, 8);
		return toString(octets);
	}

	public static String getMacMC(String str) {
		String mac = "33:33:";
		int[] octets = getOctets(str);

		if (octets == null) {
			return null;
		}
		for (int i=0; i < 4; i++) {
			if (octets[12+i] < 0x10) {
				mac += "0";
			}
			mac += Integer.toHexString(octets[12+i]) + ":";
		}
		return mac.substring(0, mac.length()-1);
	}

	// must returns an array which the length is 4*4 = 16 
	public static int[] getOctets(String str) {
		int octets[] = null;
		int bytes[] = null;
		String s = (str == null? null: str.trim().toLowerCase());
		String a, b;
		String ss[], bs[];
		int idx = -1;
		int i = 0;
		int lim;

		if (isValid(str) == false) {
			return null;
		}
		octets = new int[16];
		if (s.equals("::")) {
			while (i < 16) {
				octets[i++] = 0;
			}
			return octets;
		} else if (s.matches("::[0-9a-f]{1,4}")) {
			while (i < 14) {
				octets[i++] = 0;
			}
			switch (s.length()) {
				case 0:
				case 1:
				case 2:
					return null;
				case 3:
				case 4:
					octets[14] = 0;
					octets[15] = Integer.parseInt(s.substring(2), 16);
					break;
				case 5:
					octets[14] = Integer.parseInt(s.substring(2,3), 16);
					octets[15] = Integer.parseInt(s.substring(3), 16);
					break;
				case 6:
					octets[14] = Integer.parseInt(s.substring(2,4), 16);
					octets[15] = Integer.parseInt(s.substring(4), 16);
					break;
				default:
					return null;
			}
			return octets;
		}
		idx = s.indexOf("::");
		if (idx == -1) {
			ss = s.split(":");
			if (s.matches("([0-9a-f]{1,4}:){4}0{1,4}:5efe:([0-9{1,3}]\\.){3}[0-9]{1,3}")) {
				idx = s.lastIndexOf(":5efe:");
				if (idx == -1) {
					return null;
				}
				for (String t: s.substring(idx).split(":")) {
					switch (t.length()) {
						case 1:
							t = "000" + t;
							break;
						case 2:
							t = "00" + t;
							break;
						case 3:
							t = "0" + t;
							break;
						default:
							// we have reached the IPv4 address
							t = null;
							break;
					}
					if (t == null) {
						break;
					}
					octets[i++] = Integer.parseInt(t.substring(0, 2), 16);
					octets[i++] = Integer.parseInt(t.substring(2), 16);
				}
				octets[i++] = 0x5E;
				octets[i++] = 0xFE;
				bytes = IPv4.getOctets(s.substring(idx+6));
				if (bytes == null) {
					return null;
				}
				octets[10] = 0x5E;
				octets[11] = 0xFE;
				System.arraycopy(bytes, 0, octets, 12, 4);
				return octets;
			} else if (s.matches("(0{1,4}:){5}ffff:([0-9{1,3}]\\.){3}[0-9]{1,3}")) {
				idx = s.lastIndexOf(":ffff:");
				if (idx == -1) {
					return null;
				}
				bytes = IPv4.getOctets(s.substring(idx+6));
				if (bytes == null) {
					return null;
				}
				while (i < 10) {
					octets[i++] = 0;
				}
				octets[10] = 0xFF;
				octets[11] = 0xFF;
				System.arraycopy(bytes, 0, octets, 12, 4);
				return octets;
			} else if (s.matches("(0{1,4}:){6}:([0-9{1,3}]\\.){3}[0-9]{1,3}")) {
				while (i < 12) {
					octets[i++] = 0;
				}
				idx = s.lastIndexOf(":");
				if (idx == -1) {
					return null;
				}
				bytes = IPv4.getOctets(s.substring(idx+1));
				if (bytes == null) {
					return null;
				}
				System.arraycopy(bytes, 0, octets, 12, 4);
				return octets;
			} else if (ss.length != 16) {
				return null;
			}
			for (String t: ss) {
				switch (t.length()) {
					case 1:
						t = "000" + t;
						break;
					case 2:
						t = "00" + t;
						break;
					case 3:
						t = "0" + t;
						break;
					default:
						break;
						//return null;
				}
				octets[i++] = Integer.parseInt(t.substring(0,2), 16);
				octets[i++] = Integer.parseInt(t.substring(2), 16);
			}
		} else if (s.matches("::ffff:([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
			b = s.substring(7);
			if (IPv4.isValid(b)) {
				while (i < 0x10) {
					octets[i++] = 0;
				}
				octets[10] = 0xFF;
				octets[11] = 0xFF;
				System.arraycopy(IPv4.getOctets(b), 0, octets, 12, 4);
			} else {
				return null;
			}
		} else if (s.matches("::([0-9]{1,3}\\.){3}[0-9]{1,3}") ||
				s.matches("(0{1,4}:){1,4}([0{0,4}]:){0,1}([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
			b = s.substring(2);
			if (IPv4.isValid(b)) {
				while (i < 12) {
					octets[i++] = 0;
				}
				System.arraycopy(IPv4.getOctets(b), 0, octets, 12, 4);
			} else {
				return null;
			}
		} else if (s.matches("([0-9a-f]{1,4}:){0,4}:{1,2}5efe:([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
			// ISATAP format
			a = s.substring(idx);
			lim = s.lastIndexOf(":5efe:");
			ss = a.split(":");
			if ((lim == -1) || (ss == null)) {
				return null;
			}
			b = s.substring(lim+6);
			if (IPv4.isValid(b) == false) {
				return null;
			}
			bytes = IPv4.getOctets(b);
			if (bytes == null) {
				return null;
			}
			for (String t: ss) {
				switch (t.length()) {
					case 0:
						// we have reached "::"
						t = null;
						break;
					case 1:
					case 2:
						octets[i++] = 0;
						octets[i++] = Integer.parseInt(t, 16);
						break;
					case 3:
						octets[i++] = Integer.parseInt(t.substring(0,1), 16);
						octets[i++] = Integer.parseInt(t.substring(1), 16);
						break;
					case 4:
						octets[i++] = Integer.parseInt(t.substring(0,2), 16);
						octets[i++] = Integer.parseInt(t.substring(2), 16);
						break;
					default:
						return null;
				}
				if (t == null) {
					break;
				}
			}
			if (idx+2 < lim) {
				a = s.substring(idx+2, lim);
				ss = a.split(":");
				for (String t: ss) {
					switch (t.length()) {
						case 1:
						case 2:
							octets[i++] = 0;
							octets[i++] = Integer.parseInt(t, 16);
							break;
						case 3:
							octets[i++] = Integer.parseInt(t.substring(0,1), 16);
							octets[i++] = Integer.parseInt(t.substring(1), 16);
							break;
						case 4:
							octets[i++] = Integer.parseInt(t.substring(0,2), 16);
							octets[i++] = Integer.parseInt(t.substring(2), 16);
							break;
						case 0:
						default:
							return null;
					}
				}
			}
			while (i < 10) {
				octets[i++] = 0;
			}
			octets[10] = 0x5E;
			octets[11] = 0xFE;
			System.arraycopy(bytes, 0, octets, 12, 4);
			return octets;
		} else if (s.matches("([0-9a-f]{1,4}:){1,3}:0{1,4}:5efe:([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
			// ISATAP format
			a = s.substring(idx);
			lim = s.lastIndexOf(":5efe:");
			ss = a.split(":");
			if ((lim == -1) || (ss == null)) {
				return null;
			}
			b = s.substring(lim+6);
			if (IPv4.isValid(b) == false) {
				return null;
			}
			bytes = IPv4.getOctets(b);
			if (bytes == null) {
				return null;
			}
			for (String t: ss) {
				switch (t.length()) {
					case 0:
						return null;
					case 1:
					case 2:
						octets[i++] = 0;
						octets[i++] = Integer.parseInt(t, 16);
						break;
					case 3:
						octets[i++] = Integer.parseInt(t.substring(0,1), 16);
						octets[i++] = Integer.parseInt(t.substring(1), 16);
						break;
					default:
						octets[i++] = Integer.parseInt(t.substring(0,2), 16);
						octets[i++] = Integer.parseInt(t.substring(2), 16);
						break;
				}
			}
			if (idx + 2 < lim) {
				a = s.substring(idx+2, lim);
				ss = a.split(":");
				if (ss == null) {
					return null;
				}
				for (String t: ss) {
					if (i >= 10) {
						return null;
					}
					switch (t.length()) {
						case 1:
						case 2:
							octets[i++] = 0;
							octets[i++] = Integer.parseInt(t, 16);
							break;
						case 3:
							octets[i++] = Integer.parseInt(t.substring(0,1), 16);
							octets[i++] = Integer.parseInt(t.substring(1), 16);
							break;
						case 4:
							octets[i++] = Integer.parseInt(t.substring(0,2), 16);
							octets[i++] = Integer.parseInt(t.substring(2), 16);
							break;
						case 0:
						default:
							return null;
					}
				}
			}
			octets[10] = 0x5E;
			octets[11] = 0xFE;
			System.arraycopy(bytes, 0, octets, 12, 4);
			return octets;
		} else if (s.matches("2002:*:([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
			octets[0] = 0x20;
			octets[1] = 0x02;
			i = 2;
			lim = s.lastIndexOf(":");
			a = s.substring(idx, lim);
			ss = a.split(":");
			b = s.substring(lim+1);
			if (IPv4.isValid(b) == false) {
				return null;
			}
			bytes = IPv4.getOctets(b);
			if (bytes == null) {
				return null;
			}
			if (idx == 4) {
				while (i < 12-2*ss.length) {
					octets[i++] = 0;
				}
				for (String t: ss) {
					switch (t.length()) {
						case 0:
							t = null;
							break;
						case 1:
						case 2:
							octets[i++] = 0;
							octets[i++] = Integer.parseInt(t, 16);
							break;
						case 3:
							octets[i++] = Integer.parseInt(t.substring(0,1), 16);
							octets[i++] = Integer.parseInt(t.substring(1), 16);
							break;
						case 4:
							octets[i++] = Integer.parseInt(t.substring(0,2), 16);
							octets[i++] = Integer.parseInt(t.substring(2), 16);
							break;
						default:
							return null;
					}
					if (t == null) {
						break;
					}
				}
			} else {
				a = s.substring(4, idx);
				ss = a.split(":");
				b = s.substring(idx+2, lim);
				bs = b.split(":");
				for (String t: ss) {
					switch (t.length()) {
						case 0:
							t = null;
							break;
						case 1:
						case 2:
							octets[i++] = 0;
							octets[i++] = Integer.parseInt(t, 16);
							break;
						case 3:
							octets[i++] = Integer.parseInt(t.substring(0,1), 16);
							octets[i++] = Integer.parseInt(t.substring(1), 16);
							break;
						case 4:
							octets[i++] = Integer.parseInt(t.substring(0,2), 16);
							octets[i++] = Integer.parseInt(t.substring(2), 16);
							break;
						default:
							return null;
					}
					if (t == null) {
						break;
					}
				}
				while (i < 12-2*bs.length) {
					octets[i++] = 0;
				}
				for (String t: bs) {
					switch (t.length()) {
						case 0:
							t = null;
							break;
						case 1:
						case 2:
							octets[i++] = 0;
							octets[i++] = Integer.parseInt(t, 16);
							break;
						case 3:
							octets[i++] = Integer.parseInt(t.substring(0,1), 16);
							octets[i++] = Integer.parseInt(t.substring(1), 16);
							break;
						case 4:
							octets[i++] = Integer.parseInt(t.substring(0,2), 16);
							octets[i++] = Integer.parseInt(t.substring(2), 16);
							break;
						default:
							return null;
					}
					if (t == null) {
						break;
					}
				}
			}
			System.arraycopy(bytes, 0, octets, 12, 4);
			return octets;
		} else if (s.startsWith("::")) {
			b = s.substring(idx+2);
			bs = b.split(":");
			lim = 16 - 2*bs.length;
			while (i < lim) {
				octets[i++] = 0;
			}
			for (String t: bs) {
				switch (t.length()) {
					case 1:
						t = "000" + t;
						break;
					case 2:
						t = "00" + t;
						break;
					case 3:
						t = "0" + t;
						break;
					default:
						break;
				}
				octets[i++] = Integer.parseInt(t.substring(0,2), 16);
				octets[i++] = Integer.parseInt(t.substring(2), 16);
			}
		} else {
			a = s.substring(0, idx);
			for (String t: a.split(":")) {
				switch (t.length()) {
					case 1:
						t = "000" + t;
						break;
					case 2:
						t = "00" + t;
						break;
					case 3:
						t = "0" + t;
						break;
					default:
						break;
						//return null;
				}
				octets[i++] = Integer.parseInt(t.substring(0,2), 16);
				octets[i++] = Integer.parseInt(t.substring(2), 16);
			}
			try {
				b = s.substring(idx+2);
				bs = b.split(":");
				lim = 16 - bs.length * 2;
				while (i < lim) {
					octets[i++] = 0;
				}
				for (String t: bs) {
					switch (t.length()) {
						case 1:
							t = "000" + t;
							break;
						case 2:
							t = "00" + t;
							break;
						case 3:
							t = "0" + t;
							break;
						case 4:
							break;
						default:
							break;
							//return null;
					}
					octets[i++] = Integer.parseInt(t.substring(0,2), 16);
					octets[i++] = Integer.parseInt(t.substring(2), 16);
				}
			} catch (IndexOutOfBoundsException ioobe) {
				// string address ends with a "::"
				b = null;
				while (i < octets.length) {
					octets[i++] = 0;
				}
			}
		}
		return octets;
	}

	/**
	* The format of an IPv6 global unicast address: is 2000::/3
	*/
	public static boolean isUnicast(String str) {
		int[] octets = getOctets(str);

		return (octets != null) && ((octets[0] & 0x2) == 0x2);
	}

	/**
	* The format of a IPv6 Link-Local address is: FE80::/10
	*/
	public static boolean isLinkLocal(String str) {
		int[] octets = getOctets(str);

		return (octets != null) && ((octets[0] & 0xFE) == 0xFE) &&
			((octets[1] & 0x80) == 0x80);
	}

	/**
	* The format of an IPv6 MC  address is: FF00::/8
	*/
	public static boolean isMC(String str) {
		int[] octets = getOctets(str);

		return (octets != null) && (octets[0] == 0xFF);
	}

	/**
	* FF02::1:FF00:0/104
	*/
	public static String getSolicitedNodeAddress(String ipv6) {
		int octets[] = getOctets(ipv6);
		StringBuffer solnod = new StringBuffer("ff02::1:ff");

		if (octets == null) {
			return null;
		}
		if (octets[13] < 0x10) {
			solnod.append("0");
		}
		solnod.append(Integer.toHexString(octets[13])).append(":");
		solnod.append(Integer.
			toHexString(((octets[14] & 0xFF) << 8) | (octets[15] & 0xFF)));
		return solnod.toString();
	}

	/**
	* The format of an IPv6 SolicitedNode MC address is: FF02::1:FF00:0/104
	*/
	public static boolean isSolicitedNodeAddress(String str) {
		int[] octets = getOctets(str);

		return (octets != null) && (octets[0] == 0xFF) &&
			(octets[1] == 0x02) && 
			(octets[2] == 0) &&
			(octets[3] == 0) &&
			(octets[4] == 0) &&
			(octets[5] == 0) &&
			(octets[6] == 0) &&
			(octets[7] == 0) &&
			(octets[8] == 1) &&
			(octets[9] == 0xFF) &&
			(octets[10] == 0) &&
			(octets[11] == 0) &&
			(octets[12] == 0);
	}

	/**
	* <64-bit PREFIX>+ 0000:5EFE + <IPv4 of ISATAP link>
	*/
	public static String buildISATAPAddress(String prefix, String ipv4) {
		int octets[] = getOctets(prefix);
		int b4[] = IPv4.getOctets(ipv4);
		String pfx = (prefix == null? null: prefix.trim().toLowerCase());

		if ((pfx == null) || (IPv4.isValid(ipv4) == false)) {
			return null;
		}
		if (octets == null) {
			if (isValid(pfx + "::") == false) {
				return null;
			}
			octets = getOctets(pfx + "::");
			if (octets == null) {
				return null;
			}
		}
		if (b4 == null) {
			return null;
		}
		octets[8] = 0;
		octets[9] = 0;
		octets[10] = 0x5E;
		octets[11] = 0xFE;
		System.arraycopy(b4, 0, octets, 12, 4);
		return toString(octets);
	}

	/**
	* <64-bit PREFIX>+ 0000:5EFE + <IPv4 of ISATAP link>
	*/
	public static boolean isISATAPAddress(String str) {
		int octets[] = getOctets(str);

		if (octets == null) {
			return false;
		}
		return (octets[8] == 0) && (octets[9] == 0) &&
			(octets[10] == 0x5E) && (octets[11] == 0xFE) &&
			(octets[12] > -1) && (octets[12] < 256) &&
			(octets[13] > -1) && (octets[13] < 256) &&
			(octets[14] > -1) && (octets[14] < 256) &&
			(octets[15] > -1) && (octets[15] < 256);
	}

	public static String buildAuto6to4(String ipv4) {
		int octets[] = IPv4.getOctets(ipv4);
		StringBuffer auto = new StringBuffer("2002:");
		int i = 0;

		if (octets == null) {
			return null;
		}
		for (int b: octets) {
			auto.append(Integer.toHexString(b));
			i++;
			if (i == 2) {
				auto.append(":");
			}
		}
		auto.append("::/48");
		return auto.toString();
	}

	public static boolean isAuto6to4(String ipv6) {
		int octets[] = getOctets(ipv6);

		if ((octets == null) || (isValid(ipv6) == false)) {
			return false;
		}
		return (octets[0] == 0x20) &&
			(octets[1] == 0x02) &&
			((octets[2] < 256) && (octets[2] > -1)) &&
			((octets[3] < 256) && (octets[3] > -1)) &&
			((octets[4] < 256) && (octets[4] > -1)) &&
			((octets[5] < 256) && (octets[5] > -1));
	}

	public static String buildIPv4Compatible(String ipv4) {
		return buildFromIPv4(ipv4, "::");
	}

	public static boolean isIPv4Compatible(String ipv6) {
		int octets[] = getOctets(ipv6);

		if (octets == null) {
			return false;
		}
		return (octets[0] == 0) &&
			(octets[1] == 0) &&
			(octets[2] == 0) &&
			(octets[3] == 0) &&
			(octets[4] == 0) &&
			(octets[5] == 0) &&
			(octets[6] == 0) &&
			(octets[7] == 0) &&
			(octets[8] == 0) &&
			(octets[9] == 0) &&
			(octets[10] == 0) &&
			(octets[11] == 0) &&
			((octets[12] < 256) && (octets[12] > -1)) &&
			((octets[13] < 256) && (octets[13] > -1)) &&
			((octets[14] < 256) && (octets[14] > -1)) &&
			((octets[15] < 256) && (octets[15] > -1));
	}

	public static String buildIPv4Mapped(String ipv4) {
		return buildFromIPv4(ipv4, "::ffff:");
	}

	public static boolean isIPv4Mapped(String ipv6) {
		int octets[] = getOctets(ipv6);

		if (octets == null) {
			return false;
		}
		return (octets[0] == 0) &&
			(octets[1] == 0) &&
			(octets[2] == 0) &&
			(octets[3] == 0) &&
			(octets[4] == 0) &&
			(octets[5] == 0) &&
			(octets[6] == 0) &&
			(octets[7] == 0) &&
			(octets[8] == 0) &&
			(octets[9] == 0) &&
			(octets[10] == 0xFF) &&
			(octets[11] == 0xFF) &&
			((octets[12] < 256) && (octets[12] > -1)) &&
			((octets[13] < 256) && (octets[13] > -1)) &&
			((octets[14] < 256) && (octets[14] > -1)) &&
			((octets[15] < 256) && (octets[15] > -1));
	}

	private static String buildFromIPv4(String ipv4, String pfx) {
		int octets[] = IPv4.getOctets(ipv4);
		StringBuffer mapped = new StringBuffer(pfx);

		if (octets == null) {
			return null;
		}
		mapped.append(Integer.toHexString(octets[0])).
			append(Integer.toHexString(octets[1])).
			append(":").
			append(Integer.toHexString(octets[2])).
			append(Integer.toHexString(octets[3]));
		return mapped.toString();
	}
}