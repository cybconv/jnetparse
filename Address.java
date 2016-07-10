package net.sf.jnetparse;

public interface Address {
	public String normalize(String str);
	public boolean isValid(String str);
	public int[] getOctets(String str);
	public boolean isMC(String str);
}