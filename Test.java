//package test;

import net.sf.jnetparse.IPv4;
import net.sf.jnetparse.MAC;

import java.io.*;

public class Test {
	private static void ipmac() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		String ip = null;
		String mac = null;

		while (true) {
			System.out.print("Would you like to test MC IP->MAC conversions?: [y/n]");
			System.out.flush();
			line = br.readLine();
			if (line.matches("[nN]*") == true) {
				break;
			}
			System.out.print("IP->MAC conversions:\nIP: ");
			System.out.flush();
			line = br.readLine();
			ip = IPv4.normalize(line);
			mac = IPv4.getMacMC(ip);
			System.out.println("was given:["+line+"] -> norm:["+ip+"]");
			if (IPv4.isMC(ip)) {
				System.out.println(" -> is a MC address. The associated MC MAC address is:"+mac);
				System.out.println(" -> its vendor form is:"+MAC.toVendorForm(mac));
			} else {
				System.out.println(" -> is not a MC address.");
			}
		}
		//try {
		//	br.close();
		//} catch (IOException ignored) {}
	}

	private static void macip() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		String[] ips = null;
		String mac = null;

		while (true) {
			System.out.print("Would you like to test MC MAC->IP conversions?: [y/n]");
			System.out.flush();
			line = br.readLine();
			if (line.matches("[nN]*") == true) {
				break;
			}
			System.out.print("MAC->IP conversions:\n MAC: ");
			System.out.flush();
			line = br.readLine();
			mac = MAC.normalize(line);
			ips = MAC.listMCIPv4(mac);
			System.out.println("mac="+mac);
			if (ips == null) {
				System.out.println(" -> is not a MC MAC address");
			} else {
				for (String ip: ips) {
					System.out.println(ip);
				}
			}
		}
		//try {
		//	br.close();
		//} catch (IOException ignored) {}
	}

	private static void doTestMC() throws IOException {
		ipmac();
		macip();
	}

	public static void main(String[] argv) throws IOException {
		doTestMC();
	}
}