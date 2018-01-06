package Server;

import java.io.Serializable;

public class ServerAttributes implements Serializable {

	private static final long serialVersionUID = 1L;
	String IP = "0.0.0.0", name = "", user_count = "0/100";

	public ServerAttributes(String IP, String name, String user_count) {

		this.IP = IP;
		this.name = name;
		this.user_count = user_count;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser_count() {
		return user_count;
	}

	public void setUser_count(String user_count) {
		this.user_count = user_count;
	}

	public String toString() {
		return "{NAME=" + name + "}{USER_COUNT=" + getUser_count() + "}";
	}
}