package net.sf.jnetparse;

public class NetUtils {
	public static boolean isHostName(String str) {
		// trims all whitespaces on the two edges of the string
		String i_str = (str == null? "": str.trim().toLowerCase());
		boolean any_problem = 
			// check  if it is an empty string
			(i_str.length() == 0) || 
			// check whether whitespaces are present inside the string
			(i_str.split("\\s").length != 1) ||
			// check if any illegal character is present
			i_str.matches(".*[^\\w\\.\\-].*");

		return (!any_problem);
	}

	public static boolean checkHostAddress(String adr) {
		String i_str = (adr == null? "": adr.trim());
		boolean ok = (IPv4.isValid(i_str) || IPv6.isValid(i_str) || 
			isHostName(i_str));

		return ok;
	}

	public static int[] getOctets(String str) {
		if (IPv4.isValid(str)) {
			return IPv4.getOctets(str);
		} else if (IPv6.isValid(str)) {
			return IPv6.getOctets(str);
		} else if (MAC.isValid(str)) {
			return MAC.getOctets(str);
		} else {
			return null;
		}
	}

	public static boolean isMC(String str) {
		return IPv4.isMC(str) || IPv6.isMC(str) || 
			MAC.isMC(str) || 
			ISO.isMC(str);
	}
}