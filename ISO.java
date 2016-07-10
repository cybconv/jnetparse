package net.sf.jnetparse;

public class ISO {//implements Address {
	public static int getLength(String str) {
		return getLength(str, false);
	}

	public static int getLength(String str, boolean strict) {
		int cnt = 0;
		String s = (str == null? null: str.trim().toLowerCase());
		String[] a = (s == null? null: s.split("\\."));

		if (strict) {
			if ((a != null) && (s.equals("") == false)) {
				if (s.matches(".*[^0-9a-f\\.].*")) {
					a = null;
				}
			} else {
				a = null;
			}
		}
		if (a != null) {
			for (String t : a) {
				cnt += t.length();
			}
		}
		return cnt;
	}

	// ISO network addresses are limited to 20 bytes in length
	public static String normalize(String str) {
		String s = (str == null? null: str.trim().toLowerCase());

		if (s.equals("")) {
			s = null;
		} else if (20 < getLength(s)) {
			s = null;
		} else {
			s = s.replaceAll("[-:_]", ".");
			if (s.matches(".*[^0-9a-f\\.].*")) {
				s = null;
			} else if (s.matches("[\\d\\d]\\.")) {
				
			}
		}
		return s;
	}

	public static boolean isValid(String str) {
		return isValid(str, false);
	}

	public static boolean isValid(String str, boolean isis) {
		String s = normalize(str);
		boolean ok = (s != null) && (s.equals("") != false);

		if (isis) {
			ok = s.matches("37\\.[\\d]{4}\\.([0-9a-f]{4}[\\.]?){3}\\d\\d") ||
				s.matches("47\\.[\\d]{4}\\.([0-9a-f]{4}[\\.]?){3}\\d\\d") ||
				s.matches("49\\.[\\d]{4}\\.([0-9a-f]{4}[\\.]?){3}\\d\\d");
		} else {
			// A Complete IS0 6523 NSAP
			// 	47.0005.80123456000089AB001.AABBCCDDEEFF.00
			// A Simple NSAP Address
			// 	49.0001.0000.0000.0001.00
			ok = s.matches("^\\d\\d\\.[\\d]{4}\\.[0-9a-f]{19}\\.[0-9a-f]{12}\\.\\d\\d$") ||
				s.matches("^\\d\\d\\.([\\d]{4})?\\.([0-9a-f]{4}\\.){3}\\d\\d$");
		}
		return ok;
	}

	public static int[] getOctets(String str) {
		String s = normalize(str);
		String tmp;
		int[] octets = null;
		int idx;
		int radix;

		if (s != null) {
			if (s.matches("\\d\\d\\.[\\d]{4}\\.([0-9a-f]{4}[\\.]?){3}\\d\\d")) {
				octets = new int[1 + 2 + 2*3 + 1];
				octets[0] = Integer.parseInt(s.substring(0,2));
				octets[1] = Integer.parseInt(s.substring(2,4));
				octets[2] = Integer.parseInt(s.substring(4,6));
				idx = 0;
				for (String t : s.substring(6).split("\\.")) {
					if (idx < 6) {
						while ((t.length() > 0) && (idx < 6)) {
							for (int i=0; i<2; i++) {
								tmp = t.substring(i+0,i+2);
								if (tmp.matches(".*[^\\d].*")) {
									radix = 16;
								} else {
									radix = 10;
								}
								octets[3+idx] = Integer.parseInt(tmp, radix);
								idx++;
							}
							if (t.length() == 4) {
								break;
							} else {
								t = t.substring(5);
							}
						}
					} else {
						break;
					}
				}
				idx = s.length();
				octets[9] = Integer.parseInt(s.substring(idx-2));
			} else if (s.matches("^\\d\\d\\.[\\d]{4}\\.[0-9a-f]{19}\\.[0-9a-f]{12}\\.\\d\\d$")) {
				octets = new int[1 + 2 + (1+(19/2)) + (12/2) + 1];
				octets[0] = Integer.parseInt(s.substring(0,2));
				octets[1] = Integer.parseInt(s.substring(2,4));
				octets[2] = Integer.parseInt(s.substring(4,6));
				octets[3] = Integer.parseInt(s.substring(6,7));
				idx = 0;
				for (int i=0; i<(18+12)/2; i++) {
					if (i == 18/2) {
						idx = 1;
					}
					octets[4+i] = Integer.parseInt(s.substring(i+idx+7,i+idx+9), 16);
				}
				idx = s.length() - 1;
				octets[19] = Integer.parseInt(s.substring(idx-1));
			} else if (s.matches("^\\d\\d\\.([0-9a-f]{4}\\.){3}\\d\\d$")) {
				octets = new int[1 + 2*3 + 1];
				radix = 10;
				idx = 0;
				for (int i=0; i<8; i++) {
					octets[i] = Integer.parseInt(s.substring(idx, idx+2), radix);
					switch (i) {
						case 0:
							idx = 3;
							radix = 16;
							break;
						case 1:
						case 3:
						case 5:
							idx += 2;
							break;
						case 2:
						case 4:
							idx += 3;
							break;
						case 6:
							idx += 3;
							radix = 10;
							break;
						default:
							break;
					}
				}
			} else if (s.matches("^\\d\\d\\.[\\d]{4}\\.([0-9a-f]{4}\\.){3}\\d\\d$")) {
				octets = new int[1 + 2 + 2*3 + 1];
				radix = 10;
				idx = 0;
				for (int i=0; i<10; i++) {
					octets[i] = Integer.parseInt(s.substring(idx, idx+2), radix);
					switch (i) {
						case 0:
							idx = 3;
							break;
						case 1:
							idx += 2;
							break;
						case 2:
							idx += 3;
							radix = 16;
							break;
						case 3:
						case 5:
						case 7:
							idx += 2;
							break;
						case 4:
						case 6:
							idx += 3;
							break;
						case 8:
							idx += 3;
							radix = 10;
							break;
						default:
							break;
					}
				}
			}
		}
		return octets;
	}

	public static boolean isMC(String str) {
		String s = normalize(str);
		boolean ok = false && (s != null);

		if (ok) {
		}
		return ok;
	}
}