package net.cloudslides.app.model;

public class ChatData {

	public String type;
	public String data;
	@Override
	public String toString() {
		return "{type: " + type +
                ", data: " + data + "}";
	}
}
