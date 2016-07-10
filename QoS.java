package net.sf.jnetparse;

// http://www.cisco.com/en/US/docs/switches/lan/catalyst3560/software/release/12.2_52_se/configuration/guide/swqos.html#wp1030279
// http://www.alliedtelesis.eu/media/fount/how_to_note_alliedware_plus/howto_aw_plus__qos_standard_schemes_RevB.pdf

import java.util.HashMap;

public class QoS {

	private static final String IPP[] = new String[] {
		"Routine", 
		"Priority",
		"Immediate",
		"Flash",
		"Flash Override",
		"Critic[al]",
		"Internetwork Control",
		"Network Control"
		};
	private static final HashMap<String,String> DSCP = new HashMap<String,String>();
	private static final HashMap<String,String> DSCP_INV = new HashMap<String,String>();

	static {
		DSCP.put("none", "000000");
		DSCP.put("default", "000000");
		DSCP.put("cs0", "000000");
		DSCP.put("cs1", "001000");
		DSCP.put("cs2", "020000");
		DSCP.put("cs3", "011000");
		DSCP.put("cs4", "100000");
		DSCP.put("cs5", "101000");
		DSCP.put("cs6", "110000");
		DSCP.put("cs7", "111000");
		DSCP.put("af11", "001010");
		DSCP.put("af12", "001100");
		DSCP.put("af13", "001110");
		DSCP.put("af21", "010010");
		DSCP.put("af22", "010100");
		DSCP.put("af23", "010110");
		DSCP.put("af31", "011010");
		DSCP.put("af32", "011100");
		DSCP.put("af33", "011110");
		DSCP.put("af41", "100010");
		DSCP.put("af42", "100100");
		DSCP.put("af43", "100110");
		DSCP.put("ef", "101110");
		DSCP_INV.put("000000", "cs0");
		DSCP_INV.put("00000", "cs0");
		DSCP_INV.put("0000", "cs0");
		DSCP_INV.put("000", "cs0");
		DSCP_INV.put("00", "cs0");
		DSCP_INV.put("0", "cs0");
		DSCP_INV.put("001000", "cs1");
		DSCP_INV.put("01000", "cs1");
		DSCP_INV.put("1000", "cs1");
		DSCP_INV.put("020000", "cs2");
		DSCP_INV.put("20000", "cs2");
		DSCP_INV.put("011000", "cs3");
		DSCP_INV.put("11000", "cs3");
		DSCP_INV.put("100000", "cs4");
		DSCP_INV.put("101000", "cs5");
		DSCP_INV.put("110000", "cs6");
		DSCP_INV.put("111000", "cs7");
		DSCP_INV.put("001010", "af11");
		DSCP_INV.put("01010", "af11");
		DSCP_INV.put("1010", "af11");
		DSCP_INV.put("001100", "af12");
		DSCP_INV.put("01100", "af12");
		DSCP_INV.put("1100", "af12");
		DSCP_INV.put("001110", "af13");
		DSCP_INV.put("01110", "af13");
		DSCP_INV.put("1110", "af13");
		DSCP_INV.put("010010", "af21");
		DSCP_INV.put("10010", "af21");
		DSCP_INV.put("010100", "af22");
		DSCP_INV.put("10100", "af22");
		DSCP_INV.put("010110", "af23");
		DSCP_INV.put("10110", "af23");
		DSCP_INV.put("011010", "af31");
		DSCP_INV.put("11010", "af31");
		DSCP_INV.put("011100", "af32");
		DSCP_INV.put("11100", "af32");
		DSCP_INV.put("011110", "af33");
		DSCP_INV.put("11110", "af33");
		DSCP_INV.put("100010", "af41");
		DSCP_INV.put("100100", "af42");
		DSCP_INV.put("100110", "af43");
		DSCP_INV.put("101110", "ef");
	};

	public static int getDSCPValue(String name) {
		String v = (name == null? null: DSCP.get(name.trim().toLowerCase()));
		int d = -1;

		if (name == null) {
			return -1;
		}
		try {
			d = Integer.parseInt(v, 2);
		} catch (NumberFormatException nfe) {}
		return d;
	}

	public static String getDSCPName(int dscp) {
		return DSCP_INV.get(Integer.toHexString(dscp));
	}

	public static String getIPPName(int ipp) {
		if ((ipp < 0) || (ipp > 0x7)) {
			return null;
		}
		return QoS.IPP[ipp];
	}

	public static String getCoSName(int cos) {
		return getIPPName(cos);
	}

	public static boolean isToS(int tos) {
		return ((tos > -1) && (tos < 0x100));
	}

	public static boolean isDSCP(int dscp) {
		return ((dscp > -1) && (dscp < 0x40));
	}

	public static boolean isIPP(int ipp) {
		return ((ipp > -1) && (ipp < 0x8));
	}

	public static boolean isCoS(int cos) {
		return ((cos > -1) && (cos < 0x8));
	}

	public static int tos2dscp (int tos) {
		if ((tos < 0) || (tos > 0xFF)) {
			return -1;
		}
		return tos >> 2;
	}

	public static int tos2ipp (int tos) {
		if ((tos < 0) || (tos > 0xFF)) {
			return -1;
		}
		return tos >> 5;
	}

	public static int tos2cos (int tos) {
		if ((tos < 0) || (tos > 0xFF)) {
			return -1;
		}
		return (tos >> 5);
	}

	public static int dscp2tos (int dscp) {
		if ((dscp < 0) || (dscp > 0x3F)) {
			return -1;
		}
		return dscp << 2;
	}

	public static int dscp2ipp (int dscp) {
		if ((dscp < 0) || (dscp > 0x3F)) {
			return -1;
		}
		return dscp >> 3;
	}

	public static int dscp2cos (int dscp) {
		if ((dscp < 0) || (dscp > 0x3F)) {
			return -1;
		}
		return (dscp / 8);
	}

	public static int[] cos2dscp (int cos) {
		int dscp[] = new int[8];

		if ((cos < 0) || (cos > 0x7)) {
			return null;
		}
		for (int i=0; i < 8; i++) {
			if (i == 0) {
				dscp[i] = 8*cos;
			} else {
				dscp[i] = dscp[i-1] + 1;
			}
		}
		return dscp;
	}

	public static int[] cos2tos (int cos) {
		int tos[] = new int[8];

		if ((cos < 0) || (cos > 0x7)) {
			return null;
		}
		tos = cos2dscp(cos);
		for (int i=0; i < 8; i++) {
			tos[i] = tos[i] << 2;
		}
		return tos;
	}

	public static int cos2ipp (int cos) {
		if ((cos < 0) || (cos > 0x7)) {
			return -1;
		}
		return cos;
	}

	public static int ipp2tos (int ipp) {
		if ((ipp < 0) || (ipp > 0x7)) {
			return -1;
		}
		return ipp << 5;
	}

	public static int ipp2dscp (int ipp) {
		if ((ipp < 0) || (ipp > 0x7)) {
			return -1;
		}
		return ipp << 3;
	}

	public static int ipp2cos (int ipp) {
		if ((ipp < 0) || (ipp > 0x7)) {
			return -1;
		}
		return ipp;
	}
}