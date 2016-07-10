package net.sf.jnetparse.gui;

public class TopicInfo {
	public String id;
	public String name;

	public TopicInfo() {
	}

	public TopicInfo(String id, String name) {
		this.id  = id;
		this.name = name;
	}

	public String toString() {
		return "[" + id + ":" + name + "]";
	}
}